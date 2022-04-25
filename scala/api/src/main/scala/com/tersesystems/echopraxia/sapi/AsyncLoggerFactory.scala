package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia.core.{Caller, CoreLoggerFactory}
import com.tersesystems.echopraxia.sapi.support.DefaultAsyncLoggerMethods

/**
 * A factory for scala async loggers.
 */
object AsyncLoggerFactory {
  val FQCN: String = classOf[DefaultAsyncLoggerMethods[_]].getName

  val fieldBuilder: FieldBuilder = new FieldBuilder {}

  def getLogger(name: String): AsyncLogger[FieldBuilder] = {
    val core = CoreLoggerFactory.getLogger(FQCN, name)
    new AsyncLogger(core, fieldBuilder)
  }

  def getLogger(clazz: Class[_]): AsyncLogger[FieldBuilder] = {
    val core = CoreLoggerFactory.getLogger(FQCN, clazz.getName)
    new AsyncLogger(core, fieldBuilder)
  }

  def getLogger: AsyncLogger[FieldBuilder] = {
    val core = CoreLoggerFactory.getLogger(FQCN, Caller.resolveClassName)
    new AsyncLogger(core, fieldBuilder)
  }

}