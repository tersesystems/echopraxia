package com.tersesystems.echopraxia.log4j.layout;
;
import com.tersesystems.echopraxia.Field;
import java.util.function.BiFunction;
import org.apache.logging.log4j.message.Message;

/** Create the simplest possible message for Log4J. */
public class EchopraxiaFieldsMessage implements Message {

  private final BiFunction<String, Object[], String> formatter;
  private final String message;
  private final Object[] parameters;
  private final Field[] fields;

  public EchopraxiaFieldsMessage(
      BiFunction<String, Object[], String> formatter,
      String message,
      Object[] parameters,
      Field[] fields) {
    this.formatter = formatter;
    this.message = message;
    this.parameters = parameters;
    this.fields = fields;
  }

  @Override
  public String getFormattedMessage() {
    return formatter.apply(message, parameters);
  }

  @Override
  public String getFormat() {
    return message;
  }

  @Override
  public Object[] getParameters() {
    return parameters;
  }

  public Field[] getFields() {
    return fields;
  }

  // It looks like nothing actually uses message.getThrowable() internally
  // apart from maybe LocalizedMessage, find usages returns nothing useful.
  public Throwable getThrowable() {
    return null;
  }
}
