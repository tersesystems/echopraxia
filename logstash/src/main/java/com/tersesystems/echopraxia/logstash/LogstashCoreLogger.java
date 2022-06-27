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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.slf4j.Marker;

/** Logstash implementation of CoreLogger. */
public class LogstashCoreLogger implements CoreLogger {

  // The logger context property used to set up caller info for async logging.
  public static final String ECHOPRAXIA_ASYNC_CALLER_PROPERTY = "echopraxia.async.caller";

  private final ch.qos.logback.classic.Logger logger;
  private final Context context;
  private final Condition condition;
  private final Executor executor;
  private final String fqcn;
  private final Supplier<Runnable> threadContextFunction;

  protected LogstashCoreLogger(String fqcn, ch.qos.logback.classic.Logger logger) {
    this.fqcn = fqcn;
    this.logger = logger;
    this.context = Context.empty();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
    this.threadContextFunction = mdcContext();
  }

  public LogstashCoreLogger(
      @NotNull String fqcn,
      @NotNull ch.qos.logback.classic.Logger logger,
      @NotNull LogstashCoreLogger.Context context,
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
    final Context contextWithFields =
        this.context.withFields(() -> convertToFields(f.apply(builder)));
    return new LogstashCoreLogger(
        fqcn, logger, contextWithFields, condition, executor, threadContextFunction);
  }

  public CoreLogger withMarkers(Marker... markers) {
    final Context contextWithMarkers = this.context.withMarkers(() -> Arrays.asList(markers));
    return new LogstashCoreLogger(
        fqcn, logger, contextWithMarkers, condition, executor, threadContextFunction);
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    Context newContext = context.withFields(mapTransform.apply(MDC::getCopyOfContextMap));
    return new LogstashCoreLogger(
        fqcn, logger, newContext, condition, executor, threadContextFunction);
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
    return new LogstashCoreLogger(
        fqcn, logger, context, condition, executor, joinedThreadContextFunction);
  }

