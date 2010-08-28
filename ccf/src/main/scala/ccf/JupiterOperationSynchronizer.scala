package ccf

import ccf.messaging.ConcurrentOperationMessage
import tree.operation.TreeOperation

class JupiterOperationSynchronizer(val isPrimary: Boolean, transformer: JupiterTransformer) extends OperationSynchronizer {
  var lastLocallyCreatedMessage = 0
  var expectedRemoteMessage = 0
  private var unacknowledgedMessages = List[ConcurrentOperationMessage]()

  def resetToInitialState {
    lastLocallyCreatedMessage = 0
    expectedRemoteMessage = 0
    unacknowledgedMessages = List()
  }

  def createLocalOperation(operation: TreeOperation) = {
    val messageToSend = ConcurrentOperationMessage(operation, lastLocallyCreatedMessage, expectedRemoteMessage)
    unacknowledgedMessages = unacknowledgedMessages ::: List(messageToSend)
    lastLocallyCreatedMessage = lastLocallyCreatedMessage + 1
    messageToSend
  }

  def receiveRemoteOperation(remoteMessage: ConcurrentOperationMessage): TreeOperation = {
    if (remoteMessage.localMessage < expectedRemoteMessage)
      return transformer.createNoOp
    discardAcknowledgedMessages(remoteMessage.expectedRemoteMessage)

    if (remoteMessage.localMessage != expectedRemoteMessage)
      throw new RuntimeException("Missing message from the sequence, receiver expected #" + 
        expectedRemoteMessage + ", sender sent #" + remoteMessage.localMessage)

    var transformedRemoteOp = remoteMessage.op

    unacknowledgedMessages = unacknowledgedMessages.map { localMsg : ConcurrentOperationMessage =>
      val transformedLocalOp = transformLocal(localMsg.op, transformedRemoteOp)
      transformedRemoteOp = transformRemote(localMsg.op, transformedRemoteOp)
      ConcurrentOperationMessage(transformedLocalOp, localMsg.localMessage, localMsg.expectedRemoteMessage)
    }

    expectedRemoteMessage = remoteMessage.localMessage + 1
    transformedRemoteOp
  }

  private def discardAcknowledgedMessages(acknowledgedMessageIndex: Int) {
    unacknowledgedMessages = unacknowledgedMessages.filter { m  => (m.localMessage >= acknowledgedMessageIndex) }
  }

  private def transformRemote(localOp: TreeOperation, remoteOp: TreeOperation) = {
    transformer.transformRemoteOpForLocalExecution(localOp, remoteOp, isPrimary)
  }

  private def transformLocal(localOp: TreeOperation, remoteOp: TreeOperation) = {
    transformer.transformRemoteOpForLocalExecution(remoteOp, localOp, !isPrimary)
  }
}

