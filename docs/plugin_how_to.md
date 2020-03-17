## How to write a Plugin to run on nvimhost-scala

### Overall IO structure with neovim

nvimhost-scala communicates with neovim's RPC interface over message-pack and using Akka actors. In order for nvimhost-scala to run your Scala plugin, it needs to exists in a scala package that can be imported, just so nvimhost-scala can parse the Plugin file, and generates a .vim file which will register remotely with neovim in order for your Plugin functions to be invoked from neovim.

### DemoPlugin example

In this example, the [DemoPlugin](src/test/Main.scala) has several functions:

```scala
package testnvimhost

import java.net.InetSocketAddress
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Success, Failure, Try}
import scala.concurrent.duration._

import com.typesafe.scalalogging.{LazyLogging}

import nvimhost.NvimTypes._
import nvimhost.Plugin

object DemoPlugin
    extends Plugin(
      new InetSocketAddress("localhost", 7777),
      "testnvimhost.DemoPlugin$",
      "~/demoplugin.vim",
      "~/demoplugin.jar"
    ) {

  // sync function with single argument
  def Greet(name: ujson.Str): ujson.Str = s"Hello ${name.str}"

  // sync function with multiple arguments
  def SumFromUntil(from: ujson.Num, to: ujson.Num): ujson.Num = {
    Range(from.num.toInt, to.num.toInt).sum
  }

  // sync function calling async nvim function
  def SetVarValue(num: ujson.Num): ujson.Num = {
    nvim.command(s"let g:test_var_value = ${num}")
    num
  }

  // sync function that returns true if there's a value > 9
  def HasValueGt9(arr: ujson.Arr): ujson.Bool = {
    val filtered = arr.value
      .filter(_.isInstanceOf[ujson.Num])
      .filter(_.asInstanceOf[ujson.Num].num > 9)
      .map(_ => return true)
    false
  }

  // async function (which is a function that contains 'async' in its name) calling both sync and async nvim functions
  def SetVarValueAsync(num: ujson.Num): Unit = {

    nvim.command(s"let g:test_var_value = ${num}")

    val fut = nvim.command("echomsg 'hello world'")
    Try(Await.result(fut, 1.second)) match {
      case Success(v) => logger.debug(s"${v}")
      case Failure(e) => logger.error(e.getMessage)
    }
  }

}

object MyApp extends App with LazyLogging {
  DemoPlugin.serveForever()
}
```

### Workflow to develop a plugin

- Create a scala package for your plugin, for example `testnvimhost`, check this [DemoPlugin](src/test/Main.scala) if you need a sample, which is being tested in the CI.
- The plugin needs to inherent from `nvimhost.Plugin`. In the constructor, you have to specify these arguments:

```
abstract class Plugin(
    address: InetSocketAddress,
    packageObjectName: String,
    outVimFilePath: String,
    jarFilePath: String
) extends LazyLogging {
```

```
    address -> TCP port neovim is running.

    packageObjectName -> your object package name ("testnvimhost.DemoPlugin$", in the case of the DemoPlugin, note that you have to specify the package name and the class name with a "$" sign at the end).

    outVimFilePath -> the file path where the .vim generated manifest file, which will be bootstraped with neovim "jobstart" and "remote#host#Register" to register the Plugin functions to be invoked from neovim.

    jarFilePath -> where your Plugin fat jar will be placed (you will build with "sbt assembly" and put the jar file in this path later). Neovim will bootstrap the plugin initially by calling this fat jar.
```

- To write your functions, the name of it has to start with an uppercase letter, and any arguments must have the type `ujson.*` and the return type also has to be `ujson.*`.
- Here's a [build.sbt](https://github.com/viniarck/nvimhost-scala/blob/master/src/test/build.sbt) that you should follow. Notice that you have to add the `"io.github.viniarck" %% "nvimhost-scala" % "1.1.0"` dependency, specify the `mainClass` in order to assembly your fat jar.
- To build, `sbt assembly`.
- Copy your plugin from the target folder i.e., `target/scala-2-*` to whichever path you have especified on `jarFilePath` in the constructor of your Plugin object.

If you still need help, there's a complete example running in the CI with this plugin, check [this link](https://github.com/viniarck/nvimhost-scala/blob/master/.gitlab-ci.yml#L24-L36)

### How to run a plugin

1. Once you have assembled your Plugin fat jar. You need to run it once in the shell `scala ~/<yourPluginjarFilePath>.jar`, in order to generate the `outVimFilePath` which will have to be sourced from neovim. You'll have to repeat this process whenever you change or update the functions of your plugin to generate the new vim `outVimFilePath` manifest file.
2. Start neovim with the TCP port your plugin is supposed to use and source your `outVimFilePath` .vim file, `NVIM_LISTEN_ADDRESS=127.0.0.1:7777 nvim -u ~/<outVimFilePath>`.
3. From neovim invoke your function, for example, `: echo Greet("Foo")` (in the case of the demo project). The first time your plugin will be bootstraped with `jobstart`, the next calls you won't have to pay for the JVM startup cost, since the plugin is already running.
4. You should see the output of your function as shown in this screencast:

![screencast](https://s3.gifyu.com/images/ezgif.com-crop7c17fcbf5b8ea8fd.gif)

### Debugging

If you need to debug, you have to export this environment variable:

`NVIM_SCALA_LOG_FILE=/tmp/nvimhost_scala_log.txt`

Also to facilitate, if you want to run neovim always on your plugin port, feel free to export:

`NVIM_LISTEN_ADDRESS=127.0.0.1:7777`

