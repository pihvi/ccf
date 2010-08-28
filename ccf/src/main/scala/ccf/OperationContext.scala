package ccf

import tree.operation.TreeOperation

case class OperationContext(val op: TreeOperation, val localMsgSeqNo: Int, val remoteMsgSeqNo: Int) {
  def encode: Any = Map("op" -> op.encode, "localMsgSeqNo" -> localMsgSeqNo, "remoteMsgSeqNo" -> remoteMsgSeqNo)
}
