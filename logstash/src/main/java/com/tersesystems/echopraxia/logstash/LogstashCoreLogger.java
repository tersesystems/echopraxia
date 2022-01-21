package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.Level.INFO;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.ValueField;
import com.tersesystems.echopraxia.core.CoreLogger;
import java.util.*;
import java.util.stream.Collectors;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

/** Logstash implementation of CoreLogger. */
public class LogstashCoreLogger implements CoreLogger {

  private final org.slf4j.Logger logger;
  private final LogstashLoggingContext context;
  private final Condition condition;

  protected LogstashCoreLogger(org.slf4j.Logger logger) {
    this.logger = logger;
    this.context = LogstashLoggingContext.empty();
    this.condition = Condition.always();
  }

  public LogstashCoreLogger(
      org.slf4j.Logger logger, LogstashLoggingContext context, Condition condition) {
    this.logger = logger;
    this.context = context;
    this.condition = condition;
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
  public Condition condition() {
    return this.condition;
  }

  @Override
  public <B extends Field.Builder> CoreLogger withFields(Field.BuilderFunction<B> f, B builder) {
    LogstashLoggingContext newContext =
        new LogstashLoggingContext(() -> f.apply(builder), Collections::emptyList);
    return new LogstashCoreLogger(logger, this.context.and(newContext), condition);
  }

  @Override
  public CoreLogger withCondition(Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    if (condition == Condition.never()) {
      if (this.condition == Condition.never()) {
        return this;
      }
      return new LogstashCoreLogger(logger, context, condition);
    }
    return new LogstashCoreLogger(logger, context, this.condition.and(condition));
  }

  public CoreLogger withMarkers(Marker... markers) {
    LogstashLoggingContext newContext =
        new LogstashLoggingContext(Collections::emptyList, () -> Arrays.asList(markers));
    return new LogstashCoreLogger(logger, this.context.and(newContext), condition);
  }

  @Override
  public boolean isEnabled(Level level) {
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
  public boolean isEnabled(Level level, Condition condition) {
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
  public void log(Level level, String message) {
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
      Level level, String message, Field.BuilderFunction<FB> f, FB builder) {
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
        if (logger.isWarnEnabled(m)) {
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
  public void log(Level level, String message, Throwable e) {
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

  protected Object[] convertArguments(List<Field> args) {
    Field.Value<Throwable> throwable = null;
    List<Object> arguments = new ArrayList<>(args.size() + 1);
    for (Field field : args) {
      final String name = field.name();
      final Field.Value<?> value = field.value();
      switch (value.type()) {
        case ARRAY:
          final List<Field.Value<?>> values = ((Field.Value.ArrayValue) value).raw();
          Object[] elements = convertValues(values).toArray();
          arguments.add(StructuredArguments.array(name, elements));
          break;
        case OBJECT:
          List<Field> fieldArgs = ((Field.Value.ObjectValue) value).raw();
          final Object[] fields = convertArguments(fieldArgs);
          StructuredArgument structuredArgument =
              field instanceof ValueField
                  ? StructuredArguments.value(name, StructuredArguments.fields(fields))
                  : StructuredArguments.keyValue(name, StructuredArguments.fields(fields));
          arguments.add(structuredArgument);
          break;
        case EXCEPTION:
          throwable = (Field.Value.ExceptionValue) value;
          break;
        case NULL:
        case BOOLEAN:
        case NUMBER:
        case STRING:
          StructuredArgument simpleStructuredArgument =
              field instanceof ValueField
                  ? StructuredArguments.value(name, value.raw())
                  : StructuredArguments.keyValue(name, value.raw());
          arguments.add(simpleStructuredArgument);
          break;
      }
    }
    if (throwable != null) {
      arguments.add(throwable.raw());
    }
    return arguments.toArray();
  }

  protected List<?> convertValues(List<Field.Value<?>> values) {
    return values.stream().map(Field.Value::raw).collect(Collectors.toList());
  }

  protected org.slf4j.Marker convertMarkers(List<Field> fields, List<Marker> markers) {
    // XXX there should be a way to cache this if we know it hasn't changed, since it
    // could be calculated repeatedly.
    if (fields.isEmpty() && markers.isEmpty()) {
      return null;
    }
    return Markers.appendEntries(convertMarkerFields(fields)).and(Markers.aggregate(markers));
  }

  protected Map<?, ?> convertMarkerFields(List<Field> fields) {
    if (fields.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Object> result = new HashMap<>(fields.size());
    for (Field f : fields) {
      final String name = f.name();
      final Field.Value<?> value = f.value();
      Object rawValue = getRawValue(value);
      result.put(name, rawValue);
    }
    return result;
  }

  protected Object getRawValue(Field.Value<?> value) {
    switch (value.type()) {
      case ARRAY:
        final List<Field.Value<?>> values = ((Field.Value.ArrayValue) value).raw();
        return convertValues(values);
      case OBJECT:
        List<Field> fieldArgs = ((Field.Value.ObjectValue) value).raw();
        return convertMarkerFields(fieldArgs);
      case STRING:
      case NUMBER:
      case BOOLEAN:
      case EXCEPTION:
      case NULL:
    }
    return value.raw();
  }
}
