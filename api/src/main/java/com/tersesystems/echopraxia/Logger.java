package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.support.Utilities.getNewInstance;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.support.AbstractLoggerSupport;
import com.tersesystems.echopraxia.support.DefaultLoggerMethods;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An echopraxia logger built around a field builder.
 *
 * @param <FB> the field builder type.
 */
public class Logger<FB extends Field.Builder> extends AbstractLoggerSupport<Logger<FB>, FB>
    implements DefaultLoggerMethods<FB> {

  // This is where the logging methods are called, so the stacktrace element shows
  // DefaultLoggerMethods as the caller.
  public static final String FQCN = DefaultLoggerMethods.class.getName();

  protected Logger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder, Logger.class);
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
  public <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull Class<T> newBuilderClass) {
    return newLogger(getNewInstance(newBuilderClass));
  }

  /**
   * Creates a new async logger using this field builder.
   *
   * @deprecated since 1.3, use {@code AsyncLoggerFactory.getLogger(logger.core(), logger.fieldBuilder())}
   * @param executor the executor
   * @return an async logger.
   */
  @Deprecated()
  public AsyncLogger<FB> withExecutor(Executor executor) {
    return new AsyncLogger<>(core().withExecutor(executor), fieldBuilder);
  }

  // This is not part of the AbstractLoggerSupport
  protected <T extends Field.Builder> Logger<T> newLogger(T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (Logger<T>) this;
    }
    return new Logger<>(core(), newBuilder);
  }

  @Override
  protected @NotNull Logger<FB> newLogger(CoreLogger core) {
    return new Logger<>(core, fieldBuilder);
  }

  @Override
  protected @NotNull Logger<FB> neverLogger() {
    return new NeverLogger<>(core.withCondition(Condition.never()), fieldBuilder);
  }

  /**
   * An optimized logger for use with {@code Condition.never()}.
   *
   * @param <FB>
   */
  public static class NeverLogger<FB extends Field.Builder> extends Logger<FB> {

    protected NeverLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
      super(core, fieldBuilder);
    }

    @Override
    public @NotNull Logger<FB> withThreadContext() {
      return this;
    }

    protected @NotNull Logger<FB> newLogger(CoreLogger core) {
      return this;
    }

    @Override
    public @NotNull <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull T newBuilder) {
      return new NeverLogger<T>(core, newBuilder);
    }

    @Override
    public @NotNull Logger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
      return this;
    }

    @Override
    public @NotNull Logger<FB> withCondition(@NotNull Condition condition) {
      return this;
    }

    /** @return true if the logger level is TRACE or higher. */
    public boolean isTraceEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is TRACE or higher and the condition is met.
     */
    public boolean isTraceEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    public void trace(@Nullable String message) {
      // do nothing
    }

    @Override
    public void trace(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void trace(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    public void trace(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    public void trace(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void trace(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is DEBUG or higher. */
    public boolean isDebugEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is DEBUG or higher and the condition is met.
     */
    public boolean isDebugEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    public void debug(@Nullable String message) {
      // do nothing
    }

    @Override
    public void debug(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void debug(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    public void debug(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    public void debug(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void debug(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is INFO or higher. */
    public boolean isInfoEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is INFO or higher and the condition is met.
     */
    public boolean isInfoEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    public void info(@Nullable String message) {
      // do nothing
    }

    @Override
    public void info(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void info(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    public void info(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    public void info(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is WARN or higher. */
    public boolean isWarnEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is WARN or higher and the condition is met.
     */
    public boolean isWarnEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    public void warn(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void warn(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    public void warn(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    public void warn(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is ERROR or higher. */
    public boolean isErrorEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is ERROR or higher and the condition is met.
     */
    public boolean isErrorEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    public void error(@Nullable String message) {
      // do nothing
    }

    @Override
    public void error(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void error(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    public void error(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    public void error(
        @NotNull Condition condition,
        @Nullable String message,
        Field.@NotNull BuilderFunction<FB> f) {
      // do nothing
    }

    @Override
    public void error(
        @NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }
  }
}
