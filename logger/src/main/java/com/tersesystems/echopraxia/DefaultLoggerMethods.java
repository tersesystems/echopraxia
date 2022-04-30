package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.api.Field.EXCEPTION;
import static com.tersesystems.echopraxia.api.Level.*;
import static com.tersesystems.echopraxia.api.Level.ERROR;
import static com.tersesystems.echopraxia.api.Value.exception;
import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.DefaultMethodsSupport;
import com.tersesystems.echopraxia.api.Field;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Logger methods that delegate to `core.log` by default.
 *
 * @param <FB> the field builder type
 */
public interface DefaultLoggerMethods<FB, RET>
    extends LoggerMethods<FB, RET>, DefaultMethodsSupport<FB> {

  // ------------------------------------------------------------------------
  // TRACE

  /** @return true if the logger level is TRACE or higher. */
  default boolean isTraceEnabled() {
    return core().isEnabled(TRACE);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is TRACE or higher and the condition is met.
   */
  default boolean isTraceEnabled(@NotNull Condition condition) {
    return core().isEnabled(TRACE, condition);
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  default void trace(@Nullable String message) {
    core().log(TRACE, message);
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void trace(@Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(TRACE, message, f, fieldBuilder());
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void trace(@Nullable String message, @NotNull Throwable e) {
    core()
        .log(
            TRACE,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void trace(@NotNull Condition condition, @Nullable String message) {
    core().log(TRACE, condition, message);
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void trace(
      @NotNull Condition condition, @Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(TRACE, condition, message, f, fieldBuilder());
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
        .log(
            TRACE,
            condition,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // DEBUG

  /** @return true if the logger level is DEBUG or higher. */
  default boolean isDebugEnabled() {
    return core().isEnabled(DEBUG);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is DEBUG or higher and the condition is met.
   */
  default boolean isDebugEnabled(@NotNull Condition condition) {
    return core().isEnabled(DEBUG, condition);
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  default void debug(@Nullable String message) {
    core().log(DEBUG, message);
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void debug(@Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(DEBUG, message, f, fieldBuilder());
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void debug(@Nullable String message, @NotNull Throwable e) {
    core()
        .log(
            DEBUG,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void debug(@NotNull Condition condition, @Nullable String message) {
    core().log(DEBUG, condition, message);
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
        .log(
            DEBUG,
            condition,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void debug(
      @NotNull Condition condition, @Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(DEBUG, condition, message, f, fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // INFO

  /** @return true if the logger level is INFO or higher. */
  default boolean isInfoEnabled() {
    return core().isEnabled(INFO);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is INFO or higher and the condition is met.
   */
  default boolean isInfoEnabled(@NotNull Condition condition) {
    return core().isEnabled(INFO, condition);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  default void info(@Nullable String message) {
    core().log(INFO, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void info(@Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(INFO, message, f, fieldBuilder());
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void info(@Nullable String message, @NotNull Throwable e) {
    core()
        .log(
            INFO,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void info(@NotNull Condition condition, @Nullable String message) {
    core().log(INFO, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void info(
      @NotNull Condition condition, @Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(INFO, condition, message, f, fieldBuilder());
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
        .log(
            INFO,
            condition,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // WARN

  /** @return true if the logger level is WARN or higher. */
  default boolean isWarnEnabled() {
    return core().isEnabled(WARN);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is WARN or higher and the condition is met.
   */
  default boolean isWarnEnabled(@NotNull Condition condition) {
    return core().isEnabled(WARN, condition);
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  default void warn(@Nullable String message) {
    core().log(WARN, message);
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void warn(@Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(WARN, message, f, fieldBuilder());
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void warn(@Nullable String message, @NotNull Throwable e) {
    core()
        .log(
            WARN,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void warn(@NotNull Condition condition, @Nullable String message) {
    core().log(WARN, condition, message);
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
        .log(
            WARN,
            condition,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void warn(
      @NotNull Condition condition, @Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(WARN, condition, message, f, fieldBuilder());
  }

  // ------------------------------------------------------------------------
  // ERROR

  /** @return true if the logger level is ERROR or higher. */
  default boolean isErrorEnabled() {
    return core().isEnabled(ERROR);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is ERROR or higher and the condition is met.
   */
  default boolean isErrorEnabled(@NotNull Condition condition) {
    return core().isEnabled(ERROR, condition);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  default void error(@Nullable String message) {
    core().log(ERROR, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  default void error(@Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(ERROR, message, f, fieldBuilder());
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  default void error(@Nullable String message, @NotNull Throwable e) {
    core()
        .log(
            ERROR,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  default void error(@NotNull Condition condition, @Nullable String message) {
    core().log(ERROR, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  default void error(
      @NotNull Condition condition, @Nullable String message, @NotNull Function<FB, RET> f) {
    core().log(ERROR, condition, message, f, fieldBuilder());
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
        .log(
            ERROR,
            condition,
            message,
            fb -> singletonList(Field.keyValue(EXCEPTION, exception(e))),
            fieldBuilder());
  }
}
