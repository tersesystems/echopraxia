package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    this.context = new LogstashLoggingContext(Collections.emptyList(), Collections.emptyList());
    this.condition = Condition.always();
  }

  protected LogstashCoreLogger(
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
    List<Field> newFields = f.apply(builder);
    LogstashLoggingContext c = new LogstashLoggingContext(newFields, Collections.emptyList());
    return new LogstashCoreLogger(logger, c.and(this.context), condition);
  }

  @Override
  public CoreLogger withCondition(Condition condition) {
    return new LogstashCoreLogger(logger, context, this.condition.and(condition));
  }

  public CoreLogger withMarkers(Marker... markers) {
    List<Marker> m =
        Stream.of(Arrays.asList(markers), this.context.getMarkers())
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    LogstashLoggingContext newContext = new LogstashLoggingContext(this.context.getFields(), m);
    return new LogstashCoreLogger(logger, newContext, condition);
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
    throw new IllegalStateException("Not found path for for level " + level);
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
    throw new IllegalStateException("Not found path for for level " + level);
  }

  @Override
  public void log(Level level, String message) {
    if (!condition.test(level, context)) {
      return;
    }

    switch (level) {
      case ERROR:
        if (context == null) {
          if (logger.isErrorEnabled()) {
            logger.error(message);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isErrorEnabled(m)) {
            logger.error(m, message);
          }
        }
        break;
      case WARN:
        if (context == null) {
          if (logger.isWarnEnabled()) {
            logger.warn(message);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isWarnEnabled(m)) {
            logger.warn(m, message);
          }
        }
        break;
      case INFO:
        if (context == null) {
          if (logger.isInfoEnabled()) {
            logger.info(message);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isInfoEnabled(m)) {
            logger.info(m, message);
          }
        }
        break;
      case DEBUG:
        if (context == null) {
          if (logger.isDebugEnabled()) {
            logger.debug(message);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isDebugEnabled(m)) {
            logger.debug(m, message);
          }
        }
        break;
      case TRACE:
        if (context == null) {
          if (logger.isTraceEnabled()) {
            logger.trace(message);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isTraceEnabled(m)) {
            logger.trace(m, message);
          }
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

    final List<Field> args = f.apply(builder);
    switch (level) {
      case ERROR:
        if (context == null) {
          if (logger.isErrorEnabled()) {
            final Object[] arguments = convertArguments(args, false);
            logger.error(message, arguments);
          }
        } else {
          final Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isErrorEnabled(m)) {
            final Object[] arguments = convertArguments(args, false);
            logger.error(m, message, arguments);
          }
        }
        break;
      case WARN:
        if (context == null) {
          if (logger.isWarnEnabled()) {
            final Object[] arguments = convertArguments(args, false);
            logger.warn(message, arguments);
          }
        } else {
          final Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isWarnEnabled(m)) {
            final Object[] arguments = convertArguments(args, false);
            logger.warn(m, message, arguments);
          }
        }
        break;
      case INFO:
        if (context == null) {
          if (logger.isInfoEnabled()) {
            final Object[] arguments = convertArguments(args, false);
            logger.info(message, arguments);
          }
        } else {
          final Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isWarnEnabled(m)) {
            final Object[] arguments = convertArguments(args, false);
            logger.info(m, message, arguments);
          }
        }
        break;
      case DEBUG:
        if (context == null) {
          if (logger.isDebugEnabled()) {
            final Object[] arguments = convertArguments(args, false);
            logger.debug(message, arguments);
          }
        } else {
          final Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isDebugEnabled(m)) {
            final Object[] arguments = convertArguments(args, false);
            logger.debug(m, message, arguments);
          }
        }
        break;
      case TRACE:
        if (context == null) {
          if (logger.isTraceEnabled()) {
            final Object[] arguments = convertArguments(args, false);
            logger.trace(message, arguments);
          }
        } else {
          final Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isTraceEnabled(m)) {
            final Object[] arguments = convertArguments(args, false);
            logger.trace(m, message, arguments);
          }
        }
        break;
    }
  }

  @Override
  public void log(Level level, String message, Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }

    switch (level) {
      case ERROR:
        if (context == null) {
          if (logger.isErrorEnabled()) {
            logger.error(message, e);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isErrorEnabled(m)) {
            logger.error(m, message, e);
          }
        }
        break;
      case WARN:
        if (context == null) {
          if (logger.isWarnEnabled()) {
            logger.warn(message, e);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isWarnEnabled(m)) {
            logger.warn(m, message, e);
          }
        }
        break;
      case INFO:
        if (context == null) {
          if (logger.isInfoEnabled()) {
            logger.info(message, e);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isInfoEnabled(m)) {
            logger.info(m, message, e);
          }
        }
        break;
      case DEBUG:
        if (context == null) {
          if (logger.isDebugEnabled()) {
            logger.debug(message, e);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isDebugEnabled(m)) {
            logger.debug(m, message, e);
          }
        }
        break;
      case TRACE:
        if (context == null) {
          if (logger.isTraceEnabled()) {
            logger.trace(message, e);
          }
        } else {
          Marker m = convertMarkers(context.getFields(), context.getMarkers());
          if (logger.isTraceEnabled(m)) {
            logger.trace(m, message, e);
          }
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
      if (value instanceof Field.Value.ArrayValue) {
        final List<Field.Value<?>> values = ((Field.Value.ArrayValue) value).raw();
        Object[] elements = convertValues(values).toArray();
        arguments.add(StructuredArguments.array(name, elements));
      } else if (value instanceof Field.Value.ObjectValue) {
        List<Field> fieldArgs = ((Field.Value.ObjectValue) value).raw();
        final Object[] fields = convertArguments(fieldArgs, true);
        arguments.add(StructuredArguments.kv(name, StructuredArguments.fields(fields)));
      } else if (value instanceof Field.Value.ExceptionValue) {
        throwable = (Field.Value.ExceptionValue) value;
      } else {
        if (verbose) {
          arguments.add(StructuredArguments.keyValue(name, value.raw()));
        } else {
          arguments.add(StructuredArguments.value(name, value.raw()));
        }
      }
    }
    if (throwable != null) {
      arguments.add(throwable.raw());
    }
    return arguments.toArray();
  }

  private List<?> convertValues(List<Field.Value<?>> values) {
    return values.stream().map(Field.Value::raw).collect(Collectors.toList());
  }

  protected org.slf4j.Marker convertMarkers(List<Field> fields, List<Marker> markers) {
    return Markers.appendEntries(convertMarkerFields(fields)).and(Markers.aggregate(markers));
  }

  protected Map<?, ?> convertMarkerFields(List<Field> fields) {
    Map<String, Object> result = new HashMap<>(fields.size());
    for (Field f : fields) {
      final String name = f.name();
      final Field.Value<?> value = f.value();
      Object rawValue = getRawValue(value);
      result.put(name, rawValue);
    }
    return result;
  }

  private Object getRawValue(Field.Value<?> value) {
    if (value instanceof Field.Value.ArrayValue) {
      final List<Field.Value<?>> values = ((Field.Value.ArrayValue) value).raw();
      return convertValues(values);
    } else if (value instanceof Field.Value.ObjectValue) {
      List<Field> fieldArgs = ((Field.Value.ObjectValue) value).raw();
      return convertMarkerFields(fieldArgs);
    } else {
      return value.raw();
    }
  }
}
