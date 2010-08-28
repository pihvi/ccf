package ccf

import ccf.messaging.ConcurrentOperationMessage
import tree.operation.TreeOperation

trait OperationSynchronizer {
  def resetToInitialState: Unit
  def createLocalOperation(operation: TreeOperation): ConcurrentOperationMessage
  def receiveRemoteOperation(message: ConcurrentOperationMessage): TreeOperation
}
