package echopraxia.async;

import static echopraxia.api.FieldConstants.EXCEPTION;
import static echopraxia.api.Value.exception;
import static echopraxia.logging.api.Level.*;

import echopraxia.api.Field;
import echopraxia.api.FieldBuilderResult;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.LoggerHandle;
import echopraxia.logging.spi.DefaultMethodsSupport;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Async logger methods that implement the default `core.asyncLog` delegation.
 *
 * @param <FB> the field builder.
 */
public interface DefaultAsyncLoggerMethods<FB>
    extends AsyncLoggerMethods<FB>, DefaultMethodsSupport<FB> {

  // ------------------------------------------------------------------------
  // TRACE

  /**
   * Logs using a logger handle at TRACE level.
   *
   * @param consumer the consumer of the logger handle.
   */
  default void trace(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, consumer, fieldBuilder());
  }

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  default void trace(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, c, consumer, fieldBuilder());
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  default void trace(@Nullable String message) {
    core().asyncLog(TRACE, h -> h.log(message), fieldBuilder());
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void trace(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(TRACE, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void trace(@Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            TRACE,
            h -> h.log(message, fb -> Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void trace(@NotNull Condition condition, @Nullable String message) {
    core().asyncLog(TRACE, condition, h -> h.log(message), fieldBuilder());
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void trace(
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(TRACE, condition, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  default void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            TRACE,
            condition,
            h -> h.log(message, fb -> Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // DEBUG

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  default void debug(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, consumer, fieldBuilder());
  }

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  default void debug(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, c, consumer, fieldBuilder());
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  default void debug(@Nullable String message) {
    core().asyncLog(DEBUG, h -> h.log(message), fieldBuilder());
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void debug(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(DEBUG, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void debug(@Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            DEBUG,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void debug(@NotNull Condition condition, @Nullable String message) {
    core().asyncLog(DEBUG, condition, h -> h.log(message), fieldBuilder());
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void debug(
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(DEBUG, condition, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  default void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            DEBUG,
            condition,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // INFO

  /**
   * Logs using a logger handle at INFO level.
   *
   * @param consumer the consumer of the logger handle.
   */
  default void info(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, consumer, fieldBuilder());
  }

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  default void info(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, c, consumer, fieldBuilder());
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  default void info(@Nullable String message) {
    core().asyncLog(INFO, h -> h.log(message), fieldBuilder());
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void info(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(INFO, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void info(@Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            INFO,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void info(@NotNull Condition condition, @Nullable String message) {
    core().asyncLog(INFO, condition, h -> h.log(message), fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void info(
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(INFO, condition, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  default void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            INFO,
            condition,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // WARN

  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  default void warn(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, consumer, fieldBuilder());
  }

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  default void warn(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, c, consumer, fieldBuilder());
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  default void warn(@Nullable String message) {
    core().asyncLog(WARN, h -> h.log(message), fieldBuilder());
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void warn(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(WARN, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void warn(@Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            WARN,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void warn(@NotNull Condition condition, @Nullable String message) {
    core().asyncLog(WARN, condition, h -> h.log(message), fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void warn(
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(WARN, condition, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  default void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            WARN,
            condition,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // ERROR

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  default void error(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, consumer, fieldBuilder());
  }

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  default void error(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, c, consumer, fieldBuilder());
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  default void error(@Nullable String message) {
    core().asyncLog(ERROR, h -> h.log(message), fieldBuilder());
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void error(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(ERROR, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void error(@Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            ERROR,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void error(@NotNull Condition condition, @Nullable String message) {
    core().asyncLog(ERROR, condition, h -> h.log(message), fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void error(
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f) {
    core().asyncLog(ERROR, condition, (LoggerHandle<FB> h) -> h.log(message, f), fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  default void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core()
        .asyncLog(
            ERROR,
            condition,
            h -> h.log(message, fb -> (Field.keyValue(EXCEPTION, exception(e)))),
            fieldBuilder());
  }
}
