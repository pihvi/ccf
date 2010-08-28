package ccf.tree.operation

import ccf.session.OperationEncoder

class TreeOperationEncoder extends OperationEncoder {
  def encode(op: TreeOperation) = {
    val typeString = op.getClass.getSimpleName
    Map("type" -> typeString) ++ encodedContents(op)
  }

  private def encodedContents(op: TreeOperation): Map[String, Any] = op match {
    case NoOperation() => Map()
    case InsertOperation(index, node) => Map("index" -> index.encode, "node" -> node.encode)
    case DeleteOperation(index) => Map("index" -> index.encode)
    case MoveOperation(srcIndex, dstIndex) => Map("src" -> srcIndex.encode, "dst" -> dstIndex.encode)
    case UpdateAttributeOperation(index, attribute, modifier) => Map("index" -> index.encode, "attr" -> attribute, "modifier" -> modifier.encode)
    case _ => error("Unable to encode given operation " + op.toString)
  }
}

object TreeOperationEncoder extends TreeOperationEncoder