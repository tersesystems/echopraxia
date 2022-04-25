package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia.core.{Caller, CoreLoggerFactory}
import com.tersesystems.echopraxia.sapi.support.DefaultLoggerMethods

/**
 * A logger factory that returns a logger that can use tuples and scala types.
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
