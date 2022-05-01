package com.tersesystems.echopraxia.scala.async

import com.tersesystems.echopraxia.api.{Caller, CoreLoggerFactory}
import com.tersesystems.echopraxia.scala.api.FieldBuilder

/**
 * Async Logger Factory with source code enabled.
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
