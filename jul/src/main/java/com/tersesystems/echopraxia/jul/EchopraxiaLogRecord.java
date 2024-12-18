package com.tersesystems.echopraxia.jul;

import static java.lang.Boolean.parseBoolean;

import com.tersesystems.echopraxia.api.Field;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class EchopraxiaLogRecord extends LogRecord {

  // Disable infer source, true by default
  private static final Boolean disableInferSource =
      parseBoolean(
          System.getProperty("com.tersesystems.echopraxia.jul.disableInferSource", "true"));

  private Field[] loggerFields;

  public EchopraxiaLogRecord(
      String name,
      Level level,
      String msg,
      Field[] parameters,
      Field[] loggerFields,
      Throwable thrown) {
    super(level, msg);
    this.setLoggerName(name);

    // JUL is really slow and calls sourceClassName lots when serializing.
    if (disableInferSource) {
      setSourceClassName(null);
      setSourceMethodName(null);
    }

    this.setParameters(parameters);
    this.setLoggerFields(loggerFields);
    this.setThrown(thrown);
  }

  public void setLoggerFields(Field[] loggerFields) {
    this.loggerFields = loggerFields;
  }

  public Field[] getLoggerFields() {
    return loggerFields;
  }
}
