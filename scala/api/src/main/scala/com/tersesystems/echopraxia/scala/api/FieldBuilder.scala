package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api._

/**
 * A field builder that is enhanced with ToValue, ToObjectValue, and ToArrayValue.
 */
trait FieldBuilder {

  def list(fields: Field*): FieldBuilderResult                    = list(fields)
  def list[T: ToFieldBuilderResult](input: T): FieldBuilderResult = ToFieldBuilderResult[T](input)

  // ------------------------------------------------------------------
  // keyValue

  def keyValue[V: ToValue](key: String, value: V): Field =
    Field.keyValue(key, ToValue[V].toValue(value))
  def keyValue[V: ToValue](tuple: (String, V)): Field = keyValue(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // value

  def value[V: ToValue](key: String, value: V): Field =
    Field.value(key, ToValue[V].toValue(value))
  def value[V: ToValue](tuple: (String, V)): Field = value(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // array

  def array[AV: ToArrayValue](name: String, value: AV): Field =
    keyValue(name, implicitly[ToArrayValue[AV]].toArrayValue(value))
  def array[AV: ToArrayValue](tuple: (String, AV)): Field = array(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // object

  def obj[OV: ToObjectValue](name: String, value: OV): Field =
    keyValue(name, ToObjectValue[OV].toObjectValue(value))
  def obj[OV: ToObjectValue](tuple: (String, OV)): Field = obj(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // string

  def string(name: String, string: String): Field = value(name, string)
  def string(tuple: (String, String)): Field      = value(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // number

  def number(name: String, number: Number): Field = value(name, number)
  def number(tuple: (String, Number)): Field      = value(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // boolean

  def bool(name: String, boolean: Boolean): Field = value(name, boolean)
  def bool(tuple: (String, Boolean)): Field       = value(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // null

  def nullField(name: String): Field = keyValue(name, Value.NullValue.instance)

  // ------------------------------------------------------------------
  // exception

  def exception(ex: Throwable): Field               = value(Field.EXCEPTION, ex)
  def exception(name: String, ex: Throwable): Field = keyValue(name, ex)

  // ------------------------------------------------------------------
  // object

}

object FieldBuilder extends FieldBuilder
