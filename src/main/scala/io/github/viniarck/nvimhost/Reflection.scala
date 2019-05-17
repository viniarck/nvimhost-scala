package nvimhost

import scala.reflect.ClassTag
import scala.reflect.runtime.universe
import collection.mutable.ArrayBuffer

/** Object responsible for runtime reflections and compile time parsing plugin methods */
object Reflection {

  /** Perform a runtime reflection call */
  def call(objectName: String, methodName: String, args: Any*) = {
    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    val moduleSymbol = runtimeMirror.moduleSymbol(Class.forName(objectName))

    val targetMethod = moduleSymbol.typeSignature.members
      .filter(x => x.isMethod && x.name.toString == methodName)
      .head
      .asMethod

    val reArgs = """:\s?(\w+(\.\w+)?)""".r
    val matches =
      reArgs.findAllIn(targetMethod.typeSignature.toString).matchData.toList

    if (matches.length != args.length)
      throw new PluginParsingException(
        s"Wrong number of arguments. Expected ${matches.length} args, got ${args.length}"
      )
    for (i <- 0 until matches.length)
      if (!args(i).getClass.toString.contains(matches(i).group(1)))
        throw new PluginParsingException(
          s"Wrong argument types. Expected args: ${matches
            .map(_.group(1))
            .toList}, got args: ${args.map(_.getClass).toList}"
        )

    runtimeMirror
      .reflect(runtimeMirror.reflectModule(moduleSymbol).instance)
      .reflectMethod(targetMethod)(args: _*)
  }

  /** These are the supported args types of Plugin methods. Json was used to provide seamless convertion with msgpack */
  def getValidJsonArgTypes(): Set[String] = {
    Set("ujson.Num", "ujson.Str", "ujson.Bool", "ujson.Arr")
  }

  /** These are the supported ret types of Plugin methods. Json was used to provide seamless convertion with msgpack */
  def getValidJsonRetTypes(): Set[String] = {
    getValidJsonArgTypes() + "Unit"
  }

  /** Validate the whole plugin */
  def validatePluginStructure(objectName: String): Boolean = {
    hasValidName(objectName)
    hasValidSignatures(objectName)
    true
  }

  /** Validate the object name */
  def hasValidName(objectName: String): Boolean = {
    val re = """\w+\.\w+\$""".r
    if (re.findAllIn(objectName).matchData.toList.length == 0) {
      throw new PluginParsingException(
        s"The plugin object name should match this regex: '$re' which is expect to match a Scala Object in a certain package."
      )
    }
    true
  }

  /** Validation all arguments and return types of a Plugin object. Only methods that start with uppercase letters are validate. Uppercase letters were used to keep coherent with Neovim/Vim */
  def hasValidSignatures(objectName: String): Boolean = {
    if (listMethods(objectName).length == 0)
      throw new PluginParsingException(
        "There aren't any methods that start with uppercase letters. Please, create at least one method."
      )
    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    val moduleSymbol = runtimeMirror.moduleSymbol(Class.forName(objectName))

    val methods = moduleSymbol.typeSignature.members
      .filter(_.isMethod)
      .filter(_.name.toString().head >= 'A')
      .filter(_.name.toString().head <= 'Z')
      .map(_.asMethod)
      .map(_.typeSignature.toString())

    val reArgs = """:\s?(\w+(\.\w+)?)""".r
    val reRet = """\(.*\)(\w+(\.\w+)?)""".r
    val validRetTypes: Set[String] = getValidJsonRetTypes()
    val validArgTypes: Set[String] = getValidJsonArgTypes()
    for (item <- methods) {
      for (m <- reRet.findAllIn(item).matchData.toList) {
        if (!validRetTypes.exists(_ == m.group(1)))
          throw new PluginParsingException(
            s"Unsupported return type: ${m.group(1)}. The following types are supported: ${validRetTypes.toString}"
          )
      }
      for (m <- reArgs.findAllIn(item).matchData.toList) {
        if (!validArgTypes.exists(_ == m.group(1)))
          throw new PluginParsingException(
            s"Unsupported arg type: ${m.group(1)}. The following types are supported: ${validArgTypes.toString}"
          )
      }
    }
    true
  }

  /** List all uppercase methods of a Plugin*/
  def listMethods(objectName: String): List[String] = {
    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    val moduleSymbol = runtimeMirror.moduleSymbol(Class.forName(objectName))

    val methods = moduleSymbol.typeSignature.members
      .filter(_.isMethod)
      .filter(_.name.toString().head >= 'A')
      .filter(_.name.toString().head <= 'Z')
      .map(_.asMethod)
      .map(_.name.toString)
    methods.toList
  }

  /**
  Generate plugin manifest, the plugin hosted name is the jar name. Make sure the jar is in your PATH and you souce 'vimFileDstPath'. As soon as the plugin is registered, all the functions will be lazy loaded, and the jar will be executed as soon as your make a first call from Nvim.
    */
  def genPluginFile(
      objectName: String,
      vimFileDstPath: String,
      jarFilePath: String
  ) = {
    import java.io.PrintWriter

    val methods = listMethods(objectName)
    val jarFilePathExpanded = jarFilePath.replace("~", System.getenv("HOME"))
    val jarName = jarFilePathExpanded.split("/").last.replace(".jar", "")
    val manifestExpr = s"""
" This file has been autogenerated by this plugin '${jarFilePathExpanded}'
" Don't edit this file manually

if exists('g:loaded_${jarName}')
  finish
endif
let g:loaded_${jarName} = 1

function! F${jarName}(host)
  if filereadable('${jarFilePathExpanded}') != 1
    echoerr "File '${jarFilePathExpanded}' doesn't exist. Did you assembly this .jar file?"
  endif

  let g:job_${jarName} = jobstart(['scala', '${jarFilePathExpanded}'])
  if g:job_${jarName} == -1
    echoerr "Failed to start job '${jarName}'"
  endif

  " make sure the plugin host is ready and double check rpc channel id
  let g:${jarName}_channel = 0
  for count in range(0, 8000)
    if g:${jarName}_channel != 0
      break
    endif
    sleep 1m
  endfor
  if g:${jarName}_channel == 0
    echoerr "Failed to initialize ${jarName}"
  endif

  return g:${jarName}_channel
endfunction

call remote#host#Register('${jarName}', '.*', function('F${jarName}'))
call remote#host#RegisterPlugin('${jarName}', '${jarName}Plugin', [
""";
    var functionsExpr: String = ""
    for (i <- 0 until methods.length) {
      var syncValue = 1
      if (methods(i).toLowerCase().contains("async")) syncValue = 0
      functionsExpr = functionsExpr + raw"""\ {'type': 'function', 'name': '${methods(i)}', 'sync': ${syncValue}, 'opts': {}},"""
      if (i != methods.length - 1) functionsExpr += '\n'
    }
    val tailExpr = raw"""
\ ])"""
    new PrintWriter(vimFileDstPath.replace("~", System.getenv("HOME"))) {
      try {
        write(manifestExpr + functionsExpr + tailExpr)
      } finally {
        close
      }
    }
  }
}
