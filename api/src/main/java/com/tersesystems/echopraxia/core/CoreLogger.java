package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  boolean isEnabled(@NotNull Level level);

  /**
   * Is the logger instance enabled for the given level and conditions?
   *
   * @param level the level to log at.
   * @param condition the explicit condition that must be met.
   * @return true if the instance is enabled for the given level, false otherwise.
   */
  boolean isEnabled(@NotNull Level level, @NotNull Condition condition);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param message the message string to be logged, may be null.
   */
  void log(@NotNull Level level, @Nullable String message);

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
      @NotNull Level level,
      @Nullable String message,
      @NotNull Field.BuilderFunction<B> f,
      @NotNull B builder);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param message the message string to be logged
   * @param e the exception (throwable) to log
   */
  void log(@NotNull Level level, @Nullable String message, @NotNull Throwable e);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param condition the given condition
   * @param message the message string to be logged
   */
  void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message);

  /**
   * Log a message at the given level.
   *
   * @param level the level to log at.
   * @param condition the given condition
   * @param message the message string to be logged
   * @param e the exception (throwable) to log
   */
  void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull String message,
      @NotNull Throwable e);

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
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Field.BuilderFunction<B> f,
      @NotNull B builder);

  /**
   * Returns the given condition
   *
   * @return the given condition.
   */
  @NotNull
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
  @NotNull
  <B extends Field.Builder> CoreLogger withFields(
      @NotNull Field.BuilderFunction<B> f, @NotNull B builder);

  /**
   * Pulls fields from thread context into logger context, if any exist and the implementation
   * supports it. The implementation supplies the map, and the logger supplies the list of fields.
   *
   * @param mapTransform a function that takes a context map and returns a list of fields.
   * @return the core logger with any thread context mapped into fields.
   */
  @NotNull
  CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform);

  /**
   * Adds the given condition to the logger.
   *
   * @param condition the given condition
   * @return the core logger with the condition applied.
   */
  @NotNull
  CoreLogger withCondition(@NotNull Condition condition);
}
