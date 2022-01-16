package com.tersesystems.echopraxia.log4j.layout;
;
import com.tersesystems.echopraxia.Field;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.apache.logging.log4j.message.Message;

/** Create the simplest possible message for Log4J. */
public class EchopraxiaFieldsMessage implements Message {

  private final BiFunction<String, List<Field>, String> formatter;
  private final String message;
  private final List<Field> argumentFields;
  private final List<Field> contextFields;

  public EchopraxiaFieldsMessage(
      BiFunction<String, List<Field>, String> formatter,
      String message,
      List<Field> argumentFields,
      List<Field> contextFields) {
    this.formatter = formatter;
    this.message = message;
    this.argumentFields = argumentFields;
    this.contextFields = contextFields;
  }

  @Override
  public String getFormattedMessage() {
    return formatter.apply(message, argumentFields);
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
