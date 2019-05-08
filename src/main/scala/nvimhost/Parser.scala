package nvimhost

/** Parser to create either synchronous or asynchronous msgpack messages */
object Parser {

  /** Global id counter to ensure uniqueness */
  var id: Int = 0

  /** Create a new Nvim RPC request message. Even though the message is meant to be synchronous (it's block Nvim), it's going to be sent asynchronously without blocking our client side */
  def newRequestMsg(method: String, args: upack.Msg*): RequestMsg = {
    id += 1
    new RequestMsg(id, method, args: _*)
  }

  /** Create a new Nvim RPC notify message */
  def newNotifyMsg(method: String, args: upack.Msg*): NotifyMsg =
    new NotifyMsg(method, args: _*)
}
