package com.tersesystems.echopraxia.log4j.layout;

import com.tersesystems.echopraxia.api.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

/** Create the simplest possible message for Log4J. */
public class EchopraxiaFieldsMessage implements Message {

  private final String message;
  private final List<Field> argumentFields;
  private final List<Field> loggerFields;
  private final String formattedMessage;

  public EchopraxiaFieldsMessage(
      String message, List<Field> loggerFields, List<Field> argumentFields) {
    this.message = message;
    this.argumentFields = argumentFields;
    this.loggerFields = loggerFields;
    this.formattedMessage = ParameterizedMessage.format(getFormat(), getParameters());
  }

  @Override
  public String getFormattedMessage() {
    return formattedMessage;
  }

  @Override
  public String getFormat() {
    return message;
  }

  @Override
  public Object[] getParameters() {
    return argumentFields.toArray();
  }

  public List<Field> getArgumentFields() {
    return argumentFields;
  }

  public List<Field> getLoggerFields() {
    return loggerFields;
  }

  public List<Field> getFields() {
    return Stream.concat(argumentFields.stream(), loggerFields.stream())
        .collect(Collectors.toList());
  }

  // It looks like nothing actually uses message.getThrowable() internally
  // apart from maybe LocalizedMessage, find usages returns nothing useful.
  public Throwable getThrowable() {
    return null;
  }
}
