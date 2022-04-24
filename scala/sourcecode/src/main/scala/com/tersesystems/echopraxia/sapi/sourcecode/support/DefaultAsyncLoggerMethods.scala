package com.tersesystems.echopraxia.sapi.sourcecode.support

import com.tersesystems.echopraxia.Level._
import com.tersesystems.echopraxia.sapi.{Condition, FieldBuilder}
import com.tersesystems.echopraxia.{Field, LoggerHandle}

import java.util.function.Consumer
import scala.compat.java8.FunctionConverters._

/**
 * Default Async Logger Methods with source code implicits.
 */
trait DefaultAsyncLoggerMethods[FB <: FieldBuilder] extends AsyncLoggerMethods[FB] {
  self: DefaultMethodsSupport[FB] =>

  protected def sourceInfoFields(fb: FB)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): java.util.List[Field] = {
    fb.onlyObj(
      "sourcecode" -> Seq(
        fb.string("file", file.value),
        fb.number("line", line.value),
        fb.string("enclosing", enc.value))
    )
  }

  /**
   * Logs using a logger handle at TRACE level.
   *
   * @param consumer the consumer of the logger handle.
   */
  // ------------------------------------------------------------------------
  // TRACE
  override def trace(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(TRACE, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  override def trace(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(TRACE, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  override def trace(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(TRACE, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  override def trace(message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(TRACE, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  override def trace(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        TRACE,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  override def trace(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(TRACE, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  override def trace(condition: Condition, message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(TRACE, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  override def trace(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        TRACE,
        condition.asJava,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  // DEBUG
  override def debug(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(DEBUG, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  override def debug(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(DEBUG, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  override def debug(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(DEBUG, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  override def debug(message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(DEBUG, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  override def debug(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        DEBUG,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  override def debug(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(DEBUG, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  override def debug(condition: Condition, message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(DEBUG, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  override def debug(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        DEBUG,
        condition.asJava,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Logs using a logger handle at INFO level.
   *
   * @param consumer the consumer of the logger handle.
   */
  // INFO
  override def info(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(INFO, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  override def info(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(INFO, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  override def info(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(INFO, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  override def info(message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(INFO, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  override def info(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        INFO,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  override def info(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(INFO, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  override def info(condition: Condition, message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(INFO, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  override def info(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        INFO,
        condition.asJava,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  // WARN
  override def warn(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(WARN, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  override def warn(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(WARN, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  override def warn(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(WARN, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  override def warn(message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(WARN, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  override def warn(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        WARN,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  override def warn(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(WARN, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  override def warn(condition: Condition, message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(WARN, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  override def warn(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        WARN,
        condition.asJava,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  // ERROR
  override def error(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(ERROR, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  override def error(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(ERROR, c.asJava, consumer.asJava, fieldBuilder)
  }

  override def error(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(ERROR, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  override def error(message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(ERROR, toConsumer(message, f), fieldBuilder)
  }

  override def error(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        ERROR,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  override def error(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(ERROR, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  override def error(condition: Condition, message: String, f: FB => java.util.List[Field])(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(ERROR, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  override def error(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit = {
    core
      .withFields((f => sourceInfoFields(f)).asJava, fieldBuilder)
      .asyncLog(
        ERROR,
        condition.asJava,
        (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => fb.onlyException(e)),
        fieldBuilder
      )
  }

  @inline
  private def toConsumer(
      message: String,
      f: FB => java.util.List[Field]
  ): Consumer[LoggerHandle[FB]] = h => h.log(message, f.asJava)

}
