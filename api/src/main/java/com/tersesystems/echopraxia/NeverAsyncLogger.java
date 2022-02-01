package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeverAsyncLogger<FB extends Field.Builder> extends AsyncLogger<FB> {

  protected NeverAsyncLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder);
  }

  @Override
  public @NotNull AsyncLogger<FB> withThreadContext() {
    return this;
  }

  @Override
  public @NotNull <T extends Field.Builder> AsyncLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    return new NeverAsyncLogger<T>(core, newBuilder);
  }

  @Override
  public @NotNull AsyncLogger<FB> withFields(Field.@NotNull BuilderFunction<FB> f) {
    return this;
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
  public void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

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
  public void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

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
  public void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

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
  public void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}

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
  public void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {}
}
