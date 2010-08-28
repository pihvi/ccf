package ccf

import ccf.messaging.ConcurrentOperationMessage
import org.specs.Specification
import org.specs.mock.Mockito
import tree.indexing.UndefinedIndex
import tree.operation.{DeleteOperation, NoOperation, TreeOperation}

class JupiterOperationSynchronizerSpec extends Specification with Mockito {
  val transformer = mock[JupiterTransformer]
  transformer.createNoOp returns NoOperation()
  val synchronizer = new JupiterOperationSynchronizer(true, transformer)

  "Any JupiterOperationSynchronizer" should {
    "start indexing operations from zero" in {
      val msg = synchronizer.createLocalOperation(NoOperation())
      msg.localMessage must equalTo(0)
      msg.expectedRemoteMessage must equalTo(0)
    }

    "increment the local operation index on each created operation" in {
      synchronizer.createLocalOperation(NoOperation()).localMessage must equalTo(0)
      synchronizer.createLocalOperation(NoOperation()).localMessage must equalTo(1)
      synchronizer.createLocalOperation(NoOperation()).localMessage must equalTo(2)
      synchronizer.createLocalOperation(NoOperation()).localMessage must equalTo(3)
    }

    "increment the expected remote operation index on each received operation" in {
      val msg1 = ConcurrentOperationMessage(NoOperation(), 0, 0)
      val msg2 = ConcurrentOperationMessage(NoOperation(), 1, 0)
      val msg3 = ConcurrentOperationMessage(NoOperation(), 2, 0)
      synchronizer.receiveRemoteOperation(msg1)
      synchronizer.expectedRemoteMessage must equalTo(1)
      synchronizer.receiveRemoteOperation(msg2)
      synchronizer.expectedRemoteMessage must equalTo(2)
      synchronizer.receiveRemoteOperation(msg3)
      synchronizer.expectedRemoteMessage must equalTo(3)
    }
  }

  "Jupiter operation synchronizer after two messages have been received" should {
    val msg1 = ConcurrentOperationMessage(NoOperation(), 0, 0)
    val msg2 = ConcurrentOperationMessage(DeleteOperation(UndefinedIndex()), 1, 0)

    synchronizer.receiveRemoteOperation(msg1)
    synchronizer.receiveRemoteOperation(msg2)

    "accept the subsequent message" in {
      val op = DeleteOperation(UndefinedIndex())
      val msg = ConcurrentOperationMessage(op, 2, 0)
      synchronizer.receiveRemoteOperation(msg) must equalTo(op)
    }

    "reject message if messages are missing from the sequence" in {
      val msg = ConcurrentOperationMessage(DeleteOperation(UndefinedIndex()), 3, 0)
      synchronizer.receiveRemoteOperation(msg) must throwA[RuntimeException]
    }

    "transform operation to no-op in case of duplicate message" in {
      synchronizer.receiveRemoteOperation(msg2) must equalTo(NoOperation())
    }
  } 
}
