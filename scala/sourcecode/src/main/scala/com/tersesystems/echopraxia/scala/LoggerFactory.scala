package com.tersesystems.echopraxia.scala

import com.tersesystems.echopraxia.api.{Caller, CoreLoggerFactory}
import com.tersesystems.echopraxia.scala.api.FieldBuilder

/**
 * LoggerFactory for a logger with source code enabled.
 */
object LoggerFactory {
  val FQCN: String = classOf[DefaultLoggerMethods[_]].getName

  val fieldBuilder: FieldBuilder = new FieldBuilder {}

  def getLogger(name: String): Logger[FieldBuilder] = {
    val core = CoreLoggerFactory.getLogger(FQCN, name)
    new Logger(core, fieldBuilder)
  }

  def getLogger(clazz: Class[_]): Logger[FieldBuilder] = {
    val core = CoreLoggerFactory.getLogger(FQCN, clazz.getName)
    new Logger(core, fieldBuilder)
  }

  def getLogger: Logger[FieldBuilder] = {
    val core = CoreLoggerFactory.getLogger(FQCN, Caller.resolveClassName)
    new Logger(core, fieldBuilder)
  }

}
