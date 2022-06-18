package com.tersesystems.echopraxia.logstash;

import static org.slf4j.event.EventConstants.*;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.Value;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.slf4j.Marker;

/** Logstash implementation of CoreLogger. */
public class LogstashCoreLogger implements CoreLogger {

  // The logger context property used to set up caller info for async logging.
  public static final String ECHOPRAXIA_ASYNC_CALLER_PROPERTY = "echopraxia.async.caller";

  private final ch.qos.logback.classic.Logger logger;
  private final LogstashLoggingContext context;
  private final Condition condition;
  private final Executor executor;
  private final String fqcn;
  private final Supplier<Runnable> threadContextFunction;

  protected LogstashCoreLogger(String fqcn, ch.qos.logback.classic.Logger logger) {
    this.fqcn = fqcn;
    this.logger = logger;
    this.context = LogstashLoggingContext.empty();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
    this.threadContextFunction = mdcContext();
  }

  public LogstashCoreLogger(
      @NotNull String fqcn,
      @NotNull ch.qos.logback.classic.Logger logger,
      @NotNull LogstashLoggingContext context,
      @NotNull Condition condition,
      @NotNull Executor executor,
      @NotNull Supplier<Runnable> threadContextSupplier) {
    this.fqcn = fqcn;
    this.logger = logger;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
    this.threadContextFunction = threadContextSupplier;
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

  /**
   * Returns the underlying SLF4J logger.
   *
   * @return the SLF4J logger.
   */
  public ch.qos.logback.classic.Logger logger() {
    return logger;
  }

  @Override
  @NotNull
  public String getName() {
    return logger.getName();
  }

  /**
   * Returns the condition.
   *
   * @return the condition.
   */
  @Override
  public @NotNull Condition condition() {
    return this.condition;
  }

  @Override
  public @NotNull String fqcn() {
    return fqcn;
  }

  @Override
  public <FB> @NotNull CoreLogger withFields(
      @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder) {
    final LogstashLoggingContext contextWithFields =
        this.context.withFields(() -> convertToFields(f.apply(builder)));
    return new LogstashCoreLogger(
        fqcn, logger, contextWithFields, condition, executor, threadContextFunction);
  }

  public CoreLogger withMarkers(Marker... markers) {
    LogstashLoggingContext newContext =
      new LogstashLoggingContext(Collections::emptyList, () -> Arrays.asList(markers));
    return new LogstashCoreLogger(
      fqcn, logger, this.context.and(newContext), condition, executor, threadContextFunction);
  }

  private List<Field> convertToFields(FieldBuilderResult result) {
    if (result == null) {
      // XXX log an error
      return Collections.emptyList();
    }
    return result.fields();
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    LogstashLoggingContext newContext = context.withFields(mapTransform.apply(MDC::getCopyOfContextMap));
    return new LogstashCoreLogger(fqcn, logger, newContext, condition, executor, threadContextFunction);
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
    return new LogstashCoreLogger(fqcn, logger, context, condition, executor, supplier);
  }

  @Override
  public @NotNull CoreLogger withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      // XXX this can't be right, test it
      return this;
    }
    if (condition == Condition.never()) {
      if (this.condition == Condition.never()) {
        return this;
      }
      return new LogstashCoreLogger(
          fqcn, logger, context, condition, executor, threadContextFunction);
    }
    return new LogstashCoreLogger(
        fqcn, logger, context, this.condition.and(condition), executor, threadContextFunction);
  }

  @Override
  public @NotNull CoreLogger withExecutor(@NotNull Executor executor) {
    return new LogstashCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction);
  }

  @Override
  public @NotNull CoreLogger withFQCN(@NotNull String fqcn) {
    return new LogstashCoreLogger(
        fqcn, logger, context, condition, executor, threadContextFunction);
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    if (condition == Condition.never()) {
      return false;
    }
    Marker marker = context.resolveMarkers();
    return logger.isEnabledFor(marker, convertLogbackLevel(level))
        && condition.test(level, context);
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    if (condition == Condition.always()) {
      return isEnabled(level);
    }
    if (condition == Condition.never()) {
      return false;
    }
    Marker marker = context.resolveMarkers();
    return logger.isEnabledFor(marker, convertLogbackLevel(level))
        && this.condition.and(condition).test(level, context);
  }

  @Override
  public void log(@NotNull Level level, String message) {
    Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level)) && condition.test(level, context)) {
      logger.log(context.resolveFieldsAndMarkers(), fqcn, convertLevel(level), message, null, null);
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    // When passing a condition through with explicit arguments, we pull the args and make
    // them available through context.
    final Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))) {
      final List<Field> args = convertToFields(f.apply(builder));
      final LogstashLoggingContext argContext = context.withFields(() -> args);
      if (condition.test(level, argContext)) {
        final Object[] arguments = convertArguments(args);
        logger.log(context.resolveFieldsAndMarkers(), fqcn, convertLevel(level), message, arguments, null);
      }
    }
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, String message) {
    final Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))
        && this.condition.and(condition).test(level, context)) {
      logger.log(context.resolveFieldsAndMarkers(), fqcn, convertLevel(level), message, null, null);
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    final Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))) {
      // When passing a condition through with explicit arguments, we pull the args and make
      // them available through context.
      final List<Field> args = convertToFields(f.apply(builder));
      final LogstashLoggingContext argContext = context.withFields(() -> args);
      if (this.condition.and(condition).test(level, argContext)) {
        final Object[] arguments = convertArguments(args);
        logger.log(context.resolveFieldsAndMarkers(), fqcn, convertLevel(level), message, arguments, null);
      }
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    Marker callerMarker = callerMarker();
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          LogstashCoreLogger callerLogger = newLogger(newContext(callerMarker));
          consumer.accept(newHandle(level, builder, callerLogger));
        });
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    Marker callerMarker = callerMarker();
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          LogstashCoreLogger callerLogger = newLogger(newContext(callerMarker));
          final LoggerHandle<FB> loggerHandle = newHandle(level, c, builder, callerLogger);
          consumer.accept(loggerHandle);
        });
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    final Marker callerMarker = callerMarker();
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          LogstashCoreLogger callerLogger = newLogger(newContext(extraFields, callerMarker));
          final LoggerHandle<FB> loggerHandle = newHandle(level, builder, callerLogger);
          consumer.accept(loggerHandle);
        });
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    final Marker callerMarker = callerMarker();
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          LogstashCoreLogger callerLogger = newLogger(newContext(extraFields, callerMarker));
          final LoggerHandle<FB> loggerHandle = newHandle(level, c, builder, callerLogger);
          consumer.accept(loggerHandle);
        });
  }

  @Nullable
  protected LogstashCallerMarker callerMarker() {
    if (isAsyncCallerEnabled()) {
      return new LogstashCallerMarker(fqcn, new Throwable());
    } else {
      return null;
    }
  }

  /**
   * Returns true if the logback context property "echopraxia.async.caller" is "true", false
   * otherwise.
   *
   * @return if caller data is enabled.
   */
  protected boolean isAsyncCallerEnabled() {
    return LogstashLoggerProvider.asyncCallerEnabled;
  }

  // Top level conversion to Logback must be StructuredArgument, with an optional throwable
  // at the end of the array.
  protected Object[] convertArguments(List<Field> args) {
    // Top level arguments must be StructuredArguments, +1 for throwable

    // Exceptions have always been a little wacky.

    // Condition 1:
    // In SLF4J, if the last argument is a throwable AND there is no parameter for it in the
    // template,
    // then the throwable is treated as the ThrowableProxy (and see the stack trace).
    // logger.error("message but no parameter", e)

    // Condition 2:
    // If the message parameter is passed in, then the exception is treated as an argument!
    // logger.error("message with parameter {}", e)
    // This will NOT show a stacktrace, and will instead render e.toString().

    // We want the LAST exception to always show up as the canonical exception, but we will
    // always include it as a "value" parameter.
    Value<Throwable> throwable = null;
    List<Object> arguments = new ArrayList<>(args.size() + 1);
    for (Field field : args) {
      final String name = field.name();
      final Value<?> value = field.value();
      if (value.type() == Value.Type.EXCEPTION) {
        throwable = (Value.ExceptionValue) value;
        StructuredArgument arg = StructuredArguments.keyValue(name, throwable.raw());
        arguments.add(arg);
      } else {
        StructuredArgument arg =
            field instanceof Field.ValueField
                ? StructuredArguments.value(name, value)
                : StructuredArguments.keyValue(name, value);
        arguments.add(arg);
      }
    }

    // If the exception exists, it must be raw and at the end of the array.
    if (throwable != null) {
      arguments.add(throwable.raw());
    }
    return arguments.toArray();
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
              logger.error("Uncaught exception when running asyncLog", cause);
              return null;
            });
  }

  protected ch.qos.logback.classic.Level convertLogbackLevel(Level level) {
    switch (level) {
      case ERROR:
        return ch.qos.logback.classic.Level.ERROR;
      case WARN:
        return ch.qos.logback.classic.Level.WARN;
      case INFO:
        return ch.qos.logback.classic.Level.INFO;
      case DEBUG:
        return ch.qos.logback.classic.Level.DEBUG;
      case TRACE:
        return ch.qos.logback.classic.Level.TRACE;
    }
    throw new IllegalStateException("No level found!");
  }

  protected int convertLevel(Level level) {
    switch (level) {
      case ERROR:
        return ERROR_INT;
      case WARN:
        return WARN_INT;
      case INFO:
        return INFO_INT;
      case DEBUG:
        return DEBUG_INT;
      case TRACE:
        return TRACE_INT;
    }
    throw new IllegalStateException("No level found!");
  }

  @NotNull
  protected LogstashLoggingContext newContext(
      @NotNull Supplier<List<Field>> fieldsSupplier, Marker callerMarker) {
    Supplier<List<Field>> fields =
        LogstashLoggingContext.joinFields(fieldsSupplier, context::getFields);
    Supplier<List<Marker>> markers;
    if (callerMarker == null) {
      markers = context::getMarkers;
    } else {
      markers =
          LogstashLoggingContext.joinMarkers(
              () -> Collections.singletonList(callerMarker), context::getMarkers);
    }
    return new LogstashLoggingContext(fields, markers);
  }

  protected LogstashLoggingContext newContext(Marker callerMarker) {
    if (callerMarker == null) {
      return context;
    } else {
      Supplier<List<Marker>> markers;
      markers =
          LogstashLoggingContext.joinMarkers(
              () -> Collections.singletonList(callerMarker), context::getMarkers);
      return new LogstashLoggingContext(context::getFields, markers);
    }
  }

  protected LogstashCoreLogger newLogger(LogstashLoggingContext newContext) {
    return new LogstashCoreLogger(
        fqcn, logger, newContext, condition, executor, threadContextFunction);
  }

  @NotNull
  protected <FB> LoggerHandle<FB> newHandle(
      @NotNull Level level, @NotNull FB builder, LogstashCoreLogger callerLogger) {
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
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull FB builder,
      LogstashCoreLogger callerLogger) {
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

  public String toString() {
    return "LogstashCoreLogger[" + logger.getName() + "]";
  }
}
