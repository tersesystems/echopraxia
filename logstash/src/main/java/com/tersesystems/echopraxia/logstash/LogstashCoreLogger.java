package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.core.CoreLogger;
import java.util.*;
import java.util.stream.Collectors;
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
    // If this is Condition.never we could optimize this by returning a No-op logger
    // likewise a Condition.always means nothing (it's an AND true)
    return new LogstashCoreLogger(logger, context, this.condition.and(condition));
  }

  public CoreLogger withMarkers(Marker... markers) {
    LogstashLoggingContext newContext =
        new LogstashLoggingContext(Collections::emptyList, () -> Arrays.asList(markers));
    return new LogstashCoreLogger(logger, this.context.and(newContext), condition);
  }

  @Override
  public boolean isEnabled(Level level) {
    switch (level) {
      case ERROR:
        return logger.isErrorEnabled() && condition.test(level, context);
      case WARN:
        return logger.isWarnEnabled() && condition.test(level, context);
      case INFO:
        return logger.isInfoEnabled() && condition.test(level, context);
      case DEBUG:
        return logger.isDebugEnabled() && condition.test(level, context);
      case TRACE:
        return logger.isTraceEnabled() && condition.test(level, context);
    }
    throw new IllegalStateException("No branch found for level " + level);
  }

  @Override
  public boolean isEnabled(Level level, Condition condition) {
    switch (level) {
      case ERROR:
        return logger.isErrorEnabled() && this.condition.and(condition).test(level, context);
      case WARN:
        return logger.isWarnEnabled() && this.condition.and(condition).test(level, context);
      case INFO:
        return logger.isInfoEnabled() && this.condition.and(condition).test(level, context);
      case DEBUG:
        return logger.isDebugEnabled() && this.condition.and(condition).test(level, context);
      case TRACE:
        return logger.isTraceEnabled() && this.condition.and(condition).test(level, context);
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
        if (logger.isErrorEnabled(m)) {
          logger.error(m, message);
        }
        break;
      case WARN:
        if (logger.isWarnEnabled(m)) {
          logger.warn(m, message);
        }
        break;
      case INFO:
        if (logger.isInfoEnabled(m)) {
          logger.info(m, message);
        }
        break;
      case DEBUG:
        if (logger.isDebugEnabled(m)) {
          logger.debug(m, message);
        }
        break;
      case TRACE:
        if (logger.isTraceEnabled(m)) {
          logger.trace(m, message);
        }
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
          final Object[] arguments = convertArguments(args, false);
          logger.error(m, message, arguments);
        }
        break;
      case WARN:
        if (logger.isWarnEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args, false);
          logger.warn(m, message, arguments);
        }
        break;
      case INFO:
        if (logger.isWarnEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args, false);
          logger.info(m, message, arguments);
        }
        break;
      case DEBUG:
        if (logger.isDebugEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args, false);
          logger.debug(m, message, arguments);
        }
        break;
      case TRACE:
        if (logger.isTraceEnabled(m)) {
          final List<Field> args = f.apply(builder);
          final Object[] arguments = convertArguments(args, false);
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
        if (logger.isErrorEnabled(m)) {
          logger.error(m, message, e);
        }
        break;
      case WARN:
        if (logger.isWarnEnabled(m)) {
          logger.warn(m, message, e);
        }
        break;
      case INFO:
        if (logger.isInfoEnabled(m)) {
          logger.info(m, message, e);
        }
        break;
      case DEBUG:
        if (logger.isDebugEnabled(m)) {
          logger.debug(m, message, e);
        }
        break;
      case TRACE:
        if (logger.isTraceEnabled(m)) {
          logger.trace(m, message, e);
        }
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

  protected Object[] convertArguments(List<Field> args, boolean verbose) {
    Field.Value<Throwable> throwable = null;
    List<Object> arguments = new ArrayList<>(args.size() + 1);
    for (Field a : args) {
      final String name = a.name();
      final Field.Value<?> value = a.value();
      switch (value.type()) {
        case ARRAY:
          final List<Field.Value<?>> values = ((Field.Value.ArrayValue) value).raw();
          Object[] elements = convertValues(values).toArray();
          arguments.add(StructuredArguments.array(name, elements));
          break;
        case OBJECT:
          List<Field> fieldArgs = ((Field.Value.ObjectValue) value).raw();
          final Object[] fields = convertArguments(fieldArgs, true);
          arguments.add(StructuredArguments.kv(name, StructuredArguments.fields(fields)));
          break;
        case EXCEPTION:
          throwable = (Field.Value.ExceptionValue) value;
          break;
        case NULL:
        case BOOLEAN:
        case NUMBER:
        case STRING:
          if (verbose) {
            arguments.add(StructuredArguments.keyValue(name, value.raw()));
          } else {
            arguments.add(StructuredArguments.value(name, value.raw()));
          }
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
