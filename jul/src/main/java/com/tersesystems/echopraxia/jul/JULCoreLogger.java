package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;

public class JULCoreLogger implements CoreLogger {

  private final Logger logger;
  private final JULLoggerContext context;
  private final Condition condition;
  private final String fqcn;
  private final Supplier<Runnable> threadContextFunction;

  private final Executor executor;
  private final FieldConverter fieldConverter;

  public JULCoreLogger(@NotNull String fqcn, @NotNull Logger logger) {
    this.fqcn = fqcn;
    this.logger = logger;
    this.context = JULLoggerContext.empty();
    this.condition = Condition.always();
    this.threadContextFunction = mdcContext();
    this.executor = ForkJoinPool.commonPool();
    this.fieldConverter = FieldConverter.identity();
  }

  protected JULCoreLogger(
      @NotNull String fqcn,
      @NotNull Logger log4jLogger,
      @NotNull JULLoggerContext context,
      @NotNull Condition condition,
      @NotNull Supplier<Runnable> threadContextFunction,
      @NotNull Executor executor,
      @NotNull FieldConverter fieldConverter) {
    this.fqcn = fqcn;
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
    this.threadContextFunction = threadContextFunction;
    this.executor = executor;
    this.fieldConverter = fieldConverter;
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
  public @NotNull JULCoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    JULLoggerContext newContext = context.withFields(mapTransform.apply(MDC::getCopyOfContextMap));
    return newLogger(newContext);
  }

