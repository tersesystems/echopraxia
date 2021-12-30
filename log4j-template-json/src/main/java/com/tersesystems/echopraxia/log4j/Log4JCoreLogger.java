package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;

/** A core logger using the Log4J API. */
public class Log4JCoreLogger implements CoreLogger {

  private final Logger logger;
  private final Log4JLoggingContext context;
  private final Condition condition;

  protected Log4JCoreLogger(Logger log4jLogger, Log4JLoggingContext context, Condition condition) {
    this.logger = log4jLogger;
    this.context = context;
    this.condition = condition;
  }

  @Override
  public boolean isEnabled(Level level) {
    return logger.isEnabled(convertLevel(level));
  }

  @Override
  public boolean isEnabled(Level level, Condition condition) {
    if (!condition.test(level, context)) {
      return false;
    }
    return logger.isEnabled(convertLevel(level));
  }

  @Override
  public void log(Level level, String message) {
    Marker m = convertContext(context);
    logger.log(convertLevel(level), m, message);
  }

  @Override
  public <B extends Field.Builder> void log(
      Level level, String message, Field.BuilderFunction<B> f, B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    Marker m = convertContext(context);
    logger.log(convertLevel(level), m, createMessage(message, f, builder));
  }

  @Override
  public void log(Level level, String message, Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }
    Marker m = convertContext(context);
    logger.log(convertLevel(level), m, createMessage(message, e));
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
    Log4JLoggingContext newContext =
        new Log4JLoggingContext(() -> f.apply(builder), Collections::emptyList);
    return new Log4JCoreLogger(logger, context.and(newContext), condition);
  }

  @Override
  public CoreLogger withCondition(Condition condition) {
    return new Log4JCoreLogger(logger, context, this.condition.and(condition));
  }

  private <B extends Field.Builder> Message createMessage(
      String template, Field.BuilderFunction<B> f, B builder) {
    // We pull in the fields for arguments
    List<Field> fields = f.apply(builder);

    // we don't look in context fields for an exception, because
    // exception fields shouldn't ever be added into a context.
    // XXX do we make that explicit?  I feel like that's kind of obvious.
    Throwable t = findThrowable(fields);

    // We also need to pull in the context fields as well since
    // Log4J doesn't have a Markers facility like logstash-logback-encoder
    // so we can't send it through a marker
    List<Field> contextFields = context.getFields();

    List<Field> joinedFields = joinLists(fields, contextFields);
    return new JsonObjectMessage(template, convertArguments(joinedFields), t);
  }

  private Message createMessage(String template, Throwable e) {
    List<Field> contextFields = context.getFields();
    return new JsonObjectMessage(template, convertArguments(contextFields), e);
  }

  private Marker convertContext(Log4JLoggingContext context) {
    // https://logging.apache.org/log4j/2.x/manual/markers.html
    //
    // Log4J markers are static, kept in a single map and so there's no such
    // thing as a "detached" marker -- if you add a parent, you're adding it
    // for everyone.  SO... you can't have more than one marker passed in, and
    // you also have a one-off aggregate parent marker.
    //
    // You can't join two markers, and they're not SLF4J markers in any meaningful
    // sense.  There's Log4jMarker and that's IT, because JSON deserialization lists
    // that in the MarkerMixin.
    //
    // From what I can tell here, you'll only ever work with one Log4J marker and
    // will have to manage the parent / child relationships outside of Echopraxia.
    // So we just return the first one and hope that works.
    //
    // Good luck, little marker, and may the odds be ever in your favor.
    return context.getMarkers().stream().findFirst().orElseGet(() -> null);
  }

  private org.apache.logging.log4j.Level convertLevel(Level level) {
    return org.apache.logging.log4j.Level.getLevel(level.name());
  }

  private Throwable findThrowable(List<Field> fields) {
    for (Field field : fields) {
      final Field.Value<?> value = field.value();
      if (value instanceof Field.Value.ExceptionValue) {
        return ((Field.Value.ExceptionValue) value).raw();
      }
    }
    return null;
  }

  private List<Field> joinLists(List<Field> fields, List<Field> contextFields) {
    return Stream.concat(fields.stream(), contextFields.stream()).collect(Collectors.toList());
  }

  protected JsonObject convertArguments(List<Field> args) {
    final JsonObjectBuilder builder = Json.createObjectBuilder();

    for (Field f : args) {
      convertField(builder, f);
    }
    return builder.build();
  }

  private void convertField(JsonObjectBuilder builder, Field f) {
    switch (f.value().type()) {
      case ARRAY:
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        final List<Field.Value<?>> arrayValues = (List<Field.Value<?>>) f.value().raw();
        for (Field.Value<?> value : arrayValues) {
          addValue(value, arrayBuilder);
        }
        builder.add(f.name(), arrayBuilder);
        break;
      case OBJECT:
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        final List<Field> fields = (List<Field>) f.value().raw();
        addObject(fields, objectBuilder);
        builder.add(f.name(), objectBuilder);
        break;
      case STRING:
        builder.add(f.name(), (String) f.value().raw());
        break;
      case NUMBER:
        final Object raw = f.value().raw();
        if (raw instanceof Integer) {
          builder.add(f.name(), (Integer) raw);
        } else if (raw instanceof Long) {
          builder.add(f.name(), (Long) raw);
        } else if (raw instanceof Double) {
          builder.add(f.name(), (Double) raw);
        } else if (raw instanceof BigDecimal) {
          builder.add(f.name(), (BigDecimal) raw);
        } else if (raw instanceof BigInteger) {
          builder.add(f.name(), (BigInteger) raw);
        }
        break;
      case BOOLEAN:
        builder.add(f.name(), (Boolean) f.value().raw());
        break;
      case EXCEPTION:
        // do nothing for right now...
        break;
      case NULL:
        builder.addNull(f.name());
        break;
    }
  }

  private void addValue(Field.Value<?> value, JsonArrayBuilder arrayBuilder) {
    switch (value.type()) {
      case ARRAY:
        JsonArrayBuilder newArrayBuilder = Json.createArrayBuilder();
        addValue(value, newArrayBuilder);
        arrayBuilder.add(newArrayBuilder);
        break;
      case OBJECT:
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        List<Field> valueFields = (List<Field>) value.raw();
        addObject(valueFields, objectBuilder);
        arrayBuilder.add(objectBuilder);
        break;
      case STRING:
        arrayBuilder.add((String) value.raw());
        break;
      case NUMBER:
        final Object raw = value.raw();
        if (raw instanceof Integer) {
          arrayBuilder.add((Integer) raw);
        } else if (raw instanceof Long) {
          arrayBuilder.add((Long) raw);
        } else if (raw instanceof Double) {
          arrayBuilder.add((Double) raw);
        } else if (raw instanceof BigDecimal) {
          arrayBuilder.add((BigDecimal) raw);
        } else if (raw instanceof BigInteger) {
          arrayBuilder.add((BigInteger) raw);
        }
        break;
      case BOOLEAN:
        arrayBuilder.add((Boolean) value.raw());
        break;
      case EXCEPTION:
        // Do nothing for now...
        break;
      case NULL:
        arrayBuilder.add(JsonValue.NULL);
        break;
    }
  }

  private void addObject(List<Field> values, JsonObjectBuilder builder) {
    for (Field f : values) {
      convertField(builder, f);
    }
  }

  protected List<?> convertValues(List<Field.Value<?>> values) {
    return values.stream().map(Field.Value::raw).collect(Collectors.toList());
  }
}
