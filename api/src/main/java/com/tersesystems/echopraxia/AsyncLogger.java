package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.Level.*;
import static com.tersesystems.echopraxia.Level.WARN;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

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
public class AsyncLogger<FB extends Field.Builder> {

  protected final CoreLogger core;
  protected final FB fieldBuilder;

  protected AsyncLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
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

  /**
   * Creates a new logger with the given condition.
   *
   * <p>Note that the condition is lazily evaluated on every logging statement.
   *
   * @param condition the given condition.
   * @return the new logger.
   */
  @NotNull
  public AsyncLogger<FB> withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    // Reduce allocation if we can help it
    final CoreLogger coreLogger = core().withCondition(condition);
    if (coreLogger == core()) {
      return this;
    }
    return new AsyncLogger<>(coreLogger, fieldBuilder);
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
  public AsyncLogger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
    return new AsyncLogger<>(core().withFields(f, fieldBuilder), fieldBuilder);
  }

  /**
   * Creates a new logger with context fields from thread context / MDC mapped as context fields.
   *
   * <p>Note that the context map is lazily evaluated on every logging statement.
   *
   * @return the new logger.
   */
  @NotNull
  public AsyncLogger<FB> withThreadContext() {
    Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform =
        mapSupplier ->
            () ->
                mapSupplier.get().entrySet().stream()
                    .map(e -> fieldBuilder.string(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
    return new AsyncLogger<>(core().withThreadContext(mapTransform), fieldBuilder);
  }

  /**
   * Logs using a logger handle at TRACE level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void trace(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at TRACE level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void trace(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(TRACE, c, consumer, fieldBuilder);
  }

  /**
   * Logs using a logger handle at DEBUG level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void debug(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at DEBUG level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void debug(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(DEBUG, c, consumer, fieldBuilder);
  }

  /**
   * Logs using a logger handle at INFO level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void info(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at INFO level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void info(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(INFO, c, consumer, fieldBuilder);
  }

  /**
   * Logs using a logger handle at WARN level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void warn(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at WARN level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void warn(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(WARN, c, consumer, fieldBuilder);
  }

  /**
   * Logs using a logger handle at ERROR level.
   *
   * @param consumer the consumer of the logger handle.
   */
  public void error(Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, consumer, fieldBuilder);
  }

  /**
   * Logs using a condition and a logger handle at ERROR level.
   *
   * @param c the condition
   * @param consumer the consumer of the logger handle.
   */
  public void error(Condition c, Consumer<LoggerHandle<FB>> consumer) {
    core().asyncLog(ERROR, c, consumer, fieldBuilder);
  }
}
