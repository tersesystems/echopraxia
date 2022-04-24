package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia.Field
import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.support._

import scala.compat.java8.FunctionConverters._

/**
 * An asynchronous logger that can use scala tuples and types.
 */
final class AsyncLogger[FB <: FieldBuilder](core: CoreLogger, fieldBuilder: FB)
    extends AbstractLoggerSupport[FB](core, fieldBuilder)
    with LoggerSupport[FB]
    with DefaultAsyncLoggerMethods[FB] {

  type SELF[T <: FieldBuilder] = AsyncLogger[T]

  @inline
  override def name: String = core.getName

  @inline
  override def withCondition(scalaCondition: Condition): SELF[FB] = newLogger(
    core.withCondition(scalaCondition.asJava)
  )

  @inline
  override def withFields(f: FB => java.util.List[Field]): SELF[FB] = {
    newLogger(core.withFields[FB](f.asJava, fieldBuilder))
  }

  @inline
  override def withThreadContext: SELF[FB] = newLogger(
    core.withThreadContext(Utilities.getThreadContextFunction)
  )

  @inline
  override def withFieldBuilder[T <: FieldBuilder](newBuilderClass: Class[T]): SELF[T] =
    newLogger[T](Utilities.getNewInstance(newBuilderClass))

  @inline
  override def withFieldBuilder[T <: FieldBuilder](newBuilder: T): SELF[T] =
    new AsyncLogger(core, newBuilder)

  @inline
  private def newLogger[T <: FieldBuilder](fieldBuilder: T): SELF[T] =
    new AsyncLogger(core, fieldBuilder)

  @inline
  private def newLogger(coreLogger: CoreLogger): SELF[FB] =
    new AsyncLogger(coreLogger, fieldBuilder)

}
