package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggerHandle;
import com.tersesystems.echopraxia.core.CoreLogger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeCoreLogger implements CoreLogger {

  private final FakeLoggingContext context;
  private final Condition condition;
  private final Executor executor;

  protected FakeCoreLogger() {
    this.context = FakeLoggingContext.empty();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
  }

  public FakeCoreLogger(FakeLoggingContext context, Condition condition, Executor executor) {
    this.context = context;
    this.condition = condition;
    this.executor = executor;
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    return false;
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    return false;
  }

  @Override
  public @NotNull Condition condition() {
    return condition;
  }

  @Override
  public @NotNull <B extends Field.Builder> CoreLogger withFields(
      Field.@NotNull BuilderFunction<B> f, @NotNull B builder) {
    FakeLoggingContext newContext = new FakeLoggingContext(() -> f.apply(builder));
    return new FakeCoreLogger(context.and(newContext), this.condition.and(condition), executor);
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    return this;
  }

  @Override
  public @NotNull CoreLogger withCondition(@NotNull Condition condition) {
    return new FakeCoreLogger(context, this.condition.and(condition), executor);
  }

  @Override
  public @NotNull CoreLogger withExecutor(@NotNull Executor executor) {
    return new FakeCoreLogger(context, condition, executor);
  }

  // -----------------------------------------------------------------------

  @Override
  public void log(@NotNull Level level, @Nullable String message) {
    if (isEnabledFor(level) && this.condition.test(level, context)) {
      List<Field> fields = context.getFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public <B extends Field.Builder> void log(
      @NotNull Level level,
      @Nullable String message,
      Field.@NotNull BuilderFunction<B> f,
      @NotNull B builder) {
    if (isEnabledFor(level) && this.condition.test(level, context)) {
      List<Field> fields = context.getFields();
      List<Field> args = f.apply(builder);
      System.out.printf("" + message + " level %s fields %s args %s\n", level, fields, args);
    }
  }

  @Override
  public void log(@NotNull Level level, @Nullable String message, @NotNull Throwable e) {
    if (isEnabledFor(level) && this.condition.test(level, context)) {
      List<Field> fields = context.getFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {
    if (isEnabledFor(level) && this.condition.and(condition).test(level, context)) {
      List<Field> fields = context.getFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Throwable e) {
    if (isEnabledFor(level) && this.condition.and(condition).test(level, context)) {
      List<Field> fields = context.getFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public <B extends Field.Builder> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      Field.@NotNull BuilderFunction<B> f,
      @NotNull B builder) {
    if (isEnabledFor(level) && this.condition.and(condition).test(level, context)) {
      List<Field> fields = context.getFields();
      List<Field> args = f.apply(builder);
      System.out.printf("" + message + " level %s fields %s args %s\n", level, fields, args);
    }
  }

  private boolean isEnabledFor(Level level) {
    return true;
  }

  // -----------------------------------------------------------------------

  @Override
  public <FB extends Field.Builder> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {}

  @Override
  public <FB extends Field.Builder> void asyncLog(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {}
}
