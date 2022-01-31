package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An optimized logger for use with {@code Condition.never()}.
 *
 * @param <FB>
 */
public class NeverLogger<FB extends Field.Builder> extends Logger<FB> {

  protected NeverLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder);
  }

  @Override
  public @NotNull Logger<FB> withThreadContext() {
    return this;
  }

  @Override
  public @NotNull <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull T newBuilder) {
    return new NeverLogger<T>(core, newBuilder);
  }

  @Override
  public @NotNull Logger<FB> withFields(Field.@NotNull BuilderFunction<FB> f) {
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
  public void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
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
  public void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
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
  public void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
    // do nothing
  }
}