  @Override
  public @NotNull CoreLogger withThreadLocal(Supplier<Runnable> newThreadContextFunction) {
    Supplier<Runnable> joinedThreadContextFunction =
        () -> {
          final Runnable r1 = newThreadContextFunction.get();
          final Runnable r2 = threadContextFunction.get();
          return () -> {
            r1.run();
            r2.run();
          };
        };
    return newLogger(joinedThreadContextFunction);
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
  public @NotNull JULCoreLogger withExecutor(@NotNull Executor executor) {
    return newLogger(executor);
  }

  @Override
  public @NotNull JULCoreLogger withFieldConverter(FieldConverter fieldConverter) {
    return newLogger(fieldConverter);
  }

  @Override
  public @NotNull JULCoreLogger withFQCN(@NotNull String fqcn) {
    return newLogger(fqcn);
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    try {
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
    } catch (Exception e) {
      handleException(e);
      return false;
    }
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    try {
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
    } catch (Exception e) {
      handleException(e);
      return false;
    }
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Supplier<List<Field>> extraFields) {
    try {
      java.util.logging.Level julLevel = convertLevel(level);
      if (logger.isLoggable(julLevel)) {
        JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
        return condition.test(level, ctx);
      } else {
        return false;
      }
    } catch (Exception e) {
      handleException(e);
      return false;
    }
  }

  @Override
  public boolean isEnabled(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull Supplier<List<Field>> extraFields) {
    try {
      final java.util.logging.Level julLevel = convertLevel(level);
      if (logger.isLoggable(julLevel)) {
        JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
        return this.condition.and(condition).test(level, ctx);
      } else {
        return false;
      }
    } catch (Exception e) {
      handleException(e);
      return false;
    }
  }

  @Override
  public void log(@NotNull Level level, String message) {
    final java.util.logging.Level julLevel = convertLevel(level);
    // the isLoggable check always goes before the condition check, as conditions can be expensive
    try {
      if (logger.isLoggable(julLevel)) {
        JULLoggingContext ctx = new JULLoggingContext(this, context);
        if (condition.test(level, ctx)) {
          LogRecord logRecord = createLogRecord(julLevel, message, ctx);
          logger.log(logRecord);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public void log(
      @NotNull Level level, @NotNull Supplier<List<Field>> extraFields, @Nullable String message) {
    try {
      final java.util.logging.Level julLevel = convertLevel(level);
      // the isLoggable check always goes before the condition check, as conditions can be expensive
      if (logger.isLoggable(julLevel)) {
        JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
        if (condition.test(level, ctx)) {
          LogRecord logRecord = createLogRecord(julLevel, message, ctx);
          logger.log(logRecord);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    try {
      final java.util.logging.Level julLevel = convertLevel(level);
      if (logger.isLoggable(julLevel)) {
        JULLoggingContext ctx =
            new JULLoggingContext(this, context, () -> convertToFields(f.apply(builder)));
        if (condition.test(level, ctx)) {
          LogRecord logRecord = createLogRecord(julLevel, message, ctx);
          logger.log(logRecord);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    try {
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
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {
    try {
      final java.util.logging.Level julLevel = convertLevel(level);
      if (logger.isLoggable(julLevel)) {
        // We want to memoize context fields even if no argument...
        JULLoggingContext ctx = new JULLoggingContext(this, context);
        if (this.condition.and(condition).test(level, ctx)) {
          LogRecord logRecord = createLogRecord(julLevel, message, ctx);
          logger.log(logRecord);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition condition,
      @Nullable String message) {
    try {
      final java.util.logging.Level julLevel = convertLevel(level);
      if (logger.isLoggable(julLevel)) {
        // We want to memoize context fields even if no argument...
        JULLoggingContext ctx = new JULLoggingContext(this, context.withFields(extraFields));
        if (this.condition.and(condition).test(level, ctx)) {
          LogRecord logRecord = createLogRecord(julLevel, message, ctx);
          logger.log(logRecord);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    try {
      final java.util.logging.Level julLevel = convertLevel(level);
      if (logger.isLoggable(julLevel)) {
        JULLoggingContext ctx =
            new JULLoggingContext(this, context, () -> convertToFields(f.apply(builder)));
        if (this.condition.and(condition).test(level, ctx)) {
          LogRecord logRecord = createLogRecord(julLevel, message, ctx);
          logger.log(logRecord);
        }
      }
    } catch (Exception e) {
      handleException(e);
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
    try {
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
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public @NotNull <FB> LoggerHandle<FB> logHandle(@NotNull Level level, @NotNull FB builder) {
    return new LoggerHandle<FB>() {
      final java.util.logging.Level log4jLevel = convertLevel(level);

      @Override
      public void log(@Nullable String message) {
        try {
          JULLoggingContext ctx = new JULLoggingContext(JULCoreLogger.this, context);
          LogRecord logRecord = createLogRecord(log4jLevel, message, ctx);
          logger.log(logRecord);
        } catch (Exception e) {
          handleException(e);
        }
      }

      @Override
      public void log(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
        try {
          JULLoggingContext ctx =
              new JULLoggingContext(
                  JULCoreLogger.this, context, () -> convertToFields(f.apply(builder)));
          LogRecord logRecord = createLogRecord(log4jLevel, message, ctx);
          logger.log(logRecord);
        } catch (Exception e) {
          handleException(e);
        }
      }
    };
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    if (logger.isLoggable(convertLevel(level))) {
      Runnable threadLocalRunnable = threadContextFunction.get();
      runAsyncLog(
          () -> {
            threadLocalRunnable.run();
            consumer.accept(newHandle(level, builder, this));
          });
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    if (logger.isLoggable(convertLevel(level))) {
      Runnable threadLocalRunnable = threadContextFunction.get();
      runAsyncLog(
          () -> {
            threadLocalRunnable.run();
            final LoggerHandle<FB> loggerHandle = newHandle(level, c, builder, this);
            consumer.accept(loggerHandle);
          });
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    if (logger.isLoggable(convertLevel(level))) {
      Runnable threadLocalRunnable = threadContextFunction.get();
      runAsyncLog(
          () -> {
            threadLocalRunnable.run();
            final LoggerHandle<FB> loggerHandle = newHandle(level, builder, this);
            consumer.accept(loggerHandle);
          });
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    if (logger.isLoggable(convertLevel(level))) {
      Runnable threadLocalRunnable = threadContextFunction.get();
      runAsyncLog(
          () -> {
            threadLocalRunnable.run();
            final LoggerHandle<FB> loggerHandle = newHandle(level, c, builder, this);
            consumer.accept(loggerHandle);
          });
    }
  }

  protected void runAsyncLog(Runnable runnable) {
    // exceptionally is available in JDK 1.8, we can't use exceptionallyAsync as it's 12 only
    CompletableFuture.runAsync(runnable, executor)
        .exceptionally(
            e -> {
              // Usually we get to this point when you have thread local dependent code in your
              // logger.withContext() block, and your executor doesn't have those thread locals
              // so you NPE.
              //
              // We need to log this error, but since it could be part of the logger context
              // that is causing this error, we can't log the error with the same logger.
              //
              // Fallback to the underlying SLF4J logger to render it.
              final Throwable cause = e.getCause(); // strip the CompletionException
              logger.log(
                  java.util.logging.Level.SEVERE,
                  "Uncaught exception when running asyncLog",
                  cause);
              return null;
            });
  }

  @NotNull
  private <FB> LoggerHandle<FB> newHandle(
      @NotNull Level level, @NotNull FB builder, JULCoreLogger callerLogger) {
    return new LoggerHandle<FB>() {
      @Override
      public void log(@Nullable String message) {
        callerLogger.log(level, message);
      }

      @Override
      public void log(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
        callerLogger.log(level, message, f, builder);
      }
    };
  }

  protected <FB> LoggerHandle<FB> newHandle(
      @NotNull Level level, @NotNull Condition c, @NotNull FB builder, JULCoreLogger callerLogger) {
    return new LoggerHandle<FB>() {
      @Override
      public void log(@Nullable String message) {
        callerLogger.log(level, c, message);
      }

      @Override
      public void log(@Nullable String message, @NotNull Function<FB, FieldBuilderResult> f) {
        // conditions involve argument fields, so we can't short circuit allocation in this chain...
        callerLogger.log(level, c, message, f, builder);
      }
    };
  }

  private List<Field> convertToFields(FieldBuilderResult result) {
    if (result == null) {
      handleException(new NullPointerException("Null result!"));
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

  private EchopraxiaLogRecord createLogRecord(
      java.util.logging.Level julLevel, String messageTemplate, JULLoggingContext ctx) {
    final List<Field> argumentFields = ctx.getArgumentFields();
    final Field[] arguments = new Field[argumentFields.size()];
    int i = 0;
    Throwable thrown = null;
    for (Field f : argumentFields) {
      arguments[i++] = (Field) this.fieldConverter.convertArgumentField(f);
      if (f.value() instanceof Value.ExceptionValue) {
        thrown = (Throwable) f.value().raw();
        break;
      }
    }

    i = 0;
    List<Field> loggerFields = ctx.getLoggerFields();
    final Field[] fields = new Field[loggerFields.size()];
    for (Field f : loggerFields) {
      fields[i++] = (Field) this.fieldConverter.convertLoggerField(f);
    }
    return new EchopraxiaLogRecord(getName(), julLevel, messageTemplate, arguments, fields, thrown);
  }

  @NotNull
  private JULCoreLogger newLogger(JULLoggerContext newContext) {
    return new JULCoreLogger(
        fqcn, logger, newContext, condition, threadContextFunction, executor, fieldConverter);
  }

  private JULCoreLogger newLogger(Supplier<Runnable> newThreadContextFunction) {
    return new JULCoreLogger(
        fqcn, logger, context, condition, newThreadContextFunction, executor, fieldConverter);
  }

  @NotNull
  private JULCoreLogger newLogger(@NotNull Condition condition) {
    return new JULCoreLogger(
        fqcn, logger, context, condition, threadContextFunction, executor, fieldConverter);
  }

  private JULCoreLogger newLogger(Executor executor) {
    return new JULCoreLogger(
        fqcn, logger, context, condition, threadContextFunction, executor, fieldConverter);
  }

  private JULCoreLogger newLogger(FieldConverter fieldConverter) {
    return new JULCoreLogger(
        fqcn, logger, context, condition, threadContextFunction, executor, fieldConverter);
  }

  private JULCoreLogger newLogger(String fqcn) {
    return new JULCoreLogger(
        fqcn, logger, context, condition, threadContextFunction, executor, fieldConverter);
  }

  private Supplier<Runnable> mdcContext() {
    return () -> {
      // rendering thread (saving context from old thread)
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      // function runs in executor thread (applying context to new thread)
      return () -> {
        if (copyOfContextMap != null) {
          MDC.setContextMap(copyOfContextMap);
        }
      };
    };
  }

  public String toString() {
    return "JULCoreLogger[" + logger.getName() + "]";
  }

  private static void handleException(Throwable e) {
    EchopraxiaService.getInstance().getExceptionHandler().handleException(e);
  }
}
