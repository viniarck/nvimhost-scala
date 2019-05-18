[![pipeline status](https://gitlab.com/viniarck/nvimhost-scala/badges/master/pipeline.svg)](https://gitlab.com/viniarck/nvimhost-scala/commits/master)![Maven Central](https://img.shields.io/maven-central/v/io.github.viniarck/nvimhost-scala_2.12.svg?style=plastic)

## nvimhost-scala

Neovim (nvim) host plugin provider and API client library in [Scala](https://www.scala-lang.org/).

![nvimhostd-logo](https://lh3.googleusercontent.com/oKUV7qTP4hP4zYDU9uFRKHdruIJRCmFMmCN8ow6v23M3b1o72vAv7TCxgAqdl_Taypp8iYg6T-_-AXF0UBEaZyIGM4y5Xvg31yp7oZg49OEgWRPgZtD3L2SnlM3bUSLW6tB1rp-nYM02qZesnjC0MPL3HeEy_uGlMmLOAgGsTgicITeNyzTaDOmav5tKIB7KFhbmO4fHdrk0E4D7erNZ9lp9LAz8F7kXPCsQ5VGU6ISQQ4pRsgFvUlqTiB8_uAAJ1jl2LvZ_nQcM9WYNdPZk-2O3mlas0LwvktyQ-lTQ_g4Zq7RM4SVp6jPzEOgBor-8bwtCFrGZP1wEdRSsuHEYrTeNDYg6wiv43GVsQsLInWnQVFy8TkqTSb1NANJwiL01tuY7r3oLuwwpPofcH8h2BGvrl2L2vsTnMQIV3z4Y2r8wL-pJcvbB8ZsDtiHKx6tm3pRbduuSPR1AMMvYQH8DKXOA12a8sL44r_bm3L0z-AqtiuP1OcjAa9h_TvZvGyXYURxVmVkgHJt-xomnBXzVGuL6idsJACj9KqVfsL3wiuPW-ZAJvFHH1VRIZR7sXwb7jdPQXjmQTgRCfjqiwJGMImhl0_zoU7PyOBeNsgOJfXktUGhNi-4TrZgJ41fXZwp4ffy-ke4PzQGaEMOdAlyeE9CBpg1_IA=w600-h180-no)

## Screencast

![screencast](https://s3.gifyu.com/images/ezgif.com-crop7c17fcbf5b8ea8fd.gif)

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
