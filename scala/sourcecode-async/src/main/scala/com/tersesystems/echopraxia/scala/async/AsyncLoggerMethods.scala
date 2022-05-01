package com.tersesystems.echopraxia.scala.async

import com.tersesystems.echopraxia.api.{FieldBuilderResult, LoggerHandle}
import com.tersesystems.echopraxia.scala.api.Condition

/**
 * Async Logger Methods with source code implicits.
 */
trait AsyncLoggerMethods[FB] {

  // ------------------------------------------------------------------------
  // TRACE

  /**
   * Logs using a logger handle at TRACE level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def trace(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def trace(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  def trace(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

  // ------------------------------------------------------------------------
  // DEBUG

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def debug(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def debug(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  def debug(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

  // ------------------------------------------------------------------------
  // INFO

  /**
   * Logs using a logger handle at INFO level.
   *
   * @param consumer the consumer of the logger handle.
   */
  // INFO
  def info(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def info(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  def info(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

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
  ): Unit

  // ------------------------------------------------------------------------
  // WARN

  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def warn(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def warn(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  def warn(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

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
  ): Unit

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
  ): Unit

  def warn(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def warn(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def warn(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  // ------------------------------------------------------------------------
  // ERROR

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  def error(consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c        the condition
   * @param consumer the consumer of the logger handle.
   */
  def error(c: Condition, consumer: LoggerHandle[FB] => Unit)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def error(message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def error(message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def error(message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def error(condition: Condition, message: String)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def error(condition: Condition, message: String, f: FB => FieldBuilderResult)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit

  def error(condition: Condition, message: String, e: Throwable)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): Unit
}