  @Override
  public @NotNull CoreLogger withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this; // "x && true" is always x
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
    if (condition == Condition.always()) {
      return logger.isEnabledFor(context.resolveMarkers(), convertLogbackLevel(level));
    }
    if (condition == Condition.never()) {
      return false;
    }
    Marker marker = context.resolveMarkers();
    if (logger.isEnabledFor(marker, convertLogbackLevel(level))) {
      LogstashLoggingContext snapshotContext = new LogstashLoggingContext(context);
      return condition.test(level, snapshotContext);
    }
    return false;
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    final Condition bothConditions = this.condition.and(condition);
    if (bothConditions == Condition.always()) {
      return logger.isEnabledFor(context.resolveMarkers(), convertLogbackLevel(level));
    }
    if (bothConditions == Condition.never()) {
      return false;
    }
    Marker marker = context.resolveMarkers();
    if (logger.isEnabledFor(marker, convertLogbackLevel(level))) {
      LogstashLoggingContext snapshotContext = new LogstashLoggingContext(context);
      return bothConditions.test(level, snapshotContext);
    }
    return false;
  }

  @Override
  public void log(@NotNull Level level, String message) {
    Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))) {
      LogstashLoggingContext snapshotContext = new LogstashLoggingContext(context);
      if (condition.test(level, snapshotContext)) {
        logger.log(
            resolveLoggerFields(m, snapshotContext),
            fqcn,
            convertLevel(level),
            message,
            null,
            null);
      }
    }
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    final Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))) {
      LoggingContext ctx =
          new LogstashLoggingContext(context, () -> convertToFields(f.apply(builder)));
      if (condition.test(level, ctx)) {
        final Object[] arguments = convertArguments(ctx.getArgumentFields());
        logger.log(
            resolveLoggerFields(m, ctx), fqcn, convertLevel(level), message, arguments, null);
      }
    }
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, String message) {
    final Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))) {
      LoggingContext snapshotContext = new LogstashLoggingContext(context);
      if (this.condition.and(condition).test(level, snapshotContext)) {
        logger.log(
            resolveLoggerFields(m, snapshotContext),
            fqcn,
            convertLevel(level),
            message,
            null,
            null);
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
    final Marker m = context.resolveMarkers();
    if (logger.isEnabledFor(m, convertLogbackLevel(level))) {
      LoggingContext snapshotContext =
          new LogstashLoggingContext(context, () -> convertToFields(f.apply(builder)));
      if (this.condition.and(condition).test(level, snapshotContext)) {
        final Object[] arguments = convertArguments(snapshotContext.getArgumentFields());
        logger.log(
            resolveLoggerFields(m, snapshotContext),
            fqcn,
            convertLevel(level),
            message,
            arguments,
            null);
      }
    }
  }

  @Override
  public <FB> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    @Nullable LogstashCallerMarker result;
    if (isAsyncCallerEnabled()) {
      result = new LogstashCallerMarker(fqcn, new Throwable());
    } else {
      result = null;
    }
    Marker callerMarker = result;
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
    @Nullable LogstashCallerMarker result;
    if (isAsyncCallerEnabled()) {
      result = new LogstashCallerMarker(fqcn, new Throwable());
    } else {
      result = null;
    }
    Marker callerMarker = result;
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
    @Nullable LogstashCallerMarker result;
    if (isAsyncCallerEnabled()) {
      result = new LogstashCallerMarker(fqcn, new Throwable());
    } else {
      result = null;
    }
    final Marker callerMarker = result;
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
    @Nullable LogstashCallerMarker result;
    if (isAsyncCallerEnabled()) {
      result = new LogstashCallerMarker(fqcn, new Throwable());
    } else {
      result = null;
    }
    final Marker callerMarker = result;
    Runnable threadLocalRunnable = threadContextFunction.get();
    runAsyncLog(
        () -> {
          threadLocalRunnable.run();
          LogstashCoreLogger callerLogger = newLogger(newContext(extraFields, callerMarker));
          final LoggerHandle<FB> loggerHandle = newHandle(level, c, builder, callerLogger);
          consumer.accept(loggerHandle);
        });
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
  protected LogstashCoreLogger.Context newContext(
      @NotNull Supplier<List<Field>> fieldsSupplier, Marker callerMarker) {
    Supplier<List<Field>> loggerFields =
        Context.joinFields(fieldsSupplier, context::getLoggerFields);
    Supplier<List<Marker>> markers;
    if (callerMarker == null) {
      markers = context::getMarkers;
    } else {
      markers =
          Context.joinMarkers(() -> Collections.singletonList(callerMarker), context::getMarkers);
    }
    return new Context(loggerFields, markers);
  }

  protected Context newContext(Marker callerMarker) {
    if (callerMarker == null) {
      return context;
    } else {
      Supplier<List<Marker>> markers;
      markers =
          Context.joinMarkers(() -> Collections.singletonList(callerMarker), context::getMarkers);
      return new Context(context::getLoggerFields, markers);
    }
  }

  protected LogstashCoreLogger newLogger(Context newContext) {
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

  private Marker resolveLoggerFields(Marker ctxMarker, LoggingContext ctx) {
    final List<Field> fields = ctx.getLoggerFields();
    if (fields.isEmpty()) {
      return ctxMarker;
    } else {
      final List<Marker> markerList = new ArrayList<>(fields.size() + 1);
      for (Field field : fields) {
        LogstashMarker append = Markers.append(field.name(), field.value());
        markerList.add(append);
      }
      if (ctxMarker != null) {
        markerList.add(ctxMarker);
      }
      return Markers.aggregate(markerList);
    }
  }

  private List<Field> convertToFields(FieldBuilderResult result) {
    if (result == null) {
      // XXX log an error
      return Collections.emptyList();
    }
    return result.fields();
  }

  public String toString() {
    return "LogstashCoreLogger[" + logger.getName() + "]";
  }

  /** A logging context that stores fields belonging to the logger. */
  protected static class Context {

    private static final Context EMPTY =
        new Context(Collections::emptyList, Collections::emptyList);

    private final Supplier<List<Field>> fieldsSupplier;
    private final Supplier<List<Marker>> markersSupplier;

    private final Supplier<Marker> markersResult;

    protected Context(Supplier<List<Field>> f, Supplier<List<Marker>> m) {
      this.fieldsSupplier = f;
      this.markersSupplier = m;
      this.markersResult =
          Utilities.memoize(
              () -> {
                List<Marker> markers = getMarkers();
                if (markers.isEmpty()) {
                  return null;
                } else if (markers.size() == 1) {
                  return markers.get(0);
                } else {
                  return Markers.aggregate(markers);
                }
              });
    }

    public static Context create(List<Field> fields) {
      return new Context(() -> fields, Collections::emptyList);
    }

    public static Context create(Field field) {
      return new Context(() -> Collections.singletonList(field), Collections::emptyList);
    }

    public static Context empty() {
      return EMPTY;
    }

    public @NotNull List<Field> getLoggerFields() {
      return fieldsSupplier.get();
    }

    public @NotNull List<Marker> getMarkers() {
      return markersSupplier.get();
    }

    public Context withFields(Supplier<List<Field>> o) {
      // existing context should be concatenated before the new fields
      Supplier<List<Field>> joinedFields = joinFields(this::getLoggerFields, o);
      return new Context(joinedFields, this::getMarkers);
    }

    public Context withMarkers(Supplier<List<Marker>> o) {
      Supplier<List<Marker>> joinedMarkers = joinMarkers(this::getMarkers, o);
      return new Context(this::getLoggerFields, joinedMarkers);
    }

    /**
     * Joins the two contexts together, concatenating the lists in a supplier function.
     *
     * @param context the context to join
     * @return the new context containing fields and markers from both.
     */
    public Context and(Context context) {
      if (context == null) {
        return this;
      }

      // This MUST be lazy, we can't get the fields until statement evaluation
      Supplier<List<Field>> joinedFields =
          joinFields(this::getLoggerFields, context::getLoggerFields);
      Supplier<List<Marker>> joinedMarkers =
          joinMarkers(context::getMarkers, Context.this::getMarkers);
      return new Context(joinedFields, joinedMarkers);
    }

    static Supplier<List<Marker>> joinMarkers(
        Supplier<List<Marker>> markersSupplier, Supplier<List<Marker>> thisMarkersSupplier) {
      return () -> {
        final List<Marker> markers = markersSupplier.get();
        final List<Marker> thisMarkers = thisMarkersSupplier.get();
        if (markers.isEmpty()) {
          return thisMarkers;
        } else if (thisMarkers.isEmpty()) {
          return markers;
        } else {
          return Stream.concat(thisMarkers.stream(), markers.stream()).collect(Collectors.toList());
        }
      };
    }

    static Supplier<List<Field>> joinFields(
        Supplier<List<Field>> first, Supplier<List<Field>> second) {
      return () -> {
        List<Field> firstFields = first.get();
        List<Field> secondFields = second.get();

        if (firstFields.isEmpty()) {
          return secondFields;
        } else if (secondFields.isEmpty()) {
          return firstFields;
        } else {
          // Stream.concat is actually faster than explicit ArrayList!
          // https://blog.soebes.de/blog/2020/03/31/performance-stream-concat/
          return Stream.concat(firstFields.stream(), secondFields.stream())
              .collect(Collectors.toList());
        }
      };
    }

    @Nullable
    Marker resolveMarkers() {
      // Markers are always resolved on isEnabled, but contexts can also be
      // composed with each other, so we don't want every single context's marker,
      // only the final result.
      return markersResult.get();
    }
  }
}
