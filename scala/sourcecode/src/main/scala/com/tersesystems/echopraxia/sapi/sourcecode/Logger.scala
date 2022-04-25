package com.tersesystems.echopraxia.sapi.sourcecode

import com.tersesystems.echopraxia.{Field, KeyValueField}
import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.support.Utilities
import com.tersesystems.echopraxia.sapi.{Condition, FieldBuilder}
import sourcecode.{Enclosing, File, Line}
import support._

import scala.compat.java8.FunctionConverters.enrichAsJavaFunction

/** 
 * Logger with source code implicit parameters.
 */
final class Logger[FB](core: CoreLogger, fieldBuilder: FB)
    extends AbstractLoggerSupport(core, fieldBuilder)
    with LoggerSupport[FB]
    with DefaultLoggerMethods[FB] {

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
