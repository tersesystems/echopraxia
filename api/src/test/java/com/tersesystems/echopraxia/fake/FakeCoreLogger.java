package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.*;
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
  private final String fqcn;

  private final Supplier<Runnable> tlsSupplier;

  protected FakeCoreLogger(String fqcn) {
    this.fqcn = fqcn;
    this.context = FakeLoggingContext.empty();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
    this.tlsSupplier = () -> (Runnable) () -> {};
  }

  public FakeCoreLogger(
      String fqcn,
      FakeLoggingContext context,
      Condition condition,
      Executor executor,
      Supplier<Runnable> tlsSupplier) {
    this.fqcn = fqcn;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
    this.tlsSupplier = tlsSupplier;
  }

  @Override
  public @NotNull String getName() {
    return "wheee";
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
  public @NotNull String fqcn() {
    return fqcn;
  }

  @Override
  public @NotNull <FB> CoreLogger withFields(
      @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder) {
    FakeLoggingContext newContext = new FakeLoggingContext(() -> convert(f.apply(builder)));
    return new FakeCoreLogger(
        fqcn, context.and(newContext), this.condition.and(condition), executor, tlsSupplier);
  }

  private List<Field> convert(FieldBuilderResult input) {
    return input.fields();
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    return this;
  }

  @Override
  public @NotNull CoreLogger withThreadLocal(Supplier<Runnable> newSupplier) {
    return new FakeCoreLogger(fqcn, context, this.condition.and(condition), executor, newSupplier);
  }

  @Override
  public @NotNull CoreLogger withCondition(@NotNull Condition condition) {
    return new FakeCoreLogger(fqcn, context, this.condition.and(condition), executor, tlsSupplier);
  }

  @Override
  public @NotNull CoreLogger withExecutor(@NotNull Executor executor) {
    return new FakeCoreLogger(fqcn, context, condition, executor, tlsSupplier);
  }

  @Override
  public @NotNull CoreLogger withFQCN(@NotNull String fqcn) {
    return new FakeCoreLogger(fqcn, context, condition, executor, tlsSupplier);
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
  public <FB> void log(
      @NotNull Level level,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    List<Field> args = convert(f.apply(builder));
    FakeLoggingContext argContext = new FakeLoggingContext(() -> args);
    if (isEnabledFor(level) && this.condition.test(level, context.and(argContext))) {
      List<Field> fields = context.getFields();
      System.out.printf("" + message + " level %s fields %s args %s\n", level, fields, args);
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
  public <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    // When passing a condition through with explicit arguments, we pull the args and make
    // them available through context.
    List<Field> fields = context.getFields();
    List<Field> args = convert(f.apply(builder));
    FakeLoggingContext argContext = new FakeLoggingContext(() -> args);
    if (isEnabledFor(level) && this.condition.and(condition).test(level, context.and(argContext))) {
      System.out.printf("" + message + " level %s fields %s args %s\n", level, fields, args);
    }
  }

  private boolean isEnabledFor(Level level) {
    return true;
  }

  // -----------------------------------------------------------------------

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {}

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {}

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {}

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition condition,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {}
}
