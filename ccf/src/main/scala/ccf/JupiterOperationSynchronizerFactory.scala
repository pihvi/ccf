package ccf

class JupiterOperationSynchronizerFactory(isPrimary: Boolean, transformer: JupiterTransformer) extends OperationSynchronizerFactory {
  def createSynchronizer = new JupiterOperationSynchronizer(isPrimary, transformer)
}
