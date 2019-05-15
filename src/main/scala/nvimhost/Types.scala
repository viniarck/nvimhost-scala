package nvimhost

import concurrent.Promise
import collection.mutable.ArrayBuffer

case class Notify(msg: NotifyMsg)
case class Request(msg: RequestMsg, promise: PromiseBox)

/** Base abstract class to represent Nvim message types */
abstract class NvimMsg(_typeValue: Int, _method: String, _args: upack.Msg*) {
  val typeValue = _typeValue
  val method = _method
  val args = _args
  def encode: Array[Byte]
}

/** Represent a RPC msgpack request type message. This message is serialized as msgpack, and the transmission leverages async IO/TCP */
class RequestMsg(_id: Int, method: String, args: upack.Msg*)
    extends NvimMsg(0, method, args: _*) {
  val id = _id
  override def toString = s"SyncMsg(${this.id}, ${this.method}, ${this.args})"
  override def encode: Array[Byte] = {
    val msgSeq = Seq[upack.Msg](
      upack.Int32(this.typeValue),
      upack.Int32(this.id),
      upack.Str(this.method),
      upack.Arr(this.args: _*)
    )
    return upickle.default.writeBinary(msgSeq)
  }
}

/** Represent a RPC msgpack response type message */
class ResponseMsg(_id: Int, _error: upack.Msg, args: upack.Msg*) {
  val id = _id
  val error = _error
  override def toString =
    s"ResponseMsg(${this.id}, ${this.error}, ${this.args})"
  def encode: Array[Byte] = {
    if (_error.isInstanceOf[upack.Str])
      return upickle.default.writeBinary(
        Seq[upack.Msg](
          upack.Int32(1),
          upack.Int32(this.id),
          upack.Str(_error.str),
          upack.Null
        )
      )
    if (args.length > 1)
      upickle.default.writeBinary(
        Seq[upack.Msg](
          upack.Int32(1),
          upack.Int32(this.id),
          upack.Null,
          upack.Arr(this.args: _*)
        )
      )
    else
      upickle.default.writeBinary(
        Seq[upack.Msg](
          upack.Int32(1),
          upack.Int32(this.id),
          upack.Null,
          args(0)
        )
      )
  }
}

/** Represent an asynchronous RPC notify message. At the moment it's not used since the synchronous message leverage async IO/TCP communication */
class NotifyMsg(method: String, args: upack.Msg*)
    extends NvimMsg(2, method, args: _*) {
  override def toString = s"AsyncMsg(${this.method}, ${this.args})"
  override def encode: Array[Byte] = {
    val msgSeq = Seq[upack.Msg](
      upack.Int32(this.typeValue),
      upack.Str(this.method),
      upack.Arr(this.args: _*)
    )
    return upickle.default.writeBinary(msgSeq)
  }
}

/** To represent Nvim ext msgpack types. In practice, though just an alias is enough */
object NvimTypes {
  type Buffer = Int;
  type Window = Int;
  type TabPage = Int;
}

/** Implicit conversions to facilitate msgpack parsing */
sealed trait PromiseBox
case class StringPromiseBox(p: Promise[String]) extends PromiseBox
case class IntPromiseBox(p: Promise[Int]) extends PromiseBox
case class DoublePromiseBox(p: Promise[Double]) extends PromiseBox
case class BooleanPromiseBox(p: Promise[Boolean]) extends PromiseBox
case class UnitPromiseBox(p: Promise[Unit]) extends PromiseBox
case class BufferPromiseBox(p: Promise[NvimTypes.Buffer]) extends PromiseBox
case class WindowPromiseBox(p: Promise[NvimTypes.Window]) extends PromiseBox
case class TabPagePromiseBox(p: Promise[NvimTypes.TabPage]) extends PromiseBox
case class ArrayBufferStringPromiseBox(p: Promise[ArrayBuffer[String]])
    extends PromiseBox
case class ArrayBufferIntPromiseBox(p: Promise[ArrayBuffer[Int]])
    extends PromiseBox
case class ArrayBufferWindowPromiseBox(
    p: Promise[ArrayBuffer[NvimTypes.Window]]
) extends PromiseBox
case class ArrayBufferBufferPromiseBox(
    p: Promise[ArrayBuffer[NvimTypes.Buffer]]
) extends PromiseBox
case class ArrayBufferTabPagePromiseBox(
    p: Promise[ArrayBuffer[NvimTypes.TabPage]]
) extends PromiseBox

/** Implicit conversions to facilitate msgpack parsing */
object Conversions {
  import collection.mutable.ArrayBuffer
  import upack.Msg
  implicit def strToPack(a: String): upack.Msg = upack.Str(a)
  implicit def int32ToPack(a: Int): upack.Msg = upack.Int32(a)
  implicit def int64ToPack(a: Long): upack.Msg = upack.Int64(a)
  implicit def float32ToPack(a: Float): upack.Msg = upack.Float32(a)
  implicit def float64ToPack(a: Double): upack.Msg = upack.Float64(a)
  implicit def boolToPack(a: Boolean): upack.Msg = upack.Bool(a)
  implicit def binaryToPack(a: Array[Byte]): upack.Msg = upack.Binary(a)
  implicit def arrToPack(a: ArrayBuffer[Msg]): upack.Msg = upack.Arr(a)
  implicit def arrStringToPack(a: ArrayBuffer[String]): upack.Msg = upack.Arr(a)
  implicit def arrIntToPack(a: ArrayBuffer[Int]): upack.Msg = upack.Arr(a)
}
