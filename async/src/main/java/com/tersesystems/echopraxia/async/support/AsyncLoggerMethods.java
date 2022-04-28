package com.tersesystems.echopraxia.async.support;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggerHandle;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Logging method API used by the AsyncLogger.
 *
 * @param <FB> the field builder type.
 */
public interface AsyncLoggerMethods<FB> {

  void trace(@NotNull Consumer<LoggerHandle<FB>> consumer);

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void trace(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);

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

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  void debug(@NotNull Consumer<LoggerHandle<FB>> consumer);

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void debug(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);

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

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void info(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);

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
  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  void warn(@NotNull Consumer<LoggerHandle<FB>> consumer);

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void warn(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);

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

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  void error(@NotNull Consumer<LoggerHandle<FB>> consumer);

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void error(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);
}
