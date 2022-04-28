package com.tersesystems.echopraxia.async;

import static com.tersesystems.echopraxia.api.Utilities.getNewInstance;

import com.tersesystems.echopraxia.api.AbstractLoggerSupport;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.LoggerHandle;
import com.tersesystems.echopraxia.async.support.DefaultAsyncLoggerMethods;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
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
 * AsyncLoggerFactory.getLogger().withExecutor(executor))}.
 *
 * @param <FB> the field builder type
 */
public class AsyncLogger<FB extends FieldBuilder> extends AbstractLoggerSupport<AsyncLogger<FB>, FB>
    implements DefaultAsyncLoggerMethods<FB> {

  protected AsyncLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder, AsyncLogger.class);
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends FieldBuilder> AsyncLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    return newLogger(newBuilder);
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends FieldBuilder> AsyncLogger<T> withFieldBuilder(
      @NotNull Class<T> newBuilderClass) {
    return newLogger(getNewInstance(newBuilderClass));
  }

  /**
   * Provides a function to be run in the async logger to set up thread local storage variables in
   * the logging executor's thread. Any existing function on the core logger is composed with the
   * given function.
   *
   * <p>The method to supply is in two parts, with the supply portion run to save off the TLS
   * variables, and the runnable portion applying the TLS variables in the thread:
   *
   * @param supplier the function to apply to manage TLS state.
   * @return the logger with the thread local supplier applied.
   */
  public AsyncLogger<FB> withThreadLocal(Supplier<Runnable> supplier) {
    //    Supplier<Runnable> s = () -> {
    //      // Run before async thread execution
    //      final RequestAttributes requestAttributes =
    // RequestContextHolder.currentRequestAttributes();
    //      // runnable.run() is called in the logging thread.
    //      return () -> {
    //        RequestContextHolder.setRequestAttributes(requestAttributes);
    //      };
    //    };
    return newLogger(core.withThreadLocal(supplier));
  }

  /**
   * Creates a new async logger with a new executor.
   *
   * @param executor the new executor.
   * @return the new async logger.
   */
  public AsyncLogger<FB> withExecutor(Executor executor) {
    return newLogger(core().withExecutor(executor));
  }

  protected @NotNull AsyncLogger<FB> newLogger(CoreLogger coreLogger) {
    return new AsyncLogger<>(coreLogger, fieldBuilder());
  }

  @NotNull
  protected <T extends FieldBuilder> AsyncLogger<T> newLogger(@NotNull T fieldBuilder) {
    if (this.fieldBuilder == fieldBuilder) {
      //noinspection unchecked
      return (AsyncLogger<T>) this;
    }
    return new AsyncLogger<>(core(), fieldBuilder);
  }

  @Override
  @NotNull
  protected AsyncLogger<FB> neverLogger() {
    return new NeverAsyncLogger<>(core().withCondition(Condition.never()), fieldBuilder);
  }

  // This must extend AsyncLogger so the return type is the same
  private static class NeverAsyncLogger<FB extends FieldBuilder> extends AsyncLogger<FB> {

    protected NeverAsyncLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
      super(core, fieldBuilder);
    }

    protected @NotNull AsyncLogger<FB> newLogger(CoreLogger coreLogger) {
      return this;
    }

    @Override
    public @NotNull AsyncLogger<FB> withThreadContext() {
      return this;
    }

    @Override
    public @NotNull <T extends FieldBuilder> AsyncLogger<T> withFieldBuilder(
        @NotNull T newBuilder) {
      return new NeverAsyncLogger<T>(core, newBuilder);
    }

    @Override
    public @NotNull AsyncLogger<FB> withCondition(@NotNull Condition condition) {
      return this;
    }

    public void trace(@NotNull Consumer<LoggerHandle<FB>> consumer) {}

    /**
     * Logs using a condition and a logger handle at TRACE level.
     *
     * @param c the condition
     * @param consumer the consumer of the logger handle.
     */
    public void trace(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {}

    @Override
    public void trace(@Nullable String message) {}

    @Override
    public void trace(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void trace(@Nullable String message, @NotNull Throwable e) {}

    @Override
    public void trace(@NotNull Condition condition, @Nullable String message) {}

    @Override
    public void trace(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void trace(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

    /**
     * Logs using a logger handle at DEBUG level.
     *
     * @param consumer the consumer of the logger handle.
     */
    public void debug(@NotNull Consumer<LoggerHandle<FB>> consumer) {}

    /**
     * Logs using a condition and a logger handle at DEBUG level.
     *
     * @param c the condition
     * @param consumer the consumer of the logger handle.
     */
    public void debug(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {}

    @Override
    public void debug(@Nullable String message) {}

    @Override
    public void debug(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void debug(@Nullable String message, @NotNull Throwable e) {}

    @Override
    public void debug(@NotNull Condition condition, @Nullable String message) {}

    @Override
    public void debug(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void debug(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

    /**
     * Logs using a logger handle at INFO level.
     *
     * @param consumer the consumer of the logger handle.
     */
    public void info(@NotNull Consumer<LoggerHandle<FB>> consumer) {}

    /**
     * Logs using a condition and a logger handle at INFO level.
     *
     * @param c the condition
     * @param consumer the consumer of the logger handle.
     */
    public void info(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {}

    @Override
    public void info(@Nullable String message) {}

    @Override
    public void info(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void info(@Nullable String message, @NotNull Throwable e) {}

    @Override
    public void info(@NotNull Condition condition, @Nullable String message) {}

    @Override
    public void info(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void info(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

    /**
     * Logs using a logger handle at WARN level.
     *
     * @param consumer the consumer of the logger handle.
     */
    public void warn(@NotNull Consumer<LoggerHandle<FB>> consumer) {}

    /**
     * Logs using a condition and a logger handle at WARN level.
     *
     * @param c the condition
     * @param consumer the consumer of the logger handle.
     */
    public void warn(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {}

    @Override
    public void warn(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void warn(@Nullable String message, @NotNull Throwable e) {}

    @Override
    public void warn(@NotNull Condition condition, @Nullable String message) {}

    @Override
    public void warn(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void warn(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

    /**
     * Logs using a logger handle at ERROR level.
     *
     * @param consumer the consumer of the logger handle.
     */
    @Override
    public void error(@NotNull Consumer<LoggerHandle<FB>> consumer) {}

    /**
     * Logs using a condition and a logger handle at ERROR level.
     *
     * @param c the condition
     * @param consumer the consumer of the logger handle.
     */
    @Override
    public void error(@NotNull Condition c, @NotNull Consumer<LoggerHandle<FB>> consumer) {}

    @Override
    public void error(@Nullable String message) {}

    @Override
    public void error(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void error(@Nullable String message, @NotNull Throwable e) {}

    @Override
    public void error(@NotNull Condition condition, @Nullable String message) {}

    @Override
    public void error(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {}

    @Override
    public void error(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}
  }
}
