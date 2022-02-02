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
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A core logger using the Log4J API. */
public class Log4JCoreLogger implements CoreLogger {

  private final ExtendedLoggerWrapper logger;
  private final Log4JLoggingContext context;
  private final Condition condition;
  private final Executor executor;
  private final String fqcn;

  Log4JCoreLogger(@NotNull String fqcn, ExtendedLogger log4jLogger) {
    this.fqcn = fqcn;
    this.logger =
        new ExtendedLoggerWrapper(
            log4jLogger, log4jLogger.getName(), log4jLogger.getMessageFactory());
    this.context = new Log4JLoggingContext();
    this.condition = Condition.always();
    this.executor = ForkJoinPool.commonPool();
  }

  protected Log4JCoreLogger(
      String fqcn,
      ExtendedLogger log4jLogger,
      Log4JLoggingContext context,
      Condition condition,
      Executor executor) {
    this.fqcn = fqcn;
    this.logger =
        new ExtendedLoggerWrapper(
            log4jLogger, log4jLogger.getName(), log4jLogger.getMessageFactory());
    ;
    this.context = context;
    this.condition = condition;
    this.executor = executor;
  }

  public Logger logger() {
    return this.logger;
  }

  @Override
  public @NotNull Condition condition() {
    return this.condition;
  }

  @Override
  public <B extends Field.Builder> @NotNull CoreLogger withFields(
      Field.@NotNull BuilderFunction<B> f, @NotNull B builder) {
    Log4JLoggingContext newContext = new Log4JLoggingContext(() -> f.apply(builder), null);
    return new Log4JCoreLogger(fqcn, logger, context.and(newContext), condition, executor);
  }

  @Override
  public @NotNull CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    Supplier<List<Field>> fieldSupplier = mapTransform.apply(ThreadContext::getImmutableContext);
    Log4JLoggingContext newContext = new Log4JLoggingContext(fieldSupplier, null);
    return new Log4JCoreLogger(fqcn, logger, this.context.and(newContext), condition, executor);
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
      return new Log4JCoreLogger(fqcn, logger, context, condition, executor);
    }
    return new Log4JCoreLogger(fqcn, logger, context, this.condition.and(condition), executor);
  }

  @Override
  public @NotNull CoreLogger withExecutor(@NotNull Executor executor) {
    return new Log4JCoreLogger(fqcn, logger, context, this.condition, executor);
  }

  @NotNull
  public CoreLogger withMarker(@NotNull Marker marker) {
    Log4JLoggingContext newContext = new Log4JLoggingContext(Collections::emptyList, marker);
    return new Log4JCoreLogger(fqcn, logger, this.context.and(newContext), condition, executor);
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    return logger.isEnabled(convertLevel(level), context.getMarker(), (Message) null, null)
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
    return logger.isEnabled(convertLevel(level), context.getMarker(), (Message) null, null)
        && this.condition.and(condition).test(level, context);
  }

  @Override
  public void log(@NotNull Level level, String message) {
    if (!condition.test(level, context)) {
      return;
    }
    logger.logIfEnabled(
        fqcn, convertLevel(level), context.getMarker(), createMessage(message), null);
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
    logger.logIfEnabled(fqcn, convertLevel(level), context.getMarker(), message, e);
  }

  @Override
  public void log(@NotNull Level level, @Nullable String message, @NotNull Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }
    logger.logIfEnabled(
        fqcn,
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
      @Nullable String message,
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
      @Nullable String message,
      @NotNull Field.BuilderFunction<B> f,
      @NotNull B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message, f, builder);
  }

  @Override
  public <FB extends Field.Builder> void asyncLog(
      @NotNull Level level, @NotNull Consumer<LoggerHandle<FB>> consumer, @NotNull FB builder) {
    runAsyncLog(
        consumer,
        new LoggerHandle<FB>() {
          @Override
          public void log(@Nullable String message) {
            Log4JCoreLogger.this.log(level, message);
          }

          @Override
          public void log(@Nullable String message, @NotNull Field.BuilderFunction<FB> f) {
            Log4JCoreLogger.this.log(level, message, f, builder);
          }

          @Override
          public void log(@Nullable String message, @NotNull Throwable e) {
            Log4JCoreLogger.this.log(level, message, e);
          }
        });
  }

  @Override
  public <FB extends Field.Builder> void asyncLog(
      @NotNull Level level,
      @NotNull Condition c,
      @NotNull Consumer<LoggerHandle<FB>> consumer,
      @NotNull FB builder) {
    runAsyncLog(
        consumer,
        new LoggerHandle<FB>() {
          @Override
          public void log(@Nullable String message) {
            Log4JCoreLogger.this.log(level, c, message);
          }

          @Override
          public void log(@Nullable String message, Field.@NotNull BuilderFunction<FB> f) {
            Log4JCoreLogger.this.log(level, c, message, f, builder);
          }

          @Override
          public void log(@Nullable String message, @NotNull Throwable e) {
            Log4JCoreLogger.this.log(level, c, message, e);
          }
        });
  }

  protected Message createMessage(String message) {
    return createMessage(message, Collections.emptyList());
  }

  protected Message createMessage(String template, List<Field> arguments) {
    List<Field> contextFields = context.getFields();
    return new EchopraxiaFieldsMessage(template, arguments, contextFields);
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
      if (value instanceof Value.ExceptionValue) {
        return ((Value.ExceptionValue) value).raw();
      }
    }
    return null;
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
}
