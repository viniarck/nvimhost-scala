package nvimhost

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorSystem, ActorRef, Kill, Props, Stash}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import scala.concurrent.{Promise, Future}
import NvimTypes._
import scala.collection.mutable.ArrayBuffer

import com.typesafe.scalalogging.{LazyLogging}

/** Actor to represent an IO/TCP client to connect to Nvim. It mostly sends RPC requests asynchronously */
class ClientActor(address: InetSocketAddress, actorSystem: ActorSystem)
    extends Actor
    with LazyLogging
    with Stash {

  logger.info("Bootstraping client connection")
  IO(Tcp)(actorSystem) ! Connect(address)
  var promises = scala.collection.mutable.Map[Int, PromiseBox]()

  def receive = {
    case CommandFailed(command: Tcp.Command) =>
      logger.error(s"Failed to connect to ${address.toString}")
      self ! Kill
      actorSystem.terminate()
      System.exit(1)
    case Connected(remote, local) =>
      logger.info(s"Successfully connected to $address")
      val connection = sender()
      connection ! Register(self)
      unstashAll()
      context become {
        case Received(data) =>
          val decoded = upack.transform(data.toArray.clone(), ujson.Value);
          logger.debug(
            s"Received arr: ${decoded.arr} size: ${decoded.arr.length}"
          )
          if (decoded.arr.length != 4) {
            val errMsg =
              "Nvim RPC response format has changed. Please file an issue on https://github.com/viniarck/nvimhost-scala"
            logger.error(errMsg)
            throw new RuntimeException(errMsg)
          }
          if (decoded
                .arr(0)
                .value
                .asInstanceOf[Double]
                .toInt == MsgKind.Response.id) {
            val msgId: Int = decoded.arr(1).value.asInstanceOf[Double].toInt
            val error = decoded.arr(2).value
            val result = decoded.arr(3).value
            val promise = promises.get(msgId) match {
              case Some(StringPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[String])
              case Some(IntPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[Int])
              case Some(DoublePromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[Double])
              case Some(BooleanPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[Boolean])
              case Some(UnitPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[Unit])
              case Some(BufferPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[Buffer])
              case Some(WindowPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[Window])
              case Some(TabPagePromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[TabPage])
              case Some(ArrayBufferStringPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[ArrayBuffer[String]])
              case Some(ArrayBufferIntPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[ArrayBuffer[Int]])
              case Some(ArrayBufferWindowPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[ArrayBuffer[Window]])
              case Some(ArrayBufferBufferPromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[ArrayBuffer[Buffer]])
              case Some(ArrayBufferTabPagePromiseBox(p)) =>
                if (error != null)
                  p.failure(new RuntimeException(error.toString()))
                else p.success(result.asInstanceOf[ArrayBuffer[TabPage]])
              case None =>
                logger.error(s"Couldn't find promise id '${msgId}'");
                throw new RuntimeException(
                  s"Couldn't find promise id '${msgId}'"
                )
            }
          } else {
            throw new RuntimeException(
              s"Received wrong message type ${decoded.arr(0).value}"
            )
          }
        case Request(msg, promise) => {
          promises += (msg.id -> promise)
          connection ! Write(ByteString(msg.encode))
        }
        case Notify(msg) =>
          connection ! Write(ByteString(msg.encode))
        case _: ConnectionClosed => logger.error("Connetion closed")
      }
    case _ => stash()
  }
}
