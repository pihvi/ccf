package ccf

import tree.operation.TreeOperation

trait JupiterTransformer {
  def transformRemoteOpForLocalExecution(localOp: TreeOperation, remoteOp: TreeOperation, localIsPrimary: Boolean): TreeOperation
  def createNoOp: TreeOperation
}
