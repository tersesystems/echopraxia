package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.{Field, FieldBuilderResult}

// if using -T here then all the subtypes of iterable also apply
trait ToFieldBuilderResult[-T] {
  def toResult(input: T): FieldBuilderResult
}

trait LowPriorityToFieldBuilderResult {
  implicit val iterableToFieldBuilderResult: ToFieldBuilderResult[Iterable[Field]] =
    new ToFieldBuilderResult[Iterable[Field]] {
      override def toResult(iterable: Iterable[Field]): FieldBuilderResult =
        FieldBuilderResult.list(iterable.toArray)
    }

  implicit val iteratorToFieldBuilderResult: ToFieldBuilderResult[Iterator[Field]] =
    new ToFieldBuilderResult[Iterator[Field]] {
      import scala.collection.JavaConverters.asJavaIteratorConverter
      override def toResult(iterator: Iterator[Field]): FieldBuilderResult =
        FieldBuilderResult.list(iterator.asJava)
    }

  //  @SuppressWarnings(Array("deprecated"))
  //  implicit val traversableToFieldBuilderResult: ToFieldBuilderResult[Traversable[Field]] = new ToFieldBuilderResult[Traversable[Field]] {
  //    override def toResult(traversable: Traversable[Field]): FieldBuilderResult = seqToFieldBuilderResult.toResult(traversable.toSeq)
  //  }

  //  implicit val seqToFieldBuilderResult: ToFieldBuilderResult[Seq[Field]] = new ToFieldBuilderResult[Seq[Field]] {
  //    override def toResult(iterable: Seq[Field]): FieldBuilderResult = FieldBuilderResult.list(iterable.toArray)
  //  }

  // array doesn't seem to be covered by Iterable
  implicit val arrayToFieldBuilderResult: ToFieldBuilderResult[Array[Field]] =
    new ToFieldBuilderResult[Array[Field]] {
      override def toResult(array: Array[Field]): FieldBuilderResult =
        FieldBuilderResult.list(array)
    }
}

object ToFieldBuilderResult extends LowPriorityToFieldBuilderResult {
  def apply[T: ToFieldBuilderResult](input: T): FieldBuilderResult =
    implicitly[ToFieldBuilderResult[T]].toResult(input)
}
