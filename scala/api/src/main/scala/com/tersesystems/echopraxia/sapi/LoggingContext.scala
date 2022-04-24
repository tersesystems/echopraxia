package com.tersesystems.echopraxia.sapi

import com.jayway.jsonpath.Predicate
import com.tersesystems.echopraxia.Field.Value
import com.tersesystems.echopraxia.Field.Value.{ArrayValue, ObjectValue}
import com.tersesystems.echopraxia.Field.Value.ValueType._
import com.tersesystems.echopraxia.{Field, LoggingContext => JLoggingContext}

import scala.compat.java8.OptionConverters._
import scala.jdk.CollectionConverters._

object LoggingContext {

  // This repeats stuff in AbstractLoggingContext
  private val ExceptionPath = "$." + Field.Builder.EXCEPTION

  def apply(context: JLoggingContext): LoggingContext = {
    new LoggingContext(context)
  }
}

/**
 * A scala logging context.
 */
class LoggingContext private (context: JLoggingContext) {

  def findString(jsonPath: String): Option[String] = {
    context.findString(jsonPath).asScala
  }

  def findBoolean(jsonPath: String): Option[Boolean] = {
    context.findBoolean(jsonPath).asScala.map(_.booleanValue())
  }

  def findNumber(jsonPath: String): Option[Number] = {
    context.findNumber(jsonPath).asScala
  }

  def findNull(jsonPath: String): Boolean = {
    context.findNull(jsonPath)
  }

  def findThrowable(jsonPath: String): Option[Throwable] = {
    context.findThrowable(jsonPath).asScala
  }

  def findThrowable: Option[Throwable] = {
    findThrowable(LoggingContext.ExceptionPath)
  }

  def findObject(jsonPath: String): Option[Map[String, Any]] = {
    context.findValue(jsonPath, classOf[Value.ObjectValue]).asScala.map(convertObject)
  }

  def findObject(jsonPath: String, predicates: Predicate*): Option[Map[String, Any]] = {
    context.findValue(jsonPath, classOf[Value.ObjectValue], predicates:_*).asScala.map(convertObject)
  }

  def findList(jsonPath: String): Seq[Any] = {
    context.findValue(jsonPath, classOf[Value.ArrayValue]).asScala.map(convertArray).getOrElse(Seq.empty)
  }

  def findList(jsonPath: String, predicates: Predicate*): Seq[Any] = {
    context.findValue(jsonPath, classOf[Value.ArrayValue], predicates:_*).asScala.map(convertArray).getOrElse(Seq.empty)
  }

  private def deepConvert(value: Field.Value[_]): Any = {
    value.`type` match {
      case STRING =>
        value.raw.asInstanceOf[String]
      case EXCEPTION =>
        value.raw().asInstanceOf[Throwable]
      case BOOLEAN =>
        value.raw.asInstanceOf[Boolean]
      case NUMBER =>
        value.raw.asInstanceOf[Number]
      case OBJECT =>
        convertObject(value.asInstanceOf[ObjectValue])
      case ARRAY =>
        convertArray(value.asInstanceOf[ArrayValue])
      case NULL =>
        null
    }
  }

  private def convertObject(objectValue: ObjectValue): Map[String, Any] = {
    objectValue.raw().asScala.map(f => f.name() -> deepConvert(f.value())).toMap
  }

  private def convertArray(arrayValue: ArrayValue): Seq[Any] = {
    arrayValue.raw().asScala.toSeq.map(deepConvert) // toSeq for 2.13 compat
  }

}
