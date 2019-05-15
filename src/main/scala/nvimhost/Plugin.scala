package nvimhost

import akka.Done
import akka.actor.{Actor, ActorSystem, Kill, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import akka.io.Tcp._
import com.typesafe.scalalogging.{LazyLogging}
import java.net.InetSocketAddress
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}
import collection.mutable.ArrayBuffer

/** Abstraction that represents a Nvim Plugin */
abstract class Plugin(
    address: InetSocketAddress,
    packageObjectName: String,
    outVimFilePath: String,
    jarFilePath: String
) extends LazyLogging {

  logger.info("Validating plugin name and methods")
  try {
    Reflection.validatePluginStructure(packageObjectName)
  } catch {
    case e: Exception => logger.error(e.toString()); System.exit(1)
  }

  logger.info(s"Generating manifest vim file: $outVimFilePath for: $packageObjectName, executable jar: $jarFilePath")
  Reflection.genPluginFile(
    packageObjectName,
    outVimFilePath,
    jarFilePath
  )

  val nvim = new NvimAPI(
    new InetSocketAddress(address.getHostName, address.getPort)
  )
  val jarName = jarFilePath.split(java.io.File.separator).last.replace(".jar", "")
  val system = ActorSystem(jarName)
  val pluginActor = system.actorOf(
    (Props(new PluginActor(address, system, packageObjectName, nvim, jarName)))
  )

  def serveForever() = {
    while (true) {
      Thread.sleep(60000)
    }
  }

  override def toString() = s"Plugin($jarName)"
}

/** Actor to represent an IO/TCP plugin, it's channel is dedicated to receive and reply RPC message. This actor needs to embed an instance of NvimAPI since the plugin has to set a variable to bootstrap the plugin RPC channel and send messages to Nvim */
class PluginActor(
    address: InetSocketAddress,
    actorSystem: ActorSystem,
    packageObjectName: String,
    nvim: NvimAPI,
    jarName: String
) extends Actor
    with LazyLogging {

  logger.info("Bootstraping plugin socket")
  val jarNameFile = jarName

  IO(Tcp)(actorSystem) ! Connect(address)
  var channelId: Int = 0

  def receive: Receive = {
    case CommandFailed(command: Tcp.Command) =>
      logger.error(s"Failed to connect to ${address.toString}")
      self ! Kill
      actorSystem.terminate()
    case Connected(remote, local) =>
      logger.info(s"Successfully connected to $address")
      val connection = sender()

      connection ! Register(self)
      connection ! Write(
        ByteString(Parser.newRequestMsg("nvim_get_api_info").encode)
      )

      context become {
        case Received(data) =>
          val decoded = upack.transform(data.toArray.clone(), ujson.Value);
          logger.debug(
            s"Received arr: ${decoded.arr} size: ${decoded.arr.length}"
          )
          if (decoded.arr.length < 2 || decoded.arr.length > 4) {
            logger.error(
              "Nvim RPC response format has changed. Please file an issue on https://github.com/viniarck/nvimhost-scala"
            )
            self ! Kill
            actorSystem.terminate()
            System.exit(1)
          }
          if (channelId == 0) {
            try {
              channelId = decoded.arr(3)(0).num.toInt
              logger.debug(s"Trying to set channelId as '${channelId}'")
              val fut =
                nvim.command(s"let g:${jarNameFile}_channel=${channelId}")
              Try(Await.result(fut, 1.second)) match {
                case Success(v) =>
                  logger.info(
                    s"Successfully set the channel 'g:${jarNameFile}_channel=${channelId}'"
                  )
                case Failure(e) =>
                  logger.error(s"Failed to set the channel. ${e.toString()}")
              }
            } catch {
              case e: Exception => logger.error(e.toString())
            }
          } else {
            val msgType: Int = decoded.arr(0).value.asInstanceOf[Double].toInt
            var msgId: Int = 0
            var methodName: String = ""
            var argsIndex = 0
            msgType match {
              case 0 => {
                logger.debug("Received a sync request from nvim")
                msgId = decoded.arr(1).value.asInstanceOf[Double].toInt
                methodName = decoded.arr(2).value.toString()
                argsIndex = 3
              }
              case 2 => {
                logger.debug("Received an async request from nvim")
                methodName = decoded.arr(1).value.toString()
                argsIndex = 2
              }
              case _ => logger.error(s"Received wrong message type: ${msgType}")
            }
            val argsIn: ArrayBuffer[Any] =
              decoded.arr(argsIndex).value.asInstanceOf[ArrayBuffer[Any]]
            logger.debug(
              s"Response data: ${methodName} ${argsIn.getClass().toString}"
            )
            val args: ujson.Arr = argsIn(0).asInstanceOf[ujson.Arr]
            logger.debug(
              s"Calling ${methodName} argsIn: ${argsIn} args: ${args}"
            )
            try {
              val res = Reflection.call(
                packageObjectName,
                methodName.split(":").last,
                args.value.toArray: _*
              )
              logger.debug(
                s"Response res: ${res} res class: ${res.getClass.toString}"
              )
              if (msgType == 0) {
                val response: ResponseMsg = res match {
                  case s: ujson.Str =>
                    new ResponseMsg(
                      msgId,
                      upack.Null,
                      upack.Str(s.str)
                    )
                  case i: ujson.Num => {
                    if (math.ceil(i.num) == i.num)
                      new ResponseMsg(
                        msgId,
                        upack.Null,
                        upack.Int32(i.num.toInt)
                      )
                    else
                      new ResponseMsg(
                        msgId,
                        upack.Null,
                        upack.Float64(i.num)
                      )
                  }
                  case b: ujson.Bool =>
                    new ResponseMsg(
                      msgId,
                      upack.Null,
                      upack.Bool(b.value)
                    )
                  case a: ujson.Arr => {
                    import Conversions._
                    var args = ArrayBuffer[upack.Msg]()
                    for (item <- a.arr.map(_.value).toArray) {
                      if (item.isInstanceOf[Double])
                        args += (item.asInstanceOf[Double])
                      else if (item.isInstanceOf[Int])
                        args += (item.asInstanceOf[Int])
                      else if (item.isInstanceOf[Boolean])
                        args += (item.asInstanceOf[Boolean])
                      else if (item.isInstanceOf[String])
                        args += (item.toString)
                      else
                        throw new RuntimeException(
                          s"Unsupported return type ${item.getClass.toString}, ujson.Arr return type only supports [Double, Int, Boolean, String, Unit]"
                        )
                    }
                    new ResponseMsg(
                      msgId,
                      upack.Null,
                      args: _*
                    )
                  }
                  case _ =>
                    val err = s"Unsupported return type: ${res.getClass.toString}"
                    logger.error(err)
                    throw new RuntimeException(err)
                }
                connection ! Write(ByteString(response.encode))
              }
            } catch {
              case e: Exception => {
                logger.error(e.toString)
                connection ! Write(
                  ByteString(new ResponseMsg(msgId, upack.Str(e.getMessage)).encode)
                )
              }
            }
          }
        case _: ConnectionClosed => {
          logger.error("Connetion closed")
          channelId = 0
        }
      }
  }
}
