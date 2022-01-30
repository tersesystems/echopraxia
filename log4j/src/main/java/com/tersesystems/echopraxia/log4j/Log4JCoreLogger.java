package com.tersesystems.echopraxia.log4j;

import static com.tersesystems.echopraxia.Field.Value;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggerHandle;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.log4j.layout.EchopraxiaFieldsMessage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A core logger using the Log4J API. */
public class Log4JCoreLogger implements CoreLogger {

  private final Logger logger;
  private final Log4JLoggingContext context;
  private final Condition condition;
  private final Executor executor;

  Log4JCoreLogger(Logger log4jLogger) {
    this.logger = log4jLogger;
    this.context = new Log4JLoggingContext();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
  }

  protected Log4JCoreLogger(
      Logger log4jLogger, Log4JLoggingContext context, Condition condition, Executor executor) {
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    if (this.condition == Condition.never()) {
      return false;
    }
    Marker marker = context.getMarker();
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
    Marker marker = context.getMarker();
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
    logger.log(convertLevel(level), context.getMarker(), createMessage(message));
  }

  @Override
  public <B extends Field.Builder> void log(
      @NotNull Level level,
      @Nullable String messageTemplate,
      @NotNull Field.BuilderFunction<B> f,
      @NotNull B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    final List<Field> argumentFields = f.apply(builder);
    final Throwable e = findThrowable(argumentFields);
    final Message message = createMessage(messageTemplate, argumentFields);
    logger.log(convertLevel(level), context.getMarker(), message, e);
  }

  @Override
  public void log(@NotNull Level level, String message, @NotNull Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }
    logger.log(
        convertLevel(level),
        context.getMarker(),
        createMessage(message, Collections.emptyList()),
        e);
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {
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

  @Override
  public <FB extends Field.Builder> void asyncLog(
      Level level, Consumer<LoggerHandle<FB>> consumer, FB builder) {
    runAsyncLog(
        consumer,
        new LoggerHandle<FB>() {
          @Override
          public void log(String message) {
            Log4JCoreLogger.this.log(level, message);
          }

          @Override
          public void log(String message, Field.BuilderFunction<FB> f) {
            Log4JCoreLogger.this.log(level, message, f, builder);
          }

          @Override
          public void log(String message, Throwable e) {
            Log4JCoreLogger.this.log(level, message, e);
          }
        });
  }

  protected <FB extends Field.Builder> void runAsyncLog(
      Consumer<LoggerHandle<FB>> consumer, LoggerHandle<FB> handle) {
    final Map<String, String> copyOfContextMap = ThreadContext.getImmutableContext();
    final ThreadContext.ContextStack contextStack = ThreadContext.getImmutableStack();
    Runnable runnable =
        () -> {
          ThreadContext.clearAll();
          if (copyOfContextMap != null) {
            ThreadContext.putAll(copyOfContextMap);
          }
          if (contextStack != null) {
            ThreadContext.setStack(contextStack);
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
              // Fallback to the underlying logger to render it.
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
            Log4JCoreLogger.this.log(level, c, message);
          }

          @Override
          public void log(String message, Field.BuilderFunction<FB> f) {
            Log4JCoreLogger.this.log(level, c, message, f, builder);
          }

          @Override
          public void log(String message, Throwable e) {
            Log4JCoreLogger.this.log(level, c, message, e);
          }
        });
  }

  @Override
  public @NotNull Condition condition() {
    return this.condition;
  }

  @Override
  public <B extends Field.Builder> @NotNull CoreLogger withFields(
      Field.@NotNull BuilderFunction<B> f, @NotNull B builder) {
    Log4JLoggingContext newContext = new Log4JLoggingContext(() -> f.apply(builder), null);
    return new Log4JCoreLogger(logger, context.and(newContext), condition, executor);
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    Supplier<List<Field>> fieldSupplier = mapTransform.apply(ThreadContext::getImmutableContext);
    Log4JLoggingContext newContext = new Log4JLoggingContext(fieldSupplier, null);
    return new Log4JCoreLogger(logger, this.context.and(newContext), condition, executor);
  }

  @Override
  public @NotNull CoreLogger withCondition(@NotNull Condition condition) {
    if (condition == Condition.never()) {
      if (this.condition == Condition.never()) {
        return this;
      }
      return new Log4JCoreLogger(logger, context, condition, executor);
    }
    if (condition == Condition.always()) {
      return this;
    }
    return new Log4JCoreLogger(logger, context, this.condition.and(condition), executor);
  }

  @Override
  public CoreLogger withExecutor(Executor executor) {
    return new Log4JCoreLogger(logger, context, this.condition, executor);
  }

  public CoreLogger withMarker(Marker marker) {
    Log4JLoggingContext newContext = new Log4JLoggingContext(Collections::emptyList, marker);
    return new Log4JCoreLogger(logger, this.context.and(newContext), condition, executor);
  }

  private Message createMessage(String message) {
    return createMessage(message, Collections.emptyList());
  }

  private <B extends Field.Builder> Message createMessage(String template, List<Field> arguments) {
    List<Field> contextFields = context.getFields();
    return new EchopraxiaFieldsMessage(template, arguments, contextFields);
  }

  private org.apache.logging.log4j.Level convertLevel(Level level) {
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

  private Throwable findThrowable(List<Field> fields) {
    for (Field field : fields) {
      final Value<?> value = field.value();
      if (value instanceof Value.ExceptionValue) {
        return ((Value.ExceptionValue) value).raw();
      }
    }
    return null;
  }

  public Logger logger() {
    return this.logger;
  }
}
