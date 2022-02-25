package com.tersesystems.echopraxia.support;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Logging Methods used in common between sync and async loggers.
 *
 * @param <FB> the field builder type.
 */
public interface BaseLoggerMethods<FB extends Field.Builder> {

  // ------------------------------------------------------------------------
  // TRACE

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  void trace(@Nullable String message);

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  void trace(@Nullable String message, Field.@NotNull BuilderFunction<FB> f);

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  void trace(@Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  void trace(@NotNull Condition condition, @Nullable String message);

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  void trace(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  // ------------------------------------------------------------------------
  // DEBUG

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  void debug(@Nullable String message);

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  void debug(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  void debug(@Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  void debug(@NotNull Condition condition, @Nullable String message);

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  void debug(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  // ------------------------------------------------------------------------
  // INFO

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  void info(@Nullable String message);

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  void info(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  void info(@Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  void info(@NotNull Condition condition, @Nullable String message);

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  void info(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  // ------------------------------------------------------------------------
  // WARN

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  void warn(@Nullable String message);

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  void warn(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  void warn(@Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  void warn(@NotNull Condition condition, @Nullable String message);

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  void warn(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  // ------------------------------------------------------------------------
  // ERROR

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  void error(@Nullable String message);

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  void error(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  void error(@Nullable String message, @NotNull Throwable e);

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  void error(@NotNull Condition condition, @Nullable String message);

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  void error(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);
}
