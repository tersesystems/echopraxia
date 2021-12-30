package com.tersesystems.echopraxia;

/**
 * The core logger API.
 *
 * <p>This is internal, and is intended for service provider implementations.
 *
 * <p>Using this in an end user application typically means you're casting to the implementation and
 * doing some Logback/Log4J stuff under the hood.
 */
public interface CoreLogger {

  /**
   * Is the logger instance enabled for the given level and logger conditions?
   *
   * @param level the level to log at.
   * @return true if the instance is enabled for the given level, false otherwise.
   */
  boolean isEnabled(Level level);

  /**
   * Is the logger instance enabled for the given level and conditions?
   *
   * @param level the level to log at.
   * @param condition the explicit condition that must be met.
   * @return true if the instance is enabled for the given level, false otherwise.
   */
  boolean isEnabled(Level level, Condition condition);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param message the message string to be logged
   */
  void log(Level level, String message);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param message the message string to be logged
   * @param f the field builder function
   * @param builder the field builder
   * @param <B> the type of field builder.
   */
  <B extends Field.Builder> void log(
      Level level, String message, Field.BuilderFunction<B> f, B builder);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param message the message string to be logged
   * @param e the exception (throwable) to log
   */
  void log(Level level, String message, Throwable e);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param condition the given condition
   * @param message the message string to be logged
   */
  void log(Level level, Condition condition, String message);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param condition the given condition
   * @param message the message string to be logged
   * @param e the exception (throwable) to log
   */
  void log(Level level, Condition condition, String message, Throwable e);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param condition the given condition.
   * @param message the message string to be logged
   * @param f the field builder function
   * @param builder the field builder
   * @param <B> the type of field builder.
   */
  <B extends Field.Builder> void log(
      Level level, Condition condition, String message, Field.BuilderFunction<B> f, B builder);

  /**
   * Returns the given condition
   *
   * @return the given condition.
   */
  Condition condition();

  /**
   * Adds the given fields to the logger context. The given function will be evaluated only, when
   * logging. If you are dependent on thread local data, you should check that you only call the
   * logger when appropriate.
   *
   * <p>That is, don't put something like this in your function:
   *
   * <p>{@code HttpServletRequest request = ((ServletRequestAttributes)
   * RequestContextHolder.getRequestAttributes()).getRequest();}
   *
   * <p>if you're exposing your logger outside the context of an HTTP request.
   *
   * @param f the field builder function
   * @param builder the field builder
   * @param <B> the type of field builder.
   * @return the core logger with given context fields applied.
   */
  <B extends Field.Builder> CoreLogger withFields(Field.BuilderFunction<B> f, B builder);

  /**
   * Adds the given condition to the logger.
   *
   * @param condition the given condition
   * @return the core logger with the condition applied.
   */
  CoreLogger withCondition(Condition condition);
}
