package com.tersesystems.echopraxia.log4j;

import static com.tersesystems.echopraxia.api.Utilities.joinFields;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.log4j.layout.EchopraxiaFieldsMessage;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A core logger using the Log4J API. */
public class Log4JCoreLogger implements CoreLogger {

  private final ExtendedLogger logger;
  private final Context context;
  private final Condition condition;
  private final Executor executor;
  private final String fqcn;

  private final Supplier<Runnable> threadContextFunction;

  Log4JCoreLogger(@NotNull String fqcn, @NotNull ExtendedLogger log4jLogger) {
    this.fqcn = fqcn;
    this.logger = log4jLogger;
    this.context = new Context();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
    this.threadContextFunction = threadContext();
  }

  protected Log4JCoreLogger(
      @NotNull String fqcn,
      @NotNull ExtendedLogger log4jLogger,
      @NotNull Log4JCoreLogger.Context context,
      @NotNull Condition condition,
      @NotNull Executor executor,
      @NotNull Supplier<Runnable> threadContextSupplier) {
    this.fqcn = fqcn;
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
    this.threadContextFunction = threadContextSupplier;
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
  public <FB> @NotNull Log4JCoreLogger withFields(
      @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder) {
    Context newContext = context.withFields(() -> convertToFields(f.apply(builder)));
    return newLogger(newContext);
  }

  @Override
  public @NotNull Log4JCoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    Supplier<List<Field>> fieldSupplier = mapTransform.apply(ThreadContext::getImmutableContext);
    return newLogger(context.withFields(fieldSupplier));
  }

  @Override
  public @NotNull CoreLogger withThreadLocal(Supplier<Runnable> newSupplier) {
    Supplier<Runnable> supplier =
        () -> {
          final Runnable r1 = newSupplier.get();
          final Runnable r2 = threadContextFunction.get();
          return () -> {
            r1.run();
            r2.run();
          };
        };
    return newLogger(supplier);
  }

