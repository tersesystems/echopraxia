package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.Level._
import com.tersesystems.echopraxia.sapi._
import com.tersesystems.echopraxia.{Field, KeyValueField, LoggerHandle}

import java.util
import java.util.function.Consumer
import scala.compat.java8.FunctionConverters._

trait DefaultAsyncLoggerMethods[FB] extends AsyncLoggerMethods[FB] {
  self: DefaultMethodsSupport[FB] =>

  // ------------------------------------------------------------------------
  // TRACE

  /**
   * Logs using a logger handle at TRACE level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def trace(consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(TRACE, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def trace(c: Condition, consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(TRACE, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  def trace(message: String): Unit = {
    core.asyncLog(TRACE, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def trace(message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(TRACE, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def trace(message: String, e: Throwable): Unit = {
    core.asyncLog(
      TRACE,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  def trace(condition: Condition, message: String): Unit = {
    core.asyncLog(TRACE, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  def trace(condition: Condition, message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(TRACE, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  def trace(condition: Condition, message: String, e: Throwable): Unit = {
    core.asyncLog(
      TRACE,
      condition.asJava,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  // ------------------------------------------------------------------------
  // DEBUG

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def debug(consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(DEBUG, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def debug(c: Condition, consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(DEBUG, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  def debug(message: String): Unit = {
    core.asyncLog(DEBUG, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def debug(message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(DEBUG, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def debug(message: String, e: Throwable): Unit = {
    core.asyncLog(
      DEBUG,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  def debug(condition: Condition, message: String): Unit = {
    core.asyncLog(DEBUG, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  def debug(condition: Condition, message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(DEBUG, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  def debug(condition: Condition, message: String, e: Throwable): Unit = {
    core.asyncLog(
      DEBUG,
      condition.asJava,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  // ------------------------------------------------------------------------
  // INFO

  /**
   * Logs using a logger handle at INFO level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def info(consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(INFO, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def info(c: Condition, consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(INFO, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  def info(message: String): Unit = {
    core.asyncLog(INFO, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def info(message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(INFO, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def info(message: String, e: Throwable): Unit = {
    core.asyncLog(
      INFO,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message   the message.
   */
  def info(condition: Condition, message: String): Unit = {
    core.asyncLog(INFO, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param f         the field builder function.
   */
  def info(condition: Condition, message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(INFO, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message   the message.
   * @param e         the given exception.
   */
  def info(condition: Condition, message: String, e: Throwable): Unit = {
    core.asyncLog(
      INFO,
      condition.asJava,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  // ------------------------------------------------------------------------
  // WARN

  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def warn(consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(WARN, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def warn(c: Condition, consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(WARN, c.asJava, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  def warn(message: String): Unit = {
    core.asyncLog(WARN, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f       the field builder function.
   */
  def warn(message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(WARN, toConsumer(message, f), fieldBuilder)
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e       the given exception.
   */
  def warn(message: String, e: Throwable): Unit = {
    core.asyncLog(
      WARN,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  def warn(condition: Condition, message: String): Unit = {
    core.asyncLog(WARN, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  def warn(condition: Condition, message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(WARN, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  def warn(condition: Condition, message: String, e: Throwable): Unit = {
    core.asyncLog(
      WARN,
      condition.asJava,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  // ------------------------------------------------------------------------
  // ERROR

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def error(consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(ERROR, consumer.asJava, fieldBuilder)
  }

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def error(c: Condition, consumer: LoggerHandle[FB] => Unit): Unit = {
    core.asyncLog(ERROR, c.asJava, consumer.asJava, fieldBuilder)
  }

  def error(message: String): Unit = {
    core.asyncLog(ERROR, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  def error(message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(ERROR, toConsumer(message, f), fieldBuilder)
  }

  def error(message: String, e: Throwable): Unit = {
    core.asyncLog(
      ERROR,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  def error(condition: Condition, message: String): Unit = {
    core.asyncLog(ERROR, condition.asJava, (h: LoggerHandle[FB]) => h.log(message), fieldBuilder)
  }

  def error(condition: Condition, message: String, f: FB => java.util.List[Field]): Unit = {
    core.asyncLog(ERROR, condition.asJava, toConsumer(message, f), fieldBuilder)
  }

  def error(condition: Condition, message: String, e: Throwable): Unit = {
    core.asyncLog(
      ERROR,
      condition.asJava,
      (h: LoggerHandle[FB]) => h.log(message, (fb: FB) => onlyException(e)),
      fieldBuilder
    )
  }

  @inline
  private def toConsumer(
      message: String,
      f: FB => java.util.List[Field]
  ): Consumer[LoggerHandle[FB]] = h => h.log(message, f.asJava)

  private def onlyException(e: Throwable): java.util.List[Field] = {
    util.Arrays.asList(KeyValueField.create(Field.Builder.EXCEPTION, Field.Value.exception(e)))
  }

}
