package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.Level.*;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An echopraxia logger built around a field builder.
 *
 * <p>This class is explicitly designed to be subclassed so that end users can customize it and
 * avoid the parameterized type tax.
 *
 * <pre>{@code
 * public class MyLogger extends Logger<MyFieldBuilder> {
 *   protected MyLogger(CoreLogger core, MyFieldBuilder fieldBuilder) { super(core, fieldBuilder); }
 * }
 *
 * static class MyLoggerFactory {
 *   public static MyLogger getLogger() { return new MyLogger(CoreLoggerFactory.getLogger(), myFieldBuilder); }
 * }
 *
 * MyLogger logger = MyLoggerFactory.getLogger();
 * }</pre>
 *
 * @param <FB> the field builder type.
 */
public class Logger<FB extends Field.Builder> {

  protected final CoreLogger core;
  protected final FB fieldBuilder;

  protected Logger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    this.core = core;
    this.fieldBuilder = fieldBuilder;
  }

  /** @return the internal core logger. */
  @NotNull
  public CoreLogger core() {
    return core;
  }

  /** @return the field builder. */
  @NotNull
  public FB fieldBuilder() {
    return fieldBuilder;
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (Logger<T>) this;
    }
    return new Logger<>(core(), newBuilder);
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull Class<T> newBuilderClass) {
    try {
      final T newInstance = newBuilderClass.getDeclaredConstructor().newInstance();
      return new Logger<>(core(), newInstance);
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Creates a new logger with the given condition.
   *
   * <p>Note that the condition is lazily evaluated on every logging statement.
   *
   * @param condition the given condition.
   * @return the new logger.
   */
  @NotNull
  public Logger<FB> withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    // Reduce allocation if we can help it
    final CoreLogger coreLogger = core().withCondition(condition);
    if (coreLogger == core()) {
      return this;
    }
    return new Logger<>(coreLogger, fieldBuilder);
  }

  /**
   * Creates a new logger with the given context fields.
   *
   * <p>Note that the field builder function is lazily evaluated on every logging statement.
   *
   * @param f the given function producing fields from a field builder.
   * @return the new logger.
   */
  @NotNull
  public Logger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
    return new Logger<>(core().withFields(f, fieldBuilder), fieldBuilder);
  }

  /**
   * Creates a new logger with context fields from thread context / MDC mapped as context fields.
   *
   * <p>Note that the context map is lazily evaluated on every logging statement.
   *
   * @return the new logger.
   */
  @NotNull
  public Logger<FB> withThreadContext() {
    Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform =
        mapSupplier ->
            () ->
                mapSupplier.get().entrySet().stream()
                    .map(e -> fieldBuilder.string(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
    return new Logger<>(core().withThreadContext(mapTransform), fieldBuilder);
  }

  public Logger<FB> withExecutor(Executor executor) {
    return new Logger<>(core().withExecutor(executor), fieldBuilder);
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
  public void trace(@Nullable String message) {
    core().log(TRACE, message);
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void trace(@Nullable String message, Field.BuilderFunction<FB> f) {
    core().log(TRACE, message, f, fieldBuilder);
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void trace(@Nullable String message, @NotNull Throwable e) {
    core().log(TRACE, message, e);
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
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
  public void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(TRACE, condition, message, e);
  }

  public void asyncTrace(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, consumer, fieldBuilder);
  }

  public void asyncTrace(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, c, consumer, fieldBuilder);
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
  public void debug(@Nullable String message) {
    core().log(DEBUG, message);
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void debug(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(DEBUG, message, f, fieldBuilder);
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void debug(@Nullable String message, @NotNull Throwable e) {
    core().log(DEBUG, message, e);
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
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
  public void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(DEBUG, condition, message, e);
  }

  public void asyncDebug(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, consumer, fieldBuilder);
  }

  public void asyncDebug(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, c, consumer, fieldBuilder);
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
  public void info(@Nullable String message) {
    core().log(INFO, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void info(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(INFO, message, f, fieldBuilder);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void info(@Nullable String message, @NotNull Throwable e) {
    core().log(INFO, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
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
  public void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(INFO, condition, message, e);
  }

  public void asyncInfo(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, consumer, fieldBuilder);
  }

  public void asyncInfo(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, c, consumer, fieldBuilder);
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
  public void warn(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(WARN, message, f, fieldBuilder);
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void warn(@Nullable String message, @NotNull Throwable e) {
    core().log(WARN, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
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
  public void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(WARN, condition, message, e);
  }

  public void asyncWarn(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, consumer, fieldBuilder);
  }

  public void asyncWarn(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, c, consumer, fieldBuilder);
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
  public void error(@Nullable String message) {
    core().log(ERROR, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void error(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    core().log(ERROR, message, f, fieldBuilder);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void error(@Nullable String message, @NotNull Throwable e) {
    core().log(ERROR, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
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
  public void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    core().log(ERROR, condition, message, e);
  }

  public void asyncError(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, consumer, fieldBuilder);
  }

  public void asyncError(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, c, consumer, fieldBuilder);
  }
}
