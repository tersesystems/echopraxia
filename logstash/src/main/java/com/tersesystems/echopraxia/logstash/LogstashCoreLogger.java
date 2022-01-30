package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.Field.Value;

import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.core.CoreLogger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.slf4j.Marker;

/** Logstash implementation of CoreLogger. */
public class LogstashCoreLogger implements CoreLogger {

  private final org.slf4j.Logger logger;
  private final LogstashLoggingContext context;
  private final Condition condition;
  private final Executor executor;

  protected LogstashCoreLogger(org.slf4j.Logger logger) {
    this.logger = logger;
    this.context = LogstashLoggingContext.empty();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
  }

  public LogstashCoreLogger(
      org.slf4j.Logger logger,
      LogstashLoggingContext context,
      Condition condition,
      Executor executor) {
    this.logger = logger;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
  }

  /**
   * Returns the underlying SLF4J logger.
   *
   * @return the SLF4J logger.
   */
  public org.slf4j.Logger logger() {
    return logger;
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
  public <B extends Field.Builder> @NotNull CoreLogger withFields(
      Field.@NotNull BuilderFunction<B> f, @NotNull B builder) {
    LogstashLoggingContext newContext =
        new LogstashLoggingContext(() -> f.apply(builder), Collections::emptyList);
    return new LogstashCoreLogger(logger, this.context.and(newContext), condition, executor);
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    Supplier<List<Field>> fieldSupplier = mapTransform.apply(MDC::getCopyOfContextMap);
    LogstashLoggingContext newContext =
        new LogstashLoggingContext(fieldSupplier, Collections::emptyList);
    return new LogstashCoreLogger(logger, this.context.and(newContext), condition, executor);
  }

  @Override
  public @NotNull CoreLogger withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    if (condition == Condition.never()) {
      if (this.condition == Condition.never()) {
        return this;
      }
      return new LogstashCoreLogger(logger, context, condition, executor);
    }
    return new LogstashCoreLogger(logger, context, this.condition.and(condition), executor);
  }

  @Override
  public CoreLogger withExecutor(Executor executor) {
    return new LogstashCoreLogger(logger, context, condition, executor);
  }

  @Override
  public <FB extends Field.Builder> void asyncLog(
      Level level, Consumer<LoggerHandle<FB>> consumer, FB builder) {
    runAsyncLog(
        consumer,
        new LoggerHandle<FB>() {
          @Override
          public void log(String message) {
            LogstashCoreLogger.this.log(level, message);
          }

          @Override
          public void log(String message, Field.BuilderFunction<FB> f) {
            LogstashCoreLogger.this.log(level, message, f, builder);
          }

          @Override
          public void log(String message, Throwable e) {
            LogstashCoreLogger.this.log(level, message, e);
          }
        });
  }

