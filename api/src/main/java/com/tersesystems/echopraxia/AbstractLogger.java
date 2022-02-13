package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.tersesystems.echopraxia.Level.*;
import static com.tersesystems.echopraxia.Level.ERROR;

public abstract class AbstractLogger<FB extends Field.Builder, SELF> implements LoggerLike<FB, SELF>  {
  protected final CoreLogger core;
  protected final FB fieldBuilder;

  protected AbstractLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    this.core = core;
    this.fieldBuilder = fieldBuilder;
  }

  @NotNull
  protected abstract SELF newLogger(CoreLogger coreLogger);

  @Override
  @NotNull
  public SELF withFields(@NotNull Field.BuilderFunction<FB> f) {
    return newLogger(core().withFields(f, fieldBuilder));
  }

  @Override
  @NotNull
  public SELF withThreadContext() {
    Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform =
            mapSupplier ->
                    () -> {
                      List<Field> list = new ArrayList<>();
                      for (Map.Entry<String, String> e : mapSupplier.get().entrySet()) {
                        Field string = fieldBuilder.string(e.getKey(), e.getValue());
                        list.add(string);
                      }
                      return list;
                    };
    return newLogger(core().withThreadContext(mapTransform));
  }

  @Override
  public @NotNull String getName() {
    return core.getName();
  }

  /** @return the internal core logger. */
  @Override
  @NotNull
  public CoreLogger core() {
    return core;
  }

  /** @return the field builder. */
  @Override
  @NotNull
  public FB fieldBuilder() {
    return fieldBuilder;
  }

  // ------------------------------------------------------------------------
  // TRACE

  /** @return true if the logger level is TRACE or higher. */
  public boolean isTraceEnabled() {
    return core().isEnabled(TRACE);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is TRACE or higher and the condition is met.
   */
  public boolean isTraceEnabled(@NotNull Condition condition) {
    return core().isEnabled(TRACE, condition);
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  @Override
  public void trace(@Nullable String message) {
    core().log(TRACE, message);
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void trace(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
    core().log(TRACE, message, f, fieldBuilder);
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void trace(@Nullable String message, @NotNull Throwable e) {
    core().log(TRACE, message, e);
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void trace(@NotNull Condition condition, @Nullable String message) {
    core().log(TRACE, condition, message);
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void trace(
          @NotNull Condition condition,
          @Nullable String message,
          @NotNull Field.BuilderFunction<FB> f) {
    core().log(TRACE, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(TRACE, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // DEBUG

  /** @return true if the logger level is DEBUG or higher. */
  public boolean isDebugEnabled() {
    return core().isEnabled(DEBUG);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is DEBUG or higher and the condition is met.
   */
  public boolean isDebugEnabled(@NotNull Condition condition) {
    return core().isEnabled(DEBUG, condition);
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  @Override
  public void debug(@Nullable String message) {
    core().log(DEBUG, message);
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void debug(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(DEBUG, message, f, fieldBuilder);
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void debug(@Nullable String message, @NotNull Throwable e) {
    core().log(DEBUG, message, e);
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void debug(@NotNull Condition condition, @Nullable String message) {
    core().log(DEBUG, condition, message);
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void debug(
          @NotNull Condition condition,
          @Nullable String message,
          @NotNull Field.BuilderFunction<FB> f) {
    core().log(DEBUG, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(DEBUG, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // INFO

  /** @return true if the logger level is INFO or higher. */
  public boolean isInfoEnabled() {
    return core().isEnabled(INFO);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is INFO or higher and the condition is met.
   */
  public boolean isInfoEnabled(@NotNull Condition condition) {
    return core().isEnabled(INFO, condition);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  @Override
  public void info(@Nullable String message) {
    core().log(INFO, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void info(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(INFO, message, f, fieldBuilder);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void info(@Nullable String message, @NotNull Throwable e) {
    core().log(INFO, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void info(@NotNull Condition condition, @Nullable String message) {
    core().log(INFO, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void info(
          @NotNull Condition condition,
          @Nullable String message,
          @NotNull Field.BuilderFunction<FB> f) {
    core().log(INFO, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(INFO, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // WARN

  /** @return true if the logger level is WARN or higher. */
  public boolean isWarnEnabled() {
    return core().isEnabled(WARN);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is WARN or higher and the condition is met.
   */
  public boolean isWarnEnabled(@NotNull Condition condition) {
    return core().isEnabled(WARN, condition);
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  public void warn(@Nullable String message) {
    core().log(WARN, message);
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void warn(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(WARN, message, f, fieldBuilder);
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void warn(@Nullable String message, @NotNull Throwable e) {
    core().log(WARN, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void warn(@NotNull Condition condition, @Nullable String message) {
    core().log(WARN, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void warn(
          @NotNull Condition condition,
          @Nullable String message,
          @NotNull Field.BuilderFunction<FB> f) {
    core().log(WARN, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(WARN, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // ERROR

  /** @return true if the logger level is ERROR or higher. */
  public boolean isErrorEnabled() {
    return core().isEnabled(ERROR);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is ERROR or higher and the condition is met.
   */
  public boolean isErrorEnabled(@NotNull Condition condition) {
    return core().isEnabled(ERROR, condition);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  @Override
  public void error(@Nullable String message) {
    core().log(ERROR, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void error(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(ERROR, message, f, fieldBuilder);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void error(@Nullable String message, @NotNull Throwable e) {
    core().log(ERROR, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void error(@NotNull Condition condition, @Nullable String message) {
    core().log(ERROR, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void error(
          @NotNull Condition condition,
          @Nullable String message,
          @NotNull Field.BuilderFunction<FB> f) {
    core().log(ERROR, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(ERROR, condition, message, e);
  }

}
