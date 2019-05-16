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
