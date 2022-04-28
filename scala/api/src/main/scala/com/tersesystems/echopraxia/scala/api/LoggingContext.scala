package com.tersesystems.echopraxia.scala.api

import com.daodecode.scalaj.collection.immutable._
import com.tersesystems.echopraxia.api.{Field, LoggingContext => JLoggingContext}

import scala.compat.java8.OptionConverters._

object LoggingContext {

  // This repeats stuff in AbstractLoggingContext
  private val ExceptionPath = "$." + Field.EXCEPTION

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
    context.findObject(jsonPath).asScala.map(_.deepAsScalaImmutable)
  }

  def findList(jsonPath: String): Seq[Any] = {
    context.findList(jsonPath).deepAsScalaImmutable
  }

}
