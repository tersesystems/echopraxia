package com.tersesystems.echopraxia.log4j;
;
import com.tersesystems.echopraxia.Field;
import org.apache.logging.log4j.message.Message;

import java.util.function.BiFunction;

/**
 * Create the simplest possible message for Log4J.
 */
public class EchopraxiaFieldsMessage implements Message {

  private final BiFunction<String, Object[], String> formatter;
  private final String message;
  private final Object[] parameters;
  private final Throwable throwable;
  private final Field[] fields;

  public EchopraxiaFieldsMessage(BiFunction<String, Object[], String> formatter, String message, Object[] parameters, Field[] fields, Throwable t) {
    this.formatter = formatter;
    this.message = message;
    this.parameters = parameters;
    this.fields = fields;
    this.throwable = t;
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

  @Override
  public Throwable getThrowable() {
    return throwable;
  }

  public Field[] getFields() {
    return fields;
  }
}
