package ccf.messaging

import ccf.tree.operation.TreeOperation

class Message
case class ConcurrentOperationMessage(val op: TreeOperation, val localMessage: Int, val expectedRemoteMessage: Int) extends Message
class ErrorMessage(val reason: String) extends Message
case class ChannelShutdown(override val reason: String) extends ErrorMessage(reason)
