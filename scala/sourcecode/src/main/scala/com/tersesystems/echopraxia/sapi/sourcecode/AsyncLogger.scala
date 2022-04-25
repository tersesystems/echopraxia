package com.tersesystems.echopraxia.sapi.sourcecode

import support._
import com.tersesystems.echopraxia.Field
import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.support.Utilities
import com.tersesystems.echopraxia.sapi.{Condition, FieldBuilder}
import sourcecode.{Enclosing, File, Line}

import java.util
import scala.compat.java8.FunctionConverters._

/**
 * Async Logger with source code enabled.
 */
final class AsyncLogger[FB <: FieldBuilder](core: CoreLogger, fieldBuilder: FB)
    extends AbstractLoggerSupport[FB](core, fieldBuilder)
    with LoggerSupport[FB]
    with DefaultAsyncLoggerMethods[FB] {

  @inline
  override def name: String = core.getName

  @inline
  override def withCondition(scalaCondition: Condition): AsyncLogger[FB] = newLogger(
    newCoreLogger = core.withCondition(scalaCondition.asJava)
  )

  @inline
  override def withFields(f: FB => java.util.List[Field]): AsyncLogger[FB] = {
    newLogger(newCoreLogger = core.withFields[FB](f.asJava, fieldBuilder))
  }

  @inline
  override def withThreadContext: AsyncLogger[FB] = newLogger(
    newCoreLogger = core.withThreadContext(Utilities.getThreadContextFunction)
  )

  @inline
  override def withFieldBuilder[T <: FieldBuilder](newBuilderClass: Class[T]): AsyncLogger[T] =
    newLogger[T](newFieldBuilder = Utilities.getNewInstance(newBuilderClass))

  @inline
  override def withFieldBuilder[T <: FieldBuilder](newBuilder: T): AsyncLogger[T] =
    newLogger(newFieldBuilder = newBuilder)

  @inline
  private def newLogger[T <: FieldBuilder](
      newCoreLogger: CoreLogger = core,
      newFieldBuilder: T = fieldBuilder
  ): AsyncLogger[T] =
    new AsyncLogger[T](newCoreLogger, newFieldBuilder)

  override protected def sourceInfoFields(fb: FB)(implicit line: Line, file: File, enc: Enclosing): util.List[Field] = {
    fb.onlyObj(
      "sourcecode" -> Seq(
        fb.string("file", file.value),
        fb.number("line", line.value),
        fb.string("enclosing", enc.value)
      ))
  }
}
