package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.{Field, Value}

import scala.annotation.implicitNotFound
import scala.util.{Failure, Success, Try}

/**
 * The ToValue trait, used for turning scala things into Value.
 * 
 * Most of the time you will define this in your own field builder.
 * For example to define `java.time.Instant` you could do this:
 * 
 * {{{
 * trait InstantFieldBuilder extends FieldBuilder {
 *   import com.tersesystems.echopraxia.Value
 *   implicit val instantToStringValue: ToValue[Instant] = ToValue(instantValue)
 *   def instant(name: String, i: Instant): Field = keyValue(name -> instantValue(i))
 *   def instant(tuple: (String, Instant)): Field = keyValue(tuple)
 *   private def instantValue(i: Instant): Value.StringValue = Value.string(i.toString)
 * }
 * }}}
 *
 * And then you can import the field builder context:
 *
 * {{{
 * logger.info("{}", fb => {
 *   import fb._
 *   fb.onlyKeyValue("instant" -> Instant.now())
 * })
 * }}}
 *
 * If you are implementing a mapper for a case class, you may want
 * to implement `ToObjectValue` as well:
 *
 * {{{
 * trait PersonFieldBuilder extends FieldBuilder {
 *   implicit val personToValue: ToValue[Person] = ToValue(personValue)
 *   implicit val personToObjectValue: ToObjectValue[Person] = ToObjectValue(personValue)
 *   def person(name: String, person: Person): Field = keyValue(name, personValue(person))
 *   def onlyPerson(name: String, p: Person): util.List[Field] = this.only(person(name, p))
 *   private def personValue(p: Person): Value.ObjectValue = Value.`object`(
 *     string("name", p.name),
 *     number("age", p.age)
 *   )
 * }
 * }}}
 *
 * @tparam T the object type
 */
@implicitNotFound("Could not find an implicit ToValue[${T}]")
@FunctionalInterface
trait ToValue[-T] {
  def toValue(t: T): Value[_]
}

object ToValue {
  def apply[T: ToValue]: ToValue[T] = implicitly[ToValue[T]]

  private def convert[T](f: T => Value[_]): ToValue[T] = (value: T) => f.apply(value)

  implicit val valueToValue: ToValue[Value[_]] = ToValue.convert(identity)

  implicit val stringToStringValue: ToValue[String] = ToValue.convert(Value.string)

  implicit def numberToValue[N <: Number]: ToValue[N] = ToValue.convert(Value.number(_))
  implicit val byteToValue: ToValue[Byte]             = ToValue.convert(Value.number(_))
  implicit val shortToValue: ToValue[Short]           = ToValue.convert(Value.number(_))
  implicit val intToValue: ToValue[Int]               = ToValue.convert(Value.number(_))
  implicit val longToValue: ToValue[Long]             = ToValue.convert(Value.number(_))
  implicit val doubleToValue: ToValue[Double]         = ToValue.convert(Value.number(_))
  implicit val floatToValue: ToValue[Float]           = ToValue.convert(Value.number(_))
  implicit val bigIntToValue: ToValue[BigInt]         = ToValue.convert(Value.number(_))
  implicit val bigDecimalToValue: ToValue[BigDecimal] = ToValue.convert(Value.number(_))

  implicit val booleanToBoolValue: ToValue[Boolean]            = ToValue.convert(Value.bool(_))
  implicit val javaBoolToBoolValue: ToValue[java.lang.Boolean] = ToValue.convert(Value.bool)

  implicit def throwableToValue[T <: Throwable]: ToValue[T] = ToValue.convert(Value.exception)

  implicit def optionValue[V: ToValue]: ToValue[Option[V]] = {
    case Some(v) =>
      implicitly[ToValue[V]].toValue(v)
    case None =>
      Value.NullValue.instance
  }

  implicit def tryValue[V: ToValue]: ToValue[Try[V]] = {
    case Success(v) =>
      implicitly[ToValue[V]].toValue(v)
    case Failure(e) =>
      ToValue[Throwable].toValue(e)
  }

  implicit def eitherValue[A: ToValue, B: ToValue]: ToValue[Either[A, B]] = {
    case Left(a) =>
      ToValue[A].toValue(a)
    case Right(r) =>
      ToValue[B].toValue(r)
  }

}

/**
 * ToArrayValue is used when passing an ArrayValue to a field builder.
 *
 * {{{
 * val array: Array[Int] = Array(1, 2, 3)
 * logger.info("{}", fb => fb.onlyArray("array", array)
 * }}}
 *
 * @tparam T the array type.
 */
@implicitNotFound("Could not find an implicit ToArrayValue[${T}]")
trait ToArrayValue[-T] {
  def toArrayValue(t: T): Value.ArrayValue
}

object ToArrayValue {

  def apply[T: ToArrayValue]: ToArrayValue[T] = implicitly[ToArrayValue[T]]

  implicit val identityArrayValue: ToArrayValue[Value.ArrayValue] = identity(_)

  implicit def iterableToArrayValue[V: ToValue]: ToArrayValue[collection.Iterable[V]] =
    (iterable: collection.Iterable[V]) => {
      val iterable1 = iterable.map(ToValue[V].toValue)
      Value.array(iterable1.toSeq: _*)
    }

  implicit def arrayToArrayValue[V: ToValue]: ToArrayValue[Array[V]] = (array: Array[V]) => {
    val array1 = array.map(ToValue[V].toValue)
    Value.array(array1.toSeq: _*)
  }
}

/**
 * ToObjectValue is used when providing an explicit `object` value to
 * a field builder.  Notable when you have a field or fields in a collection.
 *
 * {{{
 * val fields: Seq[Field] = ???
 * logger.info("{}", fb => fb.onlyObject("obj", fields)
 * }}}
 *
 * @tparam T the object type
 */
@implicitNotFound("Could not find an implicit ToObjectValue[${T}]")
trait ToObjectValue[-T] {
  def toObjectValue(t: T): Value.ObjectValue
}

object ToObjectValue {

  def apply[T: ToObjectValue]: ToObjectValue[T] = implicitly[ToObjectValue[T]]

  implicit val fieldToObjectValue: ToObjectValue[Field] = { f =>
    Value.`object`(f)
  }

  implicit val iterableToObjectValue: ToObjectValue[collection.Iterable[Field]] = { t =>
    Value.`object`(t.toSeq: _*)
  }

  // Don't include mapToObjectValue by default: keyValue vs value should be a user choice, not
  // done automagically by the framework.
  //
  //  implicit def mapToObjectValue[V: ToValue]: ToObjectValue[Map[String, V]] = new ToObjectValue[Map[String, V]] {
  //    override def toObjectValue(t: Map[String, V]): Value.ObjectValue = {
  //      val fields: Seq[Field] = t.map {
  //        case (k, v) =>
  //          Value.keyValue(k, v.toString)
  //      }.toSeq
  //      Value.`object`(fields.asJava)
  //    }
  //  }
}
