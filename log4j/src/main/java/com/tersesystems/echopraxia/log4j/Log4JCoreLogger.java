package com.tersesystems.echopraxia.log4j;

import static com.tersesystems.echopraxia.Field.Value;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.log4j.layout.EchopraxiaFieldsMessage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;

/** A core logger using the Log4J API. */
public class Log4JCoreLogger implements CoreLogger {

  private final Logger logger;
  private final Log4JLoggingContext context;
  private final Condition condition;

  Log4JCoreLogger(Logger log4jLogger) {
    this.logger = log4jLogger;
    this.context = new Log4JLoggingContext();
    this.condition = Condition.always();
  }

  protected Log4JCoreLogger(Logger log4jLogger, Log4JLoggingContext context, Condition condition) {
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
  }

  @Override
  public boolean isEnabled(Level level) {
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
  public boolean isEnabled(Level level, Condition condition) {
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
  public void log(Level level, String message) {
    if (!condition.test(level, context)) {
      return;
    }
    logger.log(convertLevel(level), context.getMarker(), createMessage(message));
  }

  @Override
  public <B extends Field.Builder> void log(
      Level level, String message, Field.BuilderFunction<B> f, B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    List<Field> argumentFields = f.apply(builder);
    Throwable e = findThrowable(argumentFields);
    logger.log(convertLevel(level), context.getMarker(), createMessage(message, argumentFields), e);
  }

  @Override
  public void log(Level level, String message, Throwable e) {
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
  public void log(Level level, Condition condition, String message) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message);
  }

  @Override
  public void log(Level level, Condition condition, String message, Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message, e);
  }

  @Override
  public <B extends Field.Builder> void log(
      Level level, Condition condition, String message, Field.BuilderFunction<B> f, B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    log(level, message, f, builder);
  }

  @Override
  public Condition condition() {
    return this.condition;
  }

  @Override
  public <B extends Field.Builder> CoreLogger withFields(Field.BuilderFunction<B> f, B builder) {
    Log4JLoggingContext newContext = new Log4JLoggingContext(() -> f.apply(builder), null);
    return new Log4JCoreLogger(logger, context.and(newContext), condition);
  }

  @Override
  public CoreLogger withThreadContext(
      Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    Supplier<List<Field>> fieldSupplier = mapTransform.apply(ThreadContext::getImmutableContext);
    Log4JLoggingContext newContext = new Log4JLoggingContext(fieldSupplier, null);
    return new Log4JCoreLogger(logger, this.context.and(newContext), condition);
  }

  @Override
  public CoreLogger withCondition(Condition condition) {
    if (condition == Condition.never()) {
      if (this.condition == Condition.never()) {
        return this;
      }
      return new Log4JCoreLogger(logger, context, condition);
    }
    if (condition == Condition.always()) {
      return this;
    }
    return new Log4JCoreLogger(logger, context, this.condition.and(condition));
  }

  public CoreLogger withMarker(Marker marker) {
    Log4JLoggingContext newContext = new Log4JLoggingContext(Collections::emptyList, marker);
    return new Log4JCoreLogger(logger, this.context.and(newContext), condition);
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
