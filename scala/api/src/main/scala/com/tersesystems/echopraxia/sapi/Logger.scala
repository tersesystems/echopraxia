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

  @inline
  override def name: String = core.getName

  @inline
  override def withCondition(condition: Condition): Logger[FB] = {
    newLogger(newCoreLogger = core.withCondition(condition.asJava))
  }

  @inline
  override def withFields(f: FB => java.util.List[Field]): Logger[FB] = {
    newLogger(newCoreLogger = core.withFields(f.asJava, fieldBuilder))
  }

  @inline
  override def withThreadContext: Logger[FB] = {
    newLogger(
      newCoreLogger = core.withThreadContext(Utilities.getThreadContextFunction)
    )
  }

  @inline
  override def withFieldBuilder[NEWFB <: FieldBuilder](newFieldBuilder: NEWFB): Logger[NEWFB] = {
    newLogger(newFieldBuilder = newFieldBuilder)
  }

  @inline
  override def withFieldBuilder[T <: FieldBuilder](newBuilderClass: Class[T]): Logger[T] = {
    newLogger[T](newFieldBuilder = Utilities.getNewInstance(newBuilderClass))
  }

  @inline
  private def newLogger[T <: FieldBuilder](
      newCoreLogger: CoreLogger = core,
      newFieldBuilder: T = fieldBuilder
  ): Logger[T] =
    new Logger[T](newCoreLogger, newFieldBuilder)

}
