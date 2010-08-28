package ccf

abstract class OperationSynchronizerFactory {
  def createSynchronizer: OperationSynchronizer
}

