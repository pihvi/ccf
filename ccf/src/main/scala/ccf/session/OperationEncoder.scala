package ccf.session

import ccf.tree.operation.TreeOperation

trait OperationEncoder {
  def encode(op: TreeOperation): Any
}