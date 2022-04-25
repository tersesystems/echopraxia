package com.tersesystems.echopraxia.sapi.sourcecode

import com.tersesystems.echopraxia.Field
import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.support.Utilities
import com.tersesystems.echopraxia.sapi.{Condition, FieldBuilder}
import support._

import scala.compat.java8.FunctionConverters.enrichAsJavaFunction

/** 
 * Logger with source code implicit parameters.
 */
final class Logger[FB <: FieldBuilder](core: CoreLogger, fieldBuilder: FB)
    extends AbstractLoggerSupport(core, fieldBuilder)
    with LoggerSupport[FB]
    with DefaultLoggerMethods[FB] {

  override type SELF[F <: FieldBuilder] = Logger[F]

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
