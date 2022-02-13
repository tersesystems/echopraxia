package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.Level.*;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An asynchronous echopraxia logger built around a LoggerHandle.
 *
 * <p>This class is useful when evaluating a condition would unreasonably block or otherwise be too
 * expensive to run inline with a business operation.
 *
 * <p>Instances of this class are usually created from {@code
 * LoggerFactory.getLogger().withExecutor(executor))}.
 *
 * @param <FB> the field builder type
 */
public class AsyncLogger<FB extends Field.Builder> extends AbstractLogger<AsyncLogger<FB>, FB> {

  protected AsyncLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(AsyncLogger.class, core, fieldBuilder);
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> AsyncLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (AsyncLogger<T>) this;
    }
    return new AsyncLogger<>(core(), newBuilder);
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> AsyncLogger<T> withFieldBuilder(
      @NotNull Class<T> newBuilderClass) {
    try {
      final T newInstance = newBuilderClass.getDeclaredConstructor().newInstance();
      return new AsyncLogger<>(core(), newInstance);
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected AsyncLogger<FB> newLogger(CoreLogger coreLogger) {
    return new AsyncLogger<>(coreLogger, fieldBuilder);
  }

  @Override
  protected AsyncLogger<FB> neverLogger(CoreLogger coreLogger) {
    return new NeverAsyncLogger<>(coreLogger, fieldBuilder);
  }

  /**
   * Creates a new logger with the given context fields.
   *
   * <p>Note that the field builder function is lazily evaluated on every logging statement.
   *
   * @param f the given function producing fields from a field builder.
   * @return the new logger.
   */
  @Override
  @NotNull
  public AsyncLogger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
    return newLogger(core().withFields(f, fieldBuilder));
  }

  /**
   * Creates a new logger with context fields from thread context / MDC mapped as context fields.
   *
   * <p>Note that the context map is lazily evaluated on every logging statement.
   *
   * @return the new logger.
   */
  @Override
  @NotNull
  public AsyncLogger<FB> withThreadContext() {
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

  public AsyncLogger<FB> withExecutor(Executor executor) {
    return newLogger(core().withExecutor(executor));
  }

  // ------------------------------------------------------------------------
  // TRACE

  /**
   * Logs using a logger handle at TRACE level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void trace(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void trace(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, c, consumer, fieldBuilder);
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  @Override
  public void trace(@Nullable String message) {
    asyncLog(TRACE, message);
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void trace(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
    asyncLog(TRACE, message, f);
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void trace(@Nullable String message, @NotNull Throwable e) {
    asyncLog(TRACE, message, e);
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void trace(@NotNull Condition condition, @Nullable String message) {
    asyncLog(TRACE, condition, message);
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
    asyncLog(TRACE, condition, message, f);
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
    asyncLog(TRACE, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // DEBUG

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void debug(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void debug(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, c, consumer, fieldBuilder);
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  @Override
  public void debug(@Nullable String message) {
    asyncLog(DEBUG, message);
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void debug(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    asyncLog(DEBUG, message, f);
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void debug(@Nullable String message, @NotNull Throwable e) {
    asyncLog(DEBUG, message, e);
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void debug(@NotNull Condition condition, @Nullable String message) {
    asyncLog(DEBUG, condition, message);
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
    asyncLog(DEBUG, condition, message, f);
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
    asyncLog(DEBUG, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // INFO

  /**
   * Logs using a logger handle at INFO level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void info(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void info(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, c, consumer, fieldBuilder);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  @Override
  public void info(@Nullable String message) {
    asyncLog(INFO, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void info(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    asyncLog(INFO, message, f);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void info(@Nullable String message, @NotNull Throwable e) {
    asyncLog(INFO, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void info(@NotNull Condition condition, @Nullable String message) {
    asyncLog(INFO, condition, message);
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
    asyncLog(INFO, condition, message, f);
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
    asyncLog(INFO, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // WARN

  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void warn(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void warn(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, c, consumer, fieldBuilder);
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  public void warn(@Nullable String message) {
    asyncLog(WARN, message);
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void warn(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    asyncLog(WARN, message, f);
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void warn(@Nullable String message, @NotNull Throwable e) {
    asyncLog(WARN, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void warn(@NotNull Condition condition, @Nullable String message) {
    asyncLog(WARN, condition, message);
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
    asyncLog(WARN, condition, message, f);
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
    asyncLog(WARN, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // ERROR

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void error(@NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void error(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, c, consumer, fieldBuilder);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  @Override
  public void error(@Nullable String message) {
    asyncLog(ERROR, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  @Override
  public void error(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
    asyncLog(ERROR, message, f);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  @Override
  public void error(@Nullable String message, @NotNull Throwable e) {
    asyncLog(ERROR, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  @Override
  public void error(@NotNull Condition condition, @Nullable String message) {
    asyncLog(ERROR, condition, message);
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
    asyncLog(ERROR, condition, message, f);
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
    asyncLog(ERROR, condition, message, e);
  }

  protected void asyncLog(@NotNull Level level, String message) {
    core.asyncLog(level, h -> h.log(message), fieldBuilder);
  }

  protected void asyncLog(@NotNull Level level, Condition condition, String message) {
    core.asyncLog(level, condition, h -> h.log(message), fieldBuilder);
  }

  protected void asyncLog(@NotNull Level level, String message, Throwable e) {
    core.asyncLog(level, h -> h.log(message, e), fieldBuilder);
  }

  protected void asyncLog(@NotNull Level level, Condition condition, String message, Throwable e) {
    core.asyncLog(level, condition, h -> h.log(message, e), fieldBuilder);
  }

  protected void asyncLog(@NotNull Level level, String message, Field.BuilderFunction<FB> f) {
    core.asyncLog(level, h -> h.log(message, f), fieldBuilder);
  }

  protected void asyncLog(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      Field.BuilderFunction<FB> f) {
    core.asyncLog(level, condition, h -> h.log(message, f), fieldBuilder);
  }
}
