package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.log4j.layout.EchopraxiaFieldsMessage;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;

/** A core logger using the Log4J API. */
public class Log4JCoreLogger implements CoreLogger {

  // A function that fills in message.getFormattedMessage() appropriately.
  private static final BiFunction<String, Object[], String> identity =
      (msg, args) -> {
        // XXX TODO make this work reliably in logfmt
        // https://github.com/logfellow/logstash-logback-encoder/issues/397
        // https://github.com/logfellow/logstash-logback-encoder/issues/632
        return msg;
      };

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
    logger.log(convertLevel(level), createMarker(), message);
  }

  @Override
  public <B extends Field.Builder> void log(
      Level level, String message, Field.BuilderFunction<B> f, B builder) {
    if (!condition.test(level, context)) {
      return;
    }
    List<Field> argumentFields = f.apply(builder);
    Throwable e = findThrowable(argumentFields);
    logger.log(convertLevel(level), createMarker(), createMessage(message, argumentFields), e);
  }

  @Override
  public void log(Level level, String message, Throwable e) {
    if (!condition.test(level, context)) {
      return;
    }
    logger.log(
        convertLevel(level), createMarker(), createMessage(message, Collections.emptyList()), e);
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

  private <B extends Field.Builder> Message createMessage(String template, List<Field> arguments) {
    // XXX should we filter out exception from the fields?
    List<Field> contextFields = context.getFields();
    Field[] joinedFields = joinLists(arguments, contextFields);

    return new EchopraxiaFieldsMessage(identity, template, arguments.toArray(), joinedFields);
  }

  private Marker createMarker() {
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
    // So we just return th of them and hope it works.
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

  private Field[] joinLists(List<Field> fields, List<Field> contextFields) {
    return Stream.concat(fields.stream(), contextFields.stream()).toArray(Field[]::new);
  }
}
