package com.tersesystems.echopraxia.support;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggerHandle;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Logging method API used by the AsyncLogger.
 *
 * @param <FB> the field builder type.
 */
public interface AsyncLoggerMethods<FB> extends BaseLoggerMethods<FB> {

  void trace(@NotNull Consumer<LoggerHandle<FB>> consumer);

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void trace(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);

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
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  void info(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer);

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
