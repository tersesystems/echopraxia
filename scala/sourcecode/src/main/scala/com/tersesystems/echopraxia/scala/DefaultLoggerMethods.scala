package com.tersesystems.echopraxia.scala

import com.tersesystems.echopraxia.api.Level._
import com.tersesystems.echopraxia.api.{Field, FieldBuilderResult, Value}
import com.tersesystems.echopraxia.scala.api.{Condition, DefaultMethodsSupport}
import sourcecode.{Enclosing, File, Line}

import scala.compat.java8.FunctionConverters._

/**
 * Default Logger methods with source code implicits.
 *
 * This implementation uses the protected `sourceInfoFields` method to add
 * source code information as context fields, adding a `sourcecode` object
 * containing `line`, `file`, and `enclosing` fields.
 *
 * You can subclass this method and override `sourceInfoFields` to provide
 * your own implementation.
 */
trait DefaultLoggerMethods[FB] extends LoggerMethods[FB] {
  this: DefaultMethodsSupport[FB] =>

  protected def sourceInfoFields(
      fb: FB
  )(implicit line: Line, file: File, enc: Enclosing): FieldBuilderResult = {
    Field.keyValue(
      "sourcecode", // XXX make this configurable
      Value.`object`(
        Field.keyValue("file", Value.string(file.value)),
        Field.keyValue("line", Value.number(line.value)),
        Field.keyValue("enclosing", Value.string(enc.value))
      )
    )
  }

  /** @return true if the logger level is TRACE or higher. */
  def isTraceEnabled: Boolean = core.isEnabled(TRACE)

  /**
   * @param condition the given condition.
   * @return true if the logger level is TRACE or higher and the condition is met.
   */
  def isTraceEnabled(condition: Condition): Boolean = {
    core.isEnabled(TRACE, condition.asJava)
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  def trace(
      message: String
  )(implicit line: sourcecode.Line, file: sourcecode.File, enc: sourcecode.Enclosing): Unit = {
    core.withFields(sourceInfoFields, fieldBuilder).log(TRACE, message)
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def trace(message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(TRACE, message, f.asJava, fieldBuilder)
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def trace(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(TRACE, message, (fb: FB) => onlyException(e), fieldBuilder)
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  def trace(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(TRACE, condition.asJava, message)
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  def trace(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(TRACE, condition.asJava, message, f.asJava, fieldBuilder)
  }

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  def trace(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(TRACE, condition.asJava, message, (fb: FB) => onlyException(e), fieldBuilder)
  }

  /** @return true if the logger level is DEBUG or higher. */
  def isDebugEnabled: Boolean = core.isEnabled(DEBUG)

  /**
   * @param condition the given condition.
   * @return true if the logger level is DEBUG or higher and the condition is met.
   */
  def isDebugEnabled(condition: Condition): Boolean = core.isEnabled(DEBUG, condition.asJava)

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  def debug(
      message: String
  )(implicit line: sourcecode.Line, file: sourcecode.File, enc: sourcecode.Enclosing): Unit = {
    core.log(DEBUG, message)
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def debug(message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(DEBUG, message, f.asJava, fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def debug(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(DEBUG, message, (fb: FB) => onlyException(e), fieldBuilder)
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  def debug(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(DEBUG, condition.asJava, message)
  }

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  def debug(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(DEBUG, condition.asJava, message, (fb: FB) => onlyException(e), fieldBuilder)
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  def debug(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(DEBUG, condition.asJava, message, f.asJava, fieldBuilder)
  }

  /** @return true if the logger level is INFO or higher. */
  def isInfoEnabled: Boolean = core.isEnabled(INFO)

  /**
   * @param condition the given condition.
   * @return true if the logger level is INFO or higher and the condition is met.
   */
  def isInfoEnabled(condition: Condition): Boolean = core.isEnabled(INFO, condition.asJava)

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  def info(
      message: String
  )(implicit line: sourcecode.Line, file: sourcecode.File, enc: sourcecode.Enclosing): Unit = {
    core.withFields(sourceInfoFields, fieldBuilder).log(INFO, message)
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def info(message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(INFO, message, f.asJava, fieldBuilder)
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def info(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(INFO, message, ((fb: FB) => onlyException(e)), fieldBuilder)
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  def info(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(INFO, condition.asJava, message)
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  def info(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(INFO, condition.asJava, message, f.asJava, fieldBuilder)
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  def info(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(INFO, condition.asJava, message, ((fb: FB) => onlyException(e)), fieldBuilder)
  }

  /** @return true if the logger level is WARN or higher. */
  def isWarnEnabled: Boolean = core.isEnabled(WARN)

  /**
   * @param condition the given condition.
   * @return true if the logger level is WARN or higher and the condition is met.
   */
  def isWarnEnabled(condition: Condition): Boolean = core.isEnabled(WARN, condition.asJava)

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  def warn(
      message: String
  )(implicit line: sourcecode.Line, file: sourcecode.File, enc: sourcecode.Enclosing): Unit = {
    core.withFields(sourceInfoFields, fieldBuilder).log(WARN, message)
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def warn(message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(WARN, message, f.asJava, fieldBuilder)
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def warn(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(WARN, message, ((fb: FB) => onlyException(e)), fieldBuilder)
  }

  def warn(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(WARN, condition.asJava, message)
  }

  def warn(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(WARN, condition.asJava, message, ((fb: FB) => onlyException(e)), fieldBuilder)
  }

  def warn(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core.log(WARN, condition.asJava, message, f.asJava, fieldBuilder)
  }

  /** @return true if the logger level is ERROR or higher. */
  def isErrorEnabled: Boolean = core.isEnabled(ERROR)

  /**
   * @param condition the given condition.
   * @return true if the logger level is ERROR or higher and the condition is met.
   */
  def isErrorEnabled(condition: Condition): Boolean = core.isEnabled(ERROR, condition.asJava)

  def error(
      message: String
  )(implicit line: sourcecode.Line, file: sourcecode.File, enc: sourcecode.Enclosing): Unit = {
    core.withFields(sourceInfoFields, fieldBuilder).log(ERROR, message)
  }

  def error(message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(ERROR, message, f.asJava, fieldBuilder)
  }

  def error(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(ERROR, message, ((fb: FB) => onlyException(e)), fieldBuilder)
  }

  def error(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(ERROR, condition.asJava, message)
  }

  def error(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(ERROR, condition.asJava, message, f.asJava, fieldBuilder)
  }

  def error(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields(sourceInfoFields, fieldBuilder)
      .log(ERROR, condition.asJava, message, ((fb: FB) => onlyException(e)), fieldBuilder)
  }

  private def onlyException(e: Throwable): FieldBuilderResult = {
    (Field.keyValue(Field.EXCEPTION, Value.exception(e)))
  }

}
