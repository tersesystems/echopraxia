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
  private final FieldConverter fieldConverter;

  public Log4JCoreLogger(@NotNull String fqcn, @NotNull ExtendedLogger log4jLogger) {
    this.fqcn = fqcn;
    this.logger = log4jLogger;
    this.context = Context.empty();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
    this.threadContextFunction = threadContext();
    this.fieldConverter = Log4JFieldConverter.instance();
  }

  protected Log4JCoreLogger(
      @NotNull String fqcn,
      @NotNull ExtendedLogger log4jLogger,
      @NotNull Log4JCoreLogger.Context context,
      @NotNull Condition condition,
      @NotNull Executor executor,
      @NotNull Supplier<Runnable> threadContextSupplier,
      @NotNull FieldConverter fieldConverter) {
    this.fqcn = fqcn;
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
    this.threadContextFunction = threadContextSupplier;
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
          return () -> {
            try {
              final Runnable r1 = newSupplier.get();
              final Runnable r2 = threadContextFunction.get();
              r1.run();
              r2.run();
            } catch (Exception e) {
              handleException(e);
            }
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
    return new Log4JCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction, fieldConverter);
  }

  @Override
  public @NotNull CoreLogger withFieldConverter(FieldConverter fieldConverter) {
    return new Log4JCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction, fieldConverter);
  }

  @Override
  public @NotNull Log4JCoreLogger withFQCN(@NotNull String fqcn) {
    return new Log4JCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction, fieldConverter);
  }

  @NotNull
  public Log4JCoreLogger withMarker(@NotNull Marker marker) {
    return newLogger(this.context.withMarker(marker));
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    try {
      if (condition == Condition.always()) {
        return logger.isEnabled(convertLevel(level), context.getMarker());
      }
      if (condition == Condition.never()) {
        return false;
      }
      if (logger.isEnabled(convertLevel(level), context.getMarker())) {
        Log4JLoggingContext snapshotContext = new Log4JLoggingContext(this, context);
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
        return logger.isEnabled(convertLevel(level), context.getMarker());
      }
      if (bothConditions == Condition.never()) {
        return false;
      }
      if (logger.isEnabled(convertLevel(level), context.getMarker())) {
        Log4JLoggingContext snapshotContext = new Log4JLoggingContext(this, context);
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
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx = new Log4JLoggingContext(this, context.withFields(extraFields));
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
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx = new Log4JLoggingContext(this, context.withFields(extraFields));
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
    try {
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      // the isEnabled check always goes before the condition check, as conditions can be expensive
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx = new Log4JLoggingContext(this, context);
        if (condition.test(level, ctx)) {
          final Message m = createMessage(message, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, m, null);
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
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      // the isEnabled check always goes before the condition check, as conditions can be expensive
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx = new Log4JLoggingContext(this, context.withFields(extraFields));
        if (condition.test(level, ctx)) {
          final Message m = createMessage(message, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, m, null);
        }
      }
    } catch (Exception e) {
      handleException(e);
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
    try {
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx =
            new Log4JLoggingContext(this, context, () -> convertToFields(f.apply(builder)));
        if (condition.test(level, ctx)) {
          final Throwable e = findThrowable(ctx.getArgumentFields());
          final Message message = createMessage(messageTemplate, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, message, e);
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
      @Nullable String messageTemplate,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    try {
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx =
            new Log4JLoggingContext(
                this, context.withFields(extraFields), () -> convertToFields(f.apply(builder)));
        if (condition.test(level, ctx)) {
          final Throwable e = findThrowable(ctx.getArgumentFields());
          final Message message = createMessage(messageTemplate, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, message, e);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  private List<Field> convertToFields(FieldBuilderResult result) {
    return result.fields();
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {
    try {
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        // We want to memoize context fields even if no argument...
        Log4JLoggingContext ctx = new Log4JLoggingContext(this, context);
        if (this.condition.and(condition).test(level, ctx)) {
          final Message m = createMessage(message, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, m, null);
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
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        // We want to memoize context fields even if no argument...
        Log4JLoggingContext ctx = new Log4JLoggingContext(this, context.withFields(extraFields));
        if (this.condition.and(condition).test(level, ctx)) {
          final Message m = createMessage(message, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, m, null);
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
      @Nullable String messageTemplate,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    try {
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx =
            new Log4JLoggingContext(this, context, () -> convertToFields(f.apply(builder)));
        if (this.condition.and(condition).test(level, ctx)) {
          final Throwable e = findThrowable(ctx.getArgumentFields());
          final Message message = createMessage(messageTemplate, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, message, e);
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
      @Nullable String messageTemplate,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    try {
      final Marker marker = context.getMarker();
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      if (logger.isEnabled(log4jLevel, marker)) {
        Log4JLoggingContext ctx =
            new Log4JLoggingContext(
                this, context.withFields(extraFields), () -> convertToFields(f.apply(builder)));
        if (this.condition.and(condition).test(level, ctx)) {
          final Throwable e = findThrowable(ctx.getArgumentFields());
          final Message message = createMessage(messageTemplate, ctx);
          logger.logMessage(fqcn, log4jLevel, marker, message, e);
        }
      }
    } catch (Exception e) {
      handleException(e);
    }
  }

  @Override
  public @NotNull <FB> LoggerHandle<FB> logHandle(@NotNull Level level, @NotNull FB builder) {
    return new LoggerHandle<FB>() {
      final org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
      final Marker marker = context.getMarker();

      @Override
      public void log(@Nullable String messageTemplate) {
        Log4JLoggingContext ctx = new Log4JLoggingContext(Log4JCoreLogger.this, context);
        final Throwable e = findThrowable(ctx.getArgumentFields());
        final Message message = createMessage(messageTemplate, ctx);
        logger.logMessage(fqcn, log4jLevel, marker, message, e);
      }

      @Override
      public void log(
          @Nullable String messageTemplate, @NotNull Function<FB, FieldBuilderResult> f) {
        Log4JLoggingContext ctx =
            new Log4JLoggingContext(
                Log4JCoreLogger.this, context, () -> convertToFields(f.apply(builder)));
        final Throwable e = findThrowable(ctx.getArgumentFields());
        final Message message = createMessage(messageTemplate, ctx);
        logger.logMessage(fqcn, log4jLevel, marker, message, e);
      }
    };
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    if (logger.isEnabled(convertLevel(level), context.getMarker())) {
      StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
      Runnable threadLocalRunnable = threadContextFunction.get();
      Runnable runnable =
          createRunnable(
              location, threadLocalRunnable, context, level, condition, consumer, builder);
      runAsyncLog(runnable);
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    if (logger.isEnabled(convertLevel(level), context.getMarker())) {
      StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
      Runnable threadLocalRunnable = threadContextFunction.get();
      Runnable runnable =
          createRunnable(
              location, threadLocalRunnable, context, level, condition.and(c), consumer, builder);
      runAsyncLog(runnable);
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    if (logger.isEnabled(convertLevel(level), context.getMarker())) {
      StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
      Runnable threadLocalRunnable = threadContextFunction.get();
      Runnable runnable =
          createRunnable(
              location,
              threadLocalRunnable,
              context.withFields(extraFields),
              level,
              condition,
              consumer,
              builder);
      runAsyncLog(runnable);
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    if (logger.isEnabled(convertLevel(level), context.getMarker())) {
      StackTraceElement location = includeLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
      Runnable threadLocalRunnable = threadContextFunction.get();
      Runnable runnable =
          createRunnable(
              location,
              threadLocalRunnable,
              context.withFields(extraFields),
              level,
              condition.and(c),
              consumer,
              builder);
      runAsyncLog(runnable);
    }
  }

  private <FB> Runnable createRunnable(
      @Nullable StackTraceElement location,
      Runnable threadLocalRunnable,
      Context extraContext,
      Level level,
      Condition c,
      Consumer<LoggerHandle<FB>> consumer,
      FB builder) {
    return () -> {
      threadLocalRunnable.run();
      final LoggerHandle<FB> loggerHandle = newHandle(location, extraContext, level, c, builder);
      consumer.accept(loggerHandle);
    };
  }

  protected <FB> LoggerHandle<FB> newHandle(
      @Nullable StackTraceElement location,
      @NotNull Context extraContext,
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull FB builder) {
    return new LoggerHandle<FB>() {
      @Override
      public void log(@Nullable String messageTemplate) {
        try {
          Marker marker = extraContext.getMarker();
          org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
          if (logger.isEnabled(log4jLevel, marker)) {
            Log4JLoggingContext ctx = new Log4JLoggingContext(Log4JCoreLogger.this, extraContext);
            if (c.test(level, ctx)) {
              final Message message = createMessage(messageTemplate, ctx);
              logger.logMessage(log4jLevel, marker, fqcn, location, message, null);
            }
          }
        } catch (Exception e) {
          handleException(e);
        }
      }

      @Override
      public void log(
          @Nullable String messageTemplate, @NotNull Function<FB, FieldBuilderResult> f) {
        try {
          Marker marker = extraContext.getMarker();
          org.apache.logging.log4j.Level log4jLevel = convertLevel(level);
          if (logger.isEnabled(log4jLevel, marker)) {
            Log4JLoggingContext ctx =
                new Log4JLoggingContext(
                    Log4JCoreLogger.this, extraContext, () -> convertToFields(f.apply(builder)));
            if (c.test(level, ctx)) {
              final Throwable e = findThrowable(ctx.getArgumentFields());
              final Message message = createMessage(messageTemplate, ctx);
              logger.logMessage(log4jLevel, marker, fqcn, location, message, e);
            }
          }
        } catch (Exception e) {
          handleException(e);
        }
      }
    };
  }

  protected Message createMessage(String template, Log4JLoggingContext ctx) {
    List<Field> loggerFields = ctx.getLoggerFields();
    List<Field> input = new ArrayList<>(loggerFields.size());
    for (Field loggerField : loggerFields) {
      Field f = (Field) fieldConverter.convertLoggerField(loggerField);
      input.add(f);
    }

    List<Field> argumentFields = ctx.getArgumentFields();
    List<Field> args = new ArrayList<>(argumentFields.size());
    for (Field argumentField : argumentFields) {
      Field f = (Field) fieldConverter.convertArgumentField(argumentField);
      args.add(f);
    }
    return new EchopraxiaFieldsMessage(template, input, args);
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
          handleException(cause);
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
        fqcn, logger, newContext, condition, executor, threadContextFunction, fieldConverter);
  }

  @NotNull
  private Log4JCoreLogger newLogger(Supplier<Runnable> threadContextFunction) {
    return new Log4JCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction, fieldConverter);
  }

  @NotNull
  private Log4JCoreLogger newLogger(@NotNull Condition condition) {
    return new Log4JCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction, fieldConverter);
  }

  public String toString() {
    return "Log4JCoreLogger[" + logger.getName() + "]";
  }

  protected static class Context {
    protected final Supplier<List<Field>> fieldsSupplier;
    protected final Marker marker;

    private static final Context EMPTY = new Context();

    static Context empty() {
      return EMPTY;
    }

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

  private static void handleException(Throwable e) {
    CoreLoggerFactory.getExceptionHandler().handleException(e);
  }
}
