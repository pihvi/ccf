package ccf.server

import ccf.transport.{ClientId, ChannelId}
import ccf.messaging.ConcurrentOperationMessage
import collection.mutable.ArrayBuffer
import ccf.OperationSynchronizer
import ccf.tree.operation.TreeOperation

class ClientState(val channel: ChannelId, synchronizer: OperationSynchronizer) {
  def receive(msg: ConcurrentOperationMessage): TreeOperation = {
    synchronizer.receiveRemoteOperation(msg)
  }
  def send(op: TreeOperation): ConcurrentOperationMessage = {
    synchronizer.createLocalOperation(op)
  }
}
