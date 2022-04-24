package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia._

import java.util
import scala.jdk.CollectionConverters._

/**
 * A field builder that is enhanced with ToValue, ToObjectValue, and ToArrayValue.
 */
trait FieldBuilder extends Field.Builder {

  // ------------------------------------------------------------------
  // keyValue

  def keyValue[V: ToValue](key: String, value: V): Field = KeyValueField.create(key, ToValue[V].toValue(value))
  def keyValue[V: ToValue](tuple: (String, V)): Field = keyValue(tuple._1, tuple._2)
  def onlyKeyValue[V: ToValue](key: String, value: V): util.List[Field] = only(keyValue(key, value))
  def onlyKeyValue[V: ToValue](tuple: (String, V)): java.util.List[Field] = only(keyValue(tuple._1, tuple._2))

  // ------------------------------------------------------------------
  // value

  def value[V: ToValue](key: String, value: V): Field = ValueField.create(key, ToValue[V].toValue(value))
  def value[V: ToValue](tuple: (String, V)): Field = value(tuple._1, tuple._2)
  def onlyValue[V: ToValue](key: String, value: V): java.util.List[Field] = only(this.value(key, value))
  def onlyValue[V: ToValue](tuple: (String, V)): java.util.List[Field] = only(value(tuple._1, tuple._2))

  // ------------------------------------------------------------------
  // array

  def array[AV: ToArrayValue](name: String, value: AV): Field = keyValue(name, implicitly[ToArrayValue[AV]].toArrayValue(value))
  def array[AV: ToArrayValue](tuple: (String, AV)): Field = array(tuple._1, tuple._2)
  def onlyArray[AV: ToArrayValue](name: String, value: AV): java.util.List[Field] = only(array(name, value))
  def onlyArray[AV: ToArrayValue](tuple: (String, AV)): util.List[Field] = only(array(tuple._1, tuple._2))

  // ------------------------------------------------------------------
  // object

  def obj[OV: ToObjectValue](name: String, value: OV): Field = keyValue(name, ToObjectValue[OV].toObjectValue(value))
  def obj[OV: ToObjectValue](tuple: (String, OV)): Field = obj(tuple._1, tuple._2)
  def onlyObj[OV: ToObjectValue](name: String, value: OV): java.util.List[Field] = only(obj(name, value))
  def onlyObj[OV: ToObjectValue](tuple: (String, OV)): java.util.List[Field] = onlyObj(tuple._1, tuple._2)

  // ------------------------------------------------------------------
  // string

  def string(name: String, string: String): Field = value(name, string)
  def string(tuple: (String, String)): Field = value(tuple._1, tuple._2)
  def onlyString(name: String, value: String): util.List[Field] = only(string(name, value))
  def onlyString(tuple: (String, String)): util.List[Field] = only(value(tuple._1, tuple._2))

  // ------------------------------------------------------------------
  // number

  def number(name: String, number: Number): Field = value(name, number)
  def number(tuple: (String, Number)): Field = value(tuple._1, tuple._2)
  def onlyNumber(name: String, value: Number): util.List[Field] = only(number(name, value))
  def onlyNumber(tuple: (String, Number)): util.List[Field] = only(value(tuple._1, tuple._2))

  // ------------------------------------------------------------------
  // boolean

  def bool(name: String, boolean: Boolean): Field = value(name, boolean)
  def onlyBool(name: String, bool: Boolean): util.List[Field] = only(value(name, bool))
  def bool(tuple: (String, Boolean)): Field = value(tuple._1, tuple._2)
  def onlyBool(tuple: (String, Boolean)): util.List[Field] = only(value(tuple._1, tuple._2))

  // ------------------------------------------------------------------
  // null

  def nullField(name: String): Field = keyValue(name, Field.Value.NullValue.instance)
  def onlyNullField(name: String): util.List[Field] = only(nullField(name))

  // ------------------------------------------------------------------
  // exception

  def exception(ex: Throwable): Field = value(Field.Builder.EXCEPTION,  ex)
  def exception(name: String, ex: Throwable): Field = keyValue(name, ex)
  def onlyException(value: Throwable): java.util.List[Field] = only(exception(value))
  def onlyException(name: String, value: Throwable): java.util.List[Field] = only(exception(name, value))

  // ------------------------------------------------------------------
  // object

  def list(fields: Field*): util.List[Field] = fields.asJava

  def only(field: Field): util.List[Field] = util.Arrays.asList(field)
}

object FieldBuilder extends FieldBuilder