  @Override
  public @NotNull Log4JCoreLogger withCondition(@NotNull Condition condition) {
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
  public @NotNull Log4JCoreLogger withExecutor(@NotNull Executor executor) {
    return new Log4JCoreLogger(fqcn, logger, context, condition, executor, threadContextFunction);
  }

  @Override
  public @NotNull Log4JCoreLogger withFQCN(@NotNull String fqcn) {
    return new Log4JCoreLogger(fqcn, logger, context, condition, executor, threadContextFunction);
  }

  @NotNull
  public Log4JCoreLogger withMarker(@NotNull Marker marker) {
    return newLogger(this.context.withMarker(marker));
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    if (condition == Condition.always()) {
      return logger.isEnabled(convertLevel(level), context.getMarker());
    }
    if (condition == Condition.never()) {
      return false;
    }
    if (logger.isEnabled(convertLevel(level), context.getMarker())) {
      Log4JLoggingContext snapshotContext = new Log4JLoggingContext(context);
      return condition.test(level, snapshotContext);
    }
    return false;
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    final Condition bothConditions = this.condition.and(condition);
    if (bothConditions == Condition.always()) {
      return logger.isEnabled(convertLevel(level), context.getMarker());
    }
    if (bothConditions == Condition.never()) {
      return false;
    }
    if (logger.isEnabled(convertLevel(level), context.getMarker())) {
      Log4JLoggingContext snapshotContext = new Log4JLoggingContext(context);
      return bothConditions.test(level, snapshotContext);
    }
    return false;
  }

  @Override
  public void log(@NotNull Level level, String message) {
    final Marker marker = context.getMarker();
    final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
    // the isEnabled check always goes before the condition check, as conditions can be expensive
    if (logger.isEnabled(log4jLevel, marker)) {
      Log4JLoggingContext memoContext = new Log4JLoggingContext(context);
      if (condition.test(level, memoContext)) {
        final Message m = createMessage(memoContext, message);
        logger.logMessage(fqcn, log4jLevel, marker, m, null);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @Nullable String messageTemplate,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    // because the isEnabled check looks for message and throwable, we have to
    // calculate them right up front.
    final Marker marker = context.getMarker();
    final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
    if (logger.isEnabled(log4jLevel, marker)) {
      Log4JLoggingContext argContext =
          new Log4JLoggingContext(context, () -> convertToFields(f.apply(builder)));
      if (condition.test(level, argContext)) {
        final Throwable e = findThrowable(argContext.getArgumentFields());
        final Message message = createMessage(argContext, messageTemplate);
        logger.logMessage(fqcn, log4jLevel, marker, message, e);
      }
    }
  }

  private List<Field> convertToFields(FieldBuilderResult result) {
    if (result == null) {
      // XXX log an error
      return Collections.emptyList();
    }
    return result.fields();
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {
    final Marker marker = context.getMarker();
    final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
    if (logger.isEnabled(log4jLevel, marker)) {
      // We want to memoize context fields even if no argument...
      Log4JLoggingContext argContext = new Log4JLoggingContext(context);
      if (this.condition.and(condition).test(level, argContext)) {
        final Message m = createMessage(argContext, message);
        logger.logMessage(fqcn, log4jLevel, marker, m, null);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String messageTemplate,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    final Marker marker = context.getMarker();
    final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
    if (logger.isEnabled(log4jLevel, marker)) {
      Log4JLoggingContext argContext =
          new Log4JLoggingContext(context, () -> convertToFields(f.apply(builder)));
      if (this.condition.and(condition).test(level, argContext)) {
        final Throwable e = findThrowable(argContext.getArgumentFields());
        final Message message = createMessage(argContext, messageTemplate);
        logger.logMessage(fqcn, log4jLevel, marker, message, e);
      }
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          consumer.accept(newHandle(level, builder, location));
        });
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          consumer.accept(newHandle(level, c, builder, location));
        });
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          consumer.accept(newHandle(level, builder, location));
        });
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          consumer.accept(newHandle(level, c, builder, location));
        });
  }

  protected Message createMessage(Log4JLoggingContext ctx, String template) {
    return new EchopraxiaFieldsMessage(template, ctx.getLoggerFields(), ctx.getArgumentFields());
  }

  protected org.apache.logging.log4j.Level convertLevel(Level level) {
    switch (level) {
      case ERROR:
        return org.apache.logging.log4j.Level.ERROR;
      case WARN:
        return org.apache.logging.log4j.Level.WARN;
      case INFO:
        return org.apache.logging.log4j.Level.INFO;
      case DEBUG:
        return org.apache.logging.log4j.Level.DEBUG;
      case TRACE:
        return org.apache.logging.log4j.Level.TRACE;
    }
    throw new IllegalStateException("Unknown level " + level);
  }

  protected Throwable findThrowable(List<Field> fields) {
    for (Field field : fields) {
      final Value<?> value = field.value();
      // FIXME This needs to look for both name and value
      if (value instanceof Value.ExceptionValue) {
        return ((Value.ExceptionValue) value).raw();
      }
    }
    return null;
  }

  protected Supplier<Runnable> threadContext() {
    return () -> {
      final Map<String, String> copyOfContextMap = ThreadContext.getImmutableContext();
      final ThreadContext.ContextStack contextStack = ThreadContext.getImmutableStack();
      return () -> {
        ThreadContext.clearAll();
        if (copyOfContextMap != null) {
          ThreadContext.putAll(copyOfContextMap);
        }
        if (contextStack != null) {
          ThreadContext.setStack(contextStack);
        }
      };
    };
  }

  protected void runAsyncLog(Runnable runnable) {
    Function<Throwable, ? extends Void> exceptionHandler =
        e -> {
          // Usually we get to this point when you have thread local dependent code in your
          // logger.withContext() block, and your executor doesn't have those thread locals
          // so you NPE.
          //
          // We need to log this error, but since it could be part of the logger context
          // that is causing this error, we can't log the error with the same logger.
          //
          // Fallback to the underlying logger to render it.
          final Throwable cause = e.getCause(); // strip the CompletionException
          logger.error("Uncaught exception when running asyncLog", cause);
          return null;
        };
    CompletableFuture.runAsync(runnable, executor).exceptionally(exceptionHandler);
  }

  protected boolean includeLocation() {
    // In theory, I want org.apache.logging.log4j.util.Supplier<LoggerConfig>
    // but pattern matching in Java isn't great for this.
    if (logger instanceof org.apache.logging.log4j.core.Logger) {
      final org.apache.logging.log4j.core.Logger coreLogger =
          (org.apache.logging.log4j.core.Logger) logger;
      final LoggerConfig loggerConfig = coreLogger.get();
      // I think this is equivalent to coreLogger.requiresLocation(), but this is
      // public while coreLogger.requiresLocation() is private.
      return loggerConfig.isIncludeLocation();
    } else {
      return false;
    }
  }

  @NotNull
  private Log4JCoreLogger newLogger(Context newContext) {
    return new Log4JCoreLogger(
        fqcn, logger, newContext, condition, executor, threadContextFunction);
  }

  @NotNull
  private Log4JCoreLogger newLogger(Supplier<Runnable> supplier) {
    return new Log4JCoreLogger(fqcn, logger, context, condition, executor, supplier);
  }

  @NotNull
  private Log4JCoreLogger newLogger(@NotNull Condition condition) {
    return new Log4JCoreLogger(fqcn, logger, context, condition, executor, threadContextFunction);
  }

  @NotNull
  private <FB> LoggerHandle<FB> newHandle(
      @NotNull Level level, @NotNull FB builder, StackTraceElement location) {
    return new LoggerHandle<FB>() {
      @Override
      public void log(@Nullable String messageTemplate) {
        final Marker marker = context.getMarker();
        final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
        if (logger.isEnabled(log4jLevel, marker)) {
          Log4JLoggingContext memo = new Log4JLoggingContext(context);
          if (condition.test(level, memo)) {
            final Message message = createMessage(memo, messageTemplate);
            logger.logMessage(log4jLevel, marker, fqcn, location, message, null);
          }
        }
      }

      @Override
      public void log(
          @Nullable String messageTemplate, @NotNull Function<FB, FieldBuilderResult> f) {
        // because the isEnabled check looks for message and throwable, we have to
        // calculate them right up front.
        final Marker marker = context.getMarker();
        final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
        if (logger.isEnabled(log4jLevel, marker)) {
          Log4JLoggingContext memo =
              new Log4JLoggingContext(context, () -> convertToFields(f.apply(builder)));
          if (condition.test(level, memo)) {
            final Throwable e = findThrowable(memo.getArgumentFields());
            final Message message = createMessage(memo, messageTemplate);
            // When passing a condition through with explicit arguments, we pull the args
            // and make them available through context.

            logger.logMessage(log4jLevel, marker, fqcn, location, message, e);
          }
        }
      }
    };
  }

  @NotNull
  private <FB> LoggerHandle<FB> newHandle(
      @NotNull Level level, @NotNull Condition c, @NotNull FB builder, StackTraceElement location) {
    return new LoggerHandle<FB>() {
      @Override
      public void log(@Nullable String messageTemplate) {
        // XXX this should just call a regular logger.
        final Marker marker = context.getMarker();
        final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
        if (logger.isEnabled(log4jLevel, marker)) {
          Log4JLoggingContext memo = new Log4JLoggingContext(context);
          if (condition.and(c).test(level, memo)) {
            final Message message = createMessage(memo, messageTemplate);
            logger.logMessage(log4jLevel, marker, fqcn, location, message, null);
          }
        }
      }

      @Override
      public void log(
          @Nullable String messageTemplate, @NotNull Function<FB, FieldBuilderResult> f) {
        final Marker marker = context.getMarker();
        final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
        if (logger.isEnabled(log4jLevel, marker)) {
          Log4JLoggingContext memo =
              new Log4JLoggingContext(context, () -> convertToFields(f.apply(builder)));
          if (condition.and(c).test(level, memo)) {
            final Throwable e = findThrowable(memo.getArgumentFields());
            final Message message = createMessage(memo, messageTemplate);
            logger.logMessage(log4jLevel, marker, fqcn, location, message, e);
          }
        }
      }
    };
  }

  public String toString() {
    return "Log4JCoreLogger[" + logger.getName() + "]";
  }

  protected static class Context {
    protected final Supplier<List<Field>> fieldsSupplier;
    protected final Marker marker;

    Context() {
      this.fieldsSupplier = Collections::emptyList;
      this.marker = null;
    }

    protected Context(Supplier<List<Field>> f, Marker m) {
      this.fieldsSupplier = f;
      this.marker = m;
    }

    public @NotNull List<Field> getLoggerFields() {
      return fieldsSupplier.get();
    }

    public Marker getMarker() {
      return marker;
    }

    public Context withFields(Supplier<List<Field>> o) {
      Supplier<List<Field>> joinedFields = joinFields(o, this::getLoggerFields);
      return new Context(joinedFields, this.getMarker());
    }

    public Context withMarker(Marker m) {
      return new Context(this.fieldsSupplier, m);
    }
  }
}
