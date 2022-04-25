package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia.Field
import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.support._

import scala.compat.java8.FunctionConverters._

/**
 * An asynchronous logger that can use scala tuples and types.
 */
final class AsyncLogger[FB](core: CoreLogger, fieldBuilder: FB)
    extends AbstractLoggerSupport[FB](core, fieldBuilder)
    with LoggerSupport[FB]
    with DefaultAsyncLoggerMethods[FB] {

  @inline
  override def name: String = core.getName

  @inline
  override def withCondition(scalaCondition: Condition): AsyncLogger[FB] = newLogger(
    core.withCondition(scalaCondition.asJava)
  )

  @inline
  override def withFields(f: FB => java.util.List[Field]): AsyncLogger[FB] = {
    newLogger(core.withFields[FB](f.asJava, fieldBuilder))
  }

  @inline
  override def withThreadContext: AsyncLogger[FB] = newLogger(
    core.withThreadContext(Utilities.getThreadContextFunction)
  )

  @inline
  override def withFieldBuilder[T](newBuilderClass: Class[T]): AsyncLogger[T] =
    newLogger[T](Utilities.getNewInstance(newBuilderClass))

  @inline
  override def withFieldBuilder[T](newBuilder: T): AsyncLogger[T] =
    new AsyncLogger(core, newBuilder)

  @inline
  private def newLogger[T](fieldBuilder: T): AsyncLogger[T] =
    new AsyncLogger(core, fieldBuilder)

  @inline
  private def newLogger(coreLogger: CoreLogger): AsyncLogger[FB] =
    new AsyncLogger(coreLogger, fieldBuilder)

}
