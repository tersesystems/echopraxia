package com.tersesystems.echopraxia.log4j.layout;

import com.tersesystems.echopraxia.Field;
import java.util.List;
import java.util.stream.Stream;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

/** Create the simplest possible message for Log4J. */
public class EchopraxiaFieldsMessage implements Message {

  private final String message;
  private final List<Field> argumentFields;
  private final List<Field> contextFields;

  public EchopraxiaFieldsMessage(
      String message, List<Field> argumentFields, List<Field> contextFields) {
    this.message = message;
    this.argumentFields = argumentFields;
    this.contextFields = contextFields;
  }

  @Override
  public String getFormattedMessage() {
    // If I'm reading this right, there's no way to format a message through
    // log4j without creating a ParameterizedMessage and then calling
    // getFormattedMessage() on it, because ParameterFormatter is a package
    // private class, and ReusableParameterizedMessage.set methods are also
    // package private.
    final ParameterizedMessage pm = new ParameterizedMessage(getFormat(), getParameters());
    return pm.getFormattedMessage();
  }

  @Override
  public String getFormat() {
    return message;
  }

  @Override
  public Object[] getParameters() {
    return argumentFields.toArray();
  }

  public Field[] getFields() {
    return Stream.concat(argumentFields.stream(), contextFields.stream()).toArray(Field[]::new);
  }

  // It looks like nothing actually uses message.getThrowable() internally
  // apart from maybe LocalizedMessage, find usages returns nothing useful.
  public Throwable getThrowable() {
    return null;
  }
}
