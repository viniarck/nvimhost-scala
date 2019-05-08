package nvimhost

import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}
import upack.write
import io.StdIn
import scala.concurrent.{Promise, Await, Future}
import scala.util.{Success, Failure, Try}

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}

import akka.Done
import java.net.InetSocketAddress

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

import akka.actor.{Actor, ActorSystem, Kill, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

import com.typesafe.scalalogging.{LazyLogging}

import NvimTypes._
import scala.collection.mutable.ArrayBuffer

object Foo {

  // val nvim = new NvimAPI(new InetSocketAddress("localhost", 6666))

  def Bar(i: ujson.Num, x: ujson.Num): ujson.Str = {
    // nvim.command("echomsg 'hello world'")
    (i.num + x.num).toString
  }

  def Heck(i: ujson.Num): ujson.Num = {
    (i.num + 10).toInt
  }

  def Arr(i: ujson.Num): ujson.Arr = {
    List("Nice", "Cool", "Bro")
  }

}

object MyApp extends App with LazyLogging {
  val port = 6666
  val system = ActorSystem("ClientMain")
  // val clientConnection = system.actorOf(
  //   Props(new ClientActor(new InetSocketAddress("localhost", port), system))
  // )

  // val res = Reflection.call("nvimhost.Foo$", "bar", 5, 10)
  // println(res.getClass())
  // println(res)
  // println(s"r ${r}")
  // val json = JsonUtil.toJson(foo)
  // println(json)
  // val om = new ObjectMapper()
  // om.readValue(foo)
  // val mapper = new ObjectMapper
  // mapper.registerModule(DefaultScalaModule)
  // println(mapper.writeValueAsString(MyJsonObj(10, "lol")))
  // val obj = mapper.readValue("""{"type":1}""", classOf[MyJsonObj])
  // println(obj.myType)
  // println(JacksonConfig.apiObjectMapper.writeValueAsString(foo))
  // val introspector = new JacksonAnnotationIntrospector()
  // val ac = AnnotatedClass.construct(foo.getClass(), introspector, null)
  // for (method <- ac.memberMethods()) {
  //   val annotation = method.getAnnotation(classOf[ApiOperation])
  //   if (annotation != null) {
  //     println(s"${method.getFullName} -> ${annotation.nickname()}")
  //   }
  // }
  // val nvim = new NvimAPI()
  // val fut = nvim.commandOutput("echo g:coc_enabled")
  // c.listRuntimePaths()
  // Try(Await.result(fut, 1.second)) match {
  //   case Success(v) => logger.debug(s"result '${v}'")
  //   case Failure(e) => logger.error(e.toString())
  // }

  // val fut2 = nvim.bufGetLines(2, 0, -1, true)
  // Try(Await.result(fut2, 1.second)) match {
  //   case Success(v) => logger.debug(s"result '${v}'")
  //   case Failure(e) => logger.error(e.toString())
  // }
  // fut.foreach(println)
  // println(c.bufLineCount(1))
  // println(c.bufLineCount(10))

  try {
    Reflection.genPluginFile("nvimhost.Foo$", "~/.config/nvim/settings/nvimhostscala.vim", "~/repos/nvimhost-scala/target/scala-2.12/nvimhostscala.jar")
  } catch {
    case e: java.nio.file.NoSuchFileException => logger.error(e.toString()); System.exit(1)
  }

  val host = "localhost"
  val plugin = system.actorOf(
    (Props(new PluginActor(new InetSocketAddress(host, port), system, "nvimhostscala.jar")))
  )
  // val foo = new Foo()

  // println(Reflection.hasValidSignatures("nvimhost.Foo$"))

  // import scala.concurrent.ExecutionContext.Implicits.global
  // logger.debug("Started 2")
  // val f = Future {
  //   Thread.sleep(500)
  //   logger.debug("Finished 2")
  // }
  // Await.result(f, 1.second)
  // logger.debug("Next 2")
  // val res = Await.result(fut, 1.second) match {
  //   case Result(res) => println(res.getClass); println(res.asInstanceOf[Double] + 1);res
  //   case Error(err)  => err
  // }
  // val res = Await.result(c.bufferLineCount(), 1.second) match {
  // // val res = Await.result(c.command("echomsg 'nicexxx'"), 1.second) match {
  //   case Result(res) => println(res.getClass);res
  //   case Error(err)  => err
  // }
  while (true) {
    Thread.sleep(60 * 1000)
  }
  // StdIn.readLine()
  // System.exit(0)
}
