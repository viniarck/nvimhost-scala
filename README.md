[![pipeline status](https://gitlab.com/viniarck/nvimhost-scala/badges/master/pipeline.svg)](https://gitlab.com/viniarck/nvimhost-scala/commits/master)![Maven Central](https://img.shields.io/maven-central/v/io.github.viniarck/nvimhost-scala_2.13.svg?style=plastic)

## nvimhost-scala

Neovim (nvim) host plugin provider and API client library in [Scala](https://www.scala-lang.org/).

![nvimhostd-logo](https://lh3.googleusercontent.com/HggrTKoW_oWnzczz5M1svIGj93weIuiGmcB0gz-VwG4Y19QgugFx-aM6vzoGanivdEcwIcdMTbs-fJR-vDibSAZ1c_8phPfW7i56t1P3Sr4yodrqzm8g23q3sPPPODEfve50kXtbZW6TuHU-8RFCk0WKvsNjbTDutU-ZoH3FlkrvQHIofqiTr3p6yPxehhJAgUHyA21tBPUmiSqhyVhOX7g24e5d6lgOBMP9m8H_LtL9q2QcO4k6Fy8MCb5CqljJKQZRvdfZ2Fa7N4euVdh6qWPGWwAy6ZKzeakfFeDBzyHAlhFVB7zWuvSJ_XEHFVxxAjn2uO9ZKzx9rTNcoA7_benzhUCqzpuV1Zou2Tj44DaUgfo5UNJFIFRVmHQX9k5TVHAZKzWv9ZPIgMRXxQm1uQGMxhZKjCRNWvvKorb0RzJbsnNe4-GRD2YMrbb9NFhX8HzYWG8e8XCrzyycTPoxJ5FlfYz-q5lMfbd_h22Uxet3pxMcEDvreJrf1bw0v1IYytGHYmZRM4-cj_gWEcHf1c-UG9zXZSbPaXc_3v5Xwu-kGKMaayU7C8LolcHXmgkG6abr6tO-T0rSJyfueCYFs2K2zTKWw-ar7Qpohf_rg-fBt7lYUT4u7VtlzOd9siKJgFuHdx_2E1Qf34Il0RJcGTVmsvQem53g5ooix404KwCDAzX1qpYNdc8MdMI=w600-h180-no)

## Screencast

![screencast](https://s8.gifyu.com/images/nvimhost-scala.gif)

## Goals

- Provide an API for other projects to integrate with nvim.
- Provide a library for high-performance plugins with Scala static types.
- Pay the JVM startup cost only once (when the plugin is first called).

## Examples

- You can find the API client code on [Api.scala](./src/main/scala/io/github/viniarck/nvimhost/Api.scala), it's fully asynchronous based on `scala.concurrent.Future`
- The plugindemo project can be found on [src/test/Main.scala](src/test/Main.scala). In this example, the plugin has several functions, notice that they have to start with an uppercase letter and receive and/or return `ujson` types:

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

## How to compile with SBT

- In the [CI yml file](./.gitlab-ci.yml) there's a complete example how to compile, source (from Neovim) and run the plugin.
- In the test folder, on [build.sbt](./src/test/build.sbt) you can find a base configuration to start your build.sbt configuration.
