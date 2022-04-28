package com.tersesystems.echopraxia.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
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
   * The name of the logger.
   *
   * @return logger name.
   */
  @NotNull
  String getName();

  /**
   * Returns the given condition.
   *
   * @return the given condition.
   */
  @NotNull
  Condition condition();

  /**
   * Returns the fully qualified caller name.
   *
   * @return the fully qualified caller name.
   */
  @NotNull
  String fqcn();

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
  <B> CoreLogger withFields(@NotNull Function<B, List<Field>> f, @NotNull B builder);

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
   * Provides a function to be run in the async logger to set up thread local storage variables in
   * the logging executor's thread. Any existing function on the core logger is composed with the
   * given function.
   *
   * <p>The method to supply is in two parts, with the supply portion run to save off the TLS
   * variables, and the runnable portion applying the TLS variables in the thread:
   *
   * @param newSupplier the function to apply to manage TLS state.
   * @return the core logger with the thread local supplier applied.
   */
  @NotNull
  CoreLogger withThreadLocal(Supplier<Runnable> newSupplier);

  /**
   * Adds the given condition to the logger.
   *
   * @param condition the given condition
   * @return the core logger with the condition applied.
   */
  @NotNull
  CoreLogger withCondition(@NotNull Condition condition);

  /**
   * Returns a logger with the given executor for asynchronous logging.
   *
   * @param executor the executor to use.
   * @return the core logger with the executor applied.
   */
  @NotNull
  CoreLogger withExecutor(@NotNull Executor executor);

  /**
   * Returns a logger with the given fully qualified caller name.
   *
   * @param fqcn the fully qualified class name, or null.
   * @return the core logger with the fully qualified class name applied.
   */
  @NotNull
  CoreLogger withFQCN(@NotNull String fqcn);

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
   * @param <FB> the type of field builder.
   */
  <FB> void log(
      @NotNull Level level,
      @Nullable String message,
      @NotNull Function<FB, List<Field>> f,
      @NotNull FB builder);

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
   * @param condition the given condition.
   * @param message the message string to be logged
   * @param f the field builder function
   * @param builder the field builder
   * @param <FB> the type of field builder.
   */
  <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, List<Field>> f,
      @NotNull FB builder);

  /**
   * Logs a statement asynchronously using an executor.
   *
   * @param <FB> the field builder type
   * @param level the logging level
   * @param consumer the consumer of the logger handle
   * @param builder the field builder.
   */
  <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder);

  /**
   * Logs a statement asynchronously using an executor and the given condition.
   *
   * @param <FB> the field builder type
   * @param level the logging level
   * @param condition the condition
   * @param consumer the consumer of the logger handle
   * @param builder the field builder.
   */
  <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder);
}