  protected <FB extends Field.Builder> void runAsyncLog(
      Consumer<LoggerHandle<FB>> consumer, LoggerHandle<FB> handle) {
    final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
    Runnable runnable =
        () -> {
          if (copyOfContextMap != null) {
            MDC.setContextMap(copyOfContextMap);
          }
          consumer.accept(handle);
        };

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

  @Override
  public <FB extends Field.Builder> void asyncLog(
      Level level, Condition c, Consumer<LoggerHandle<FB>> consumer, FB builder) {
    runAsyncLog(
        consumer,
        new LoggerHandle<FB>() {
          @Override
          public void log(String message) {
            LogstashCoreLogger.this.log(level, c, message);
          }

          @Override
          public void log(String message, Field.BuilderFunction<FB> f) {
            LogstashCoreLogger.this.log(level, c, message, f, builder);
          }

          @Override
          public void log(String message, Throwable e) {
            LogstashCoreLogger.this.log(level, c, message, e);
          }
        });
  }

  public CoreLogger withMarkers(Marker... markers) {
    LogstashLoggingContext newContext =
        new LogstashLoggingContext(Collections::emptyList, () -> Arrays.asList(markers));
    return new LogstashCoreLogger(logger, this.context.and(newContext), condition, executor);
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    if (condition == Condition.never()) {
      return false;
    }
    Marker marker = convertMarkers(context.getFields(), context.getMarkers());
    switch (level) {
      case ERROR:
        return logger.isErrorEnabled(marker) && condition.test(level, context);
      case WARN:
        return logger.isWarnEnabled(marker) && condition.test(level, context);
      case INFO:
        return logger.isInfoEnabled(marker) && condition.test(level, context);
      case DEBUG:
        return logger.isDebugEnabled(marker) && condition.test(level, context);
      case TRACE:
        return logger.isTraceEnabled(marker) && condition.test(level, context);
    }
    throw new IllegalStateException("No branch found for level " + level);
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    if (condition == Condition.always()) {
      return isEnabled(level);
    }
    if (condition == Condition.never()) {
      return false;
    }
    Marker marker = convertMarkers(context.getFields(), context.getMarkers());
    switch (level) {
      case ERROR:
        return logger.isErrorEnabled(marker) && this.condition.and(condition).test(level, context);
      case WARN:
        return logger.isWarnEnabled(marker) && this.condition.and(condition).test(level, context);
      case INFO:
        return logger.isInfoEnabled(marker) && this.condition.and(condition).test(level, context);
      case DEBUG:
        return logger.isDebugEnabled(marker) && this.condition.and(condition).test(level, context);
      case TRACE:
        return logger.isTraceEnabled(marker) && this.condition.and(condition).test(level, context);
    }
    throw new IllegalStateException("No branch found for level " + level);
  }

  @Override
  public void log(@NotNull Level level, String message) {
    if (!condition.test(level, context)) {
      return;
    }

    Marker m = convertMarkers(context.getFields(), context.getMarkers());
    switch (level) {
      case ERROR:
        logger.error(m, message);
        break;
      case WARN:
        logger.warn(m, message);
        break;
      case INFO:
        logger.info(m, message);
        break;
      case DEBUG:
        logger.debug(m, message);
        break;
      case TRACE:
        logger.trace(m, message);
        break;
    }
  }

  @Override
  public <FB extends Field.Builder> void log(
      @NotNull Level level,
      String message,
      Field.@NotNull BuilderFunction<FB> f,
      @NotNull FB builder) {
    if (!condition.test(level, context)) {
      return;
    }

    final Marker m = convertMarkers(context.getFields(), context.getMarkers());
    switch (level) {
      case ERROR:
        if (logger.isErrorEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args);
          logger.error(m, message, arguments);
        }
        break;
      case WARN:
        if (logger.isWarnEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args);
          logger.warn(m, message, arguments);
        }
        break;
      case INFO:
        if (logger.isInfoEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args);
          logger.info(m, message, arguments);
        }
        break;
      case DEBUG:
        if (logger.isDebugEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args);
          logger.debug(m, message, arguments);
        }
        break;
      case TRACE:
        if (logger.isTraceEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args);
          logger.trace(m, message, arguments);
        }
        break;
    }
  }

  @Override
  public void log(@NotNull Level level, String message, @NotNull Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }

    Marker m = convertMarkers(context.getFields(), context.getMarkers());
    switch (level) {
      case ERROR:
        logger.error(m, message, e);
        break;
      case WARN:
        logger.warn(m, message, e);
        break;
      case INFO:
        logger.info(m, message, e);
        break;
      case DEBUG:
        logger.debug(m, message, e);
        break;
      case TRACE:
        logger.trace(m, message, e);
        break;
    }
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, String message) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message);
  }

  @Override
  public void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull String message,
      @NotNull Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message, e);
  }

  @Override
  public <B extends Field.Builder> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      String message,
      Field.@NotNull BuilderFunction<B> f,
      @NotNull B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message, f, builder);
  }

  // Top level conversion to Logback must be StructuredArgument, with an optional throwable
  // at the end of the array.
  protected Object[] convertArguments(List<Field> args) {
    // Top level arguments must be StructuredArguments, +1 for throwable
    Value<Throwable> throwable = null;
    List<Object> arguments = new ArrayList<>(args.size() + 1);
    for (Field field : args) {
      final Value<?> value = field.value();
      if (value.type() == Value.ValueType.EXCEPTION) {
        throwable = (Value.ExceptionValue) value;
      } else {
        final String name = field.name();
        StructuredArgument array =
            field instanceof ValueField
                ? StructuredArguments.value(name, value)
                : StructuredArguments.keyValue(name, value);
        arguments.add(array);
      }
    }
    // If the exception exists, it must be raw so the varadic case will pick it up.
    if (throwable != null) {
      arguments.add(throwable.raw());
    }
    return arguments.toArray();
  }

  // Convert markers explicitly.
  protected org.slf4j.Marker convertMarkers(List<Field> fields, List<Marker> markers) {
    // XXX there should be a way to cache this if we know it hasn't changed, since it
    // could be calculated repeatedly.
    if (fields.isEmpty() && markers.isEmpty()) {
      return null;
    }

    final List<Marker> markerList =
        fields.stream()
            .map(field -> Markers.append(field.name(), field.value()))
            .collect(Collectors.toList());
    markerList.addAll(markers);
    return Markers.aggregate(markerList);
  }
}
