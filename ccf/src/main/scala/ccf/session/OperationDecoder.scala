package ccf.session

import ccf.tree.operation.TreeOperation

trait OperationDecoder {
  def decode(any: Any): TreeOperation
}