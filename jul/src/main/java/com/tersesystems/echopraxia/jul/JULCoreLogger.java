package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JULCoreLogger implements CoreLogger {

  private final Logger logger;
  private final JULLoggerContext context;
  private final Condition condition;
  private final String fqcn;

  public JULCoreLogger(@NotNull String fqcn, @NotNull Logger logger) {
    this.fqcn = fqcn;
    this.logger = logger;
    this.context = JULLoggerContext.empty();
    this.condition = Condition.always();
  }

  protected JULCoreLogger(
      @NotNull String fqcn,
      @NotNull Logger log4jLogger,
      @NotNull JULLoggerContext context,
      @NotNull Condition condition) {
    this.fqcn = fqcn;
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
  }

  @NotNull
  public Logger logger() {
    return this.logger;
  }

  @Override
  @NotNull
  public String getName() {
    return logger.getName();
  }

  @Override
  public @NotNull Condition condition() {
    return this.condition;
  }

  @Override
  public @NotNull String fqcn() {
    return fqcn;
  }

  // attempt to cover all permutations of output.
  @Override
  public <FB> @NotNull JULCoreLogger withFields(
      @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder) {
    JULLoggerContext newContext = context.withFields(() -> convertToFields(f.apply(builder)));
    return newLogger(newContext);
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    return this;
  }

  @Override
  public @NotNull CoreLogger withThreadLocal(Supplier<Runnable> newSupplier) {
    return this;
  }

  @Override
  public @NotNull JULCoreLogger withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    if (condition == Condition.never()) {
      if (this.condition == Condition.never()) {
        return this;
      }
      return newLogger(condition);
    }
    return newLogger(this.condition.and(condition));
  }

  @Override
  public @NotNull CoreLogger withExecutor(@NotNull Executor executor) {
    return null;
  }

  @Override
  public @NotNull JULCoreLogger withFQCN(@NotNull String fqcn) {
    return new JULCoreLogger(fqcn, logger, context, condition);
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    if (condition == Condition.always()) {
      return logger.isLoggable(convertLevel(level));
    }
    if (condition == Condition.never()) {
      return false;
    }
    if (logger.isLoggable(convertLevel(level))) {
      JULLoggingContext snapshotContext = new JULLoggingContext(this, context);
      return condition.test(level, snapshotContext);
    }
    return false;
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    final Condition bothConditions = this.condition.and(condition);
    if (bothConditions == Condition.always()) {
      return logger.isLoggable(convertLevel(level));
    }
    if (bothConditions == Condition.never()) {
      return false;
    }
    if (logger.isLoggable(convertLevel(level))) {
      JULLoggingContext snapshotContext = new JULLoggingContext(this, context);
      return bothConditions.test(level, snapshotContext);
    }
    return false;
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Supplier<List<Field>> extraFields) {
    java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
      return condition.test(level, ctx);
    } else {
      return false;
    }
  }

  @Override
  public boolean isEnabled(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull Supplier<List<Field>> extraFields) {

    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
      return this.condition.and(condition).test(level, ctx);
    } else {
      return false;
    }
  }

  @Override
  public void log(@NotNull Level level, String message) {
    final java.util.logging.Level julLevel = convertLevel(level);
    // the isLoggable check always goes before the condition check, as conditions can be expensive
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx = new JULLoggingContext(this, context);
      if (condition.test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public void log(
      @NotNull Level level, @NotNull Supplier<List<Field>> extraFields, @Nullable String message) {

    final java.util.logging.Level julLevel = convertLevel(level);
    // the isLoggable check always goes before the condition check, as conditions can be expensive
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
      if (condition.test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx =
          new JULLoggingContext(this, context, () -> convertToFields(f.apply(builder)));
      if (condition.test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {

    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx =
          new JULLoggingContext(
              this, context.withFields(extraFields), () -> convertToFields(f.apply(builder)));
      if (condition.test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {

    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      // We want to memoize context fields even if no argument...
      JULLoggingContext ctx = new JULLoggingContext(this, context);
      if (this.condition.and(condition).test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition condition,
      @Nullable String message) {

    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      // We want to memoize context fields even if no argument...
      JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
      if (this.condition.and(condition).test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx =
          new JULLoggingContext(this, context, () -> convertToFields(f.apply(builder)));
      if (this.condition.and(condition).test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {

    final java.util.logging.Level julLevel = convertLevel(level);
    if (logger.isLoggable(julLevel)) {
      JULLoggingContext ctx =
          new JULLoggingContext(
              this, context.withFields(extraFields), () -> convertToFields(f.apply(builder)));
      if (this.condition.and(condition).test(level, ctx)) {
        LogRecord logRecord = createLogRecord(julLevel, message, ctx);
        logger.log(logRecord);
      }
    }
  }

  @Override
  public @NotNull <FB> LoggerHandle<FB> logHandle(@NotNull Level level, @NotNull FB builder) {
    return new LoggerHandle<FB>() {
      final java.util.logging.Level log4jLevel = convertLevel(level);

      @Override
      public void log(@Nullable String message) {
        JULLoggingContext ctx = new JULLoggingContext(JULCoreLogger.this, context);
        LogRecord logRecord = createLogRecord(log4jLevel, message, ctx);
        logger.log(logRecord);
      }

      @Override
      public void log(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
        JULLoggingContext ctx =
            new JULLoggingContext(
                JULCoreLogger.this, context, () -> convertToFields(f.apply(builder)));
        LogRecord logRecord = createLogRecord(log4jLevel, message, ctx);
        logger.log(logRecord);
      }
    };
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    throw new UnsupportedOperationException();
  }

  private List<Field> convertToFields(FieldBuilderResult result) {
    if (result == null) {
      // XXX log an error
      return Collections.emptyList();
    }
    return result.fields();
  }

  private java.util.logging.Level convertLevel(Level level) {
    switch (level) {
      case ERROR:
        return java.util.logging.Level.SEVERE;
      case WARN:
        return java.util.logging.Level.WARNING;
      case INFO:
        return java.util.logging.Level.INFO;
      case DEBUG:
        return java.util.logging.Level.FINE;
      case TRACE:
        return java.util.logging.Level.FINEST;
    }
    throw new IllegalStateException("Unknown level " + level);
  }

  private LogRecord createLogRecord(
      java.util.logging.Level julLevel, String message, JULLoggingContext ctx) {
    LogRecord record = new LogRecord(julLevel, message);
    record.setLoggerName(getName());
    List<Field> fields = ctx.getFields();

    // JUL is really slow and calls sourceClassName lots when serializing.
    // record.setSourceClassName(null);
    // record.setSourceMethodName(null);

    if (fields.size() == 0) {
      return record;
    } else if (fields.size() == 1) {
      Field field = fields.get(0);
      Object obj = field.value().raw();
      if (obj instanceof Throwable) {
        record.setThrown((Throwable) obj);
      } else {
        record.setParameters(new Object[] {field});
      }
    } else {
      Object[] parameters = fields.toArray();
      record.setParameters(parameters);
    }
    return record;
  }

  @NotNull
  private JULCoreLogger newLogger(JULLoggerContext newContext) {
    return new JULCoreLogger(fqcn, logger, newContext, condition);
  }

  @NotNull
  private JULCoreLogger newLogger(@NotNull Condition condition) {
    return new JULCoreLogger(fqcn, logger, context, condition);
  }

  public String toString() {
    return "JULCoreLogger[" + logger.getName() + "]";
  }
}
