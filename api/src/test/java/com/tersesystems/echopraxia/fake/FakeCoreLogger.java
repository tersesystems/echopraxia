package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FakeCoreLogger implements CoreLogger {

  private final FakeLoggingContext context;
  private final Condition condition;
  private final Executor executor;
  private final String fqcn;

  private final Supplier<Runnable> tlsSupplier;

  public FakeCoreLogger(String fqcn) {
    this.fqcn = fqcn;
    this.context = FakeLoggingContext.empty(this);
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
  public  String getName() {
    return "wheee";
  }

  @Override
  public boolean isEnabled( Level level) {
    return false;
  }

  @Override
  public boolean isEnabled( Level level,  Condition condition) {
    return false;
  }

  @Override
  public boolean isEnabled( Level level,  Supplier<List<Field>> extraFields) {
    return false;
  }

  @Override
  public boolean isEnabled(
       Level level,
       Condition condition,
       Supplier<List<Field>> extraFields) {
    return false;
  }

  @Override
  public  Condition condition() {
    return condition;
  }

  @Override
  public  String fqcn() {
    return fqcn;
  }

  @Override
  public  <FB> CoreLogger withFields(
       Function<FB, FieldBuilderResult> f,  FB builder) {
    FakeLoggingContext ctx =
        new FakeLoggingContext(this, context::getLoggerFields, () -> convert(f.apply(builder)));
    return new FakeCoreLogger(fqcn, ctx, this.condition.and(condition), executor, tlsSupplier);
  }

  private List<Field> convert(FieldBuilderResult input) {
    return input.fields();
  }

  @Override
  public  CoreLogger withThreadContext(
       Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    return this;
  }

  @Override
  public  CoreLogger withThreadLocal(Supplier<Runnable> newSupplier) {
    return new FakeCoreLogger(fqcn, context, this.condition.and(condition), executor, newSupplier);
  }

  @Override
  public  CoreLogger withCondition( Condition condition) {
    return new FakeCoreLogger(fqcn, context, this.condition.and(condition), executor, tlsSupplier);
  }

  @Override
  public  CoreLogger withExecutor( Executor executor) {
    return new FakeCoreLogger(fqcn, context, condition, executor, tlsSupplier);
  }

  @Override
  public  CoreLogger withFQCN( String fqcn) {
    return new FakeCoreLogger(fqcn, context, condition, executor, tlsSupplier);
  }

  // -----------------------------------------------------------------------

  @Override
  public void log( Level level,  String message) {
    if (isEnabledFor(level) && this.condition.test(level, context)) {
      List<Field> fields = context.getFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public void log(
       Level level,  Supplier<List<Field>> extraFields,  String message) {
    if (isEnabledFor(level) && this.condition.test(level, context)) {
      List<Field> fields = context.withFields(extraFields).getFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public <FB> void log(
       Level level,
       String message,
       Function<FB, FieldBuilderResult> f,
       FB builder) {
    List<Field> args = convert(f.apply(builder));
    if (isEnabledFor(level)) {
      FakeLoggingContext memo =
          new FakeLoggingContext(this, context::getLoggerFields, context::getArgumentFields);
      if (this.condition.test(level, memo)) {
        List<Field> fields = context.getFields();
        System.out.printf("" + message + " level %s fields %s args %s\n", level, fields, args);
      }
    }
  }

  @Override
  public <FB> void log(
       Level level,
       Supplier<List<Field>> extraFields,
       String message,
       Function<FB, FieldBuilderResult> f,
       FB builder) {
    List<Field> args = convert(f.apply(builder));
    if (isEnabledFor(level)) {
      FakeLoggingContext memo =
          new FakeLoggingContext(
              FakeCoreLogger.this, context::getLoggerFields, context::getArgumentFields);
      if (this.condition.test(level, memo)) {
        List<Field> fields = context.getFields();
        System.out.printf("" + message + " level %s fields %s args %s\n", level, fields, args);
      }
    }
  }

  @Override
  public void log( Level level,  Condition condition,  String message) {
    if (isEnabledFor(level) && this.condition.and(condition).test(level, context)) {
      List<Field> fields = context.getLoggerFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public void log(
       Level level,
       Supplier<List<Field>> extraFields,
       Condition condition,
       String message) {
    if (isEnabledFor(level) && this.condition.and(condition).test(level, context)) {
      List<Field> fields = context.withFields(extraFields).getLoggerFields();
      System.out.printf("" + message + " level %s fields %s\n", level, fields);
    }
  }

  @Override
  public <FB> void log(
       Level level,
       Condition condition,
       String message,
       Function<FB, FieldBuilderResult> f,
       FB builder) {
    // When passing a condition through with explicit arguments, we pull the args and make
    // them available through context.
    FakeLoggingContext argContext =
        new FakeLoggingContext(
            FakeCoreLogger.this, context::getLoggerFields, () -> convert(f.apply(builder)));
    if (isEnabledFor(level) && this.condition.and(condition).test(level, argContext)) {
      System.out.printf(
          "" + message + " level %s fields %s args %s\n",
          level,
          argContext.getLoggerFields(),
          argContext.getArgumentFields());
    }
  }

  @Override
  public <FB> void log(
       Level level,
       Supplier<List<Field>> extraFields,
       Condition condition,
       String message,
       Function<FB, FieldBuilderResult> f,
       FB builder) {
    if (isEnabledFor(level)) {
      FakeLoggingContext argContext =
          new FakeLoggingContext(
              FakeCoreLogger.this,
              () -> context.withFields(extraFields).getLoggerFields(),
              () -> convert(f.apply(builder)));
      if (this.condition.and(condition).test(level, argContext)) {
        System.out.printf(
            "" + message + " level %s fields %s args %s\n",
            level,
            argContext.getLoggerFields(),
            argContext.getArgumentFields());
      }
    }
  }

  @Override
  public  <FB> LoggerHandle<FB> logHandle( Level level,  FB builder) {
    return new LoggerHandle<FB>() {
      @Override
      public void log( String message) {
        System.out.printf("" + message + " level %s fields %s\n", level, context.getLoggerFields());
      }

      @Override
      public void log( String message,  Function<FB, FieldBuilderResult> f) {
        FakeLoggingContext ctx =
            new FakeLoggingContext(
                FakeCoreLogger.this, context::getLoggerFields, () -> convert(f.apply(builder)));
        System.out.printf(
            "" + message + " level %s fields %s args %s\n",
            level,
            ctx.getLoggerFields(),
            ctx.getArgumentFields());
      }
    };
  }

  private boolean isEnabledFor(Level level) {
    return true;
  }

  // -----------------------------------------------------------------------

  @Override
  public <FB> void asyncLog(
       Level level,  Consumer<LoggerHandle<FB>> consumer,  FB builder) {}

  @Override
  public <FB> void asyncLog(
       Level level,
       Condition condition,
       Consumer<LoggerHandle<FB>> consumer,
       FB builder) {}

  @Override
  public <FB> void asyncLog(
       Level level,
       Supplier<List<Field>> extraFields,
       Consumer<LoggerHandle<FB>> consumer,
       FB builder) {}

  @Override
  public <FB> void asyncLog(
       Level level,
       Supplier<List<Field>> extraFields,
       Condition condition,
       Consumer<LoggerHandle<FB>> consumer,
       FB builder) {}
}
