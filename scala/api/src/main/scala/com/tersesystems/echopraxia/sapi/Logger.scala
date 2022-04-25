package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia.Field
import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.support.{
  DefaultLoggerMethods,
  DefaultMethodsSupport,
  LoggerSupport,
  Utilities
}

import scala.compat.java8.FunctionConverters._

/**
 * A logger with support for scala values and tuples.
 */
final class Logger[FB <: FieldBuilder](val core: CoreLogger, val fieldBuilder: FB)
    extends DefaultLoggerMethods[FB]
    with LoggerSupport[FB]
    with DefaultMethodsSupport[FB] {

  type SELF[T <: FieldBuilder] = Logger[T]

  @inline
  override def name: String = core.getName

  @inline
  override def withCondition(condition: Condition): SELF[FB] = {
    newLogger(newCoreLogger = core.withCondition(condition.asJava))
  }

  @inline
  override def withFields(f: FB => java.util.List[Field]): SELF[FB] = {
    newLogger(newCoreLogger = core.withFields(f.asJava, fieldBuilder))
  }

  @inline
  override def withThreadContext: SELF[FB] = {
    newLogger(
      newCoreLogger = core.withThreadContext(Utilities.getThreadContextFunction)
    )
  }

  @inline
  override def withFieldBuilder[NEWFB <: FieldBuilder](newFieldBuilder: NEWFB): SELF[NEWFB] = {
    newLogger(newFieldBuilder = newFieldBuilder)
  }

  @inline
  override def withFieldBuilder[T <: FieldBuilder](newBuilderClass: Class[T]): SELF[T] = {
    newLogger[T](newFieldBuilder = Utilities.getNewInstance(newBuilderClass))
  }

  @inline
  private def newLogger[T <: FieldBuilder](
      newCoreLogger: CoreLogger = core,
      newFieldBuilder: T = fieldBuilder
  ): SELF[T] =
    new Logger[T](newCoreLogger, newFieldBuilder)

}
