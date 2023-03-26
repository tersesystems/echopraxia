package com.tersesystems.echopraxia.jul;

import static java.lang.Boolean.parseBoolean;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class EchopraxiaLogRecord extends LogRecord {

  // Disable infer source, true by default
  private static final Boolean disableInferSource =
      parseBoolean(
          System.getProperty("com.tersesystems.echopraxia.jul.disableInferSource", "true"));

  private final JULLoggingContext ctx;

  public EchopraxiaLogRecord(Level level, String msg, JULLoggingContext ctx) {
    super(level, msg);
    this.ctx = ctx;

    // JUL is really slow and calls sourceClassName lots when serializing.
    if (disableInferSource) {
      setSourceClassName(null);
      setSourceMethodName(null);
    }

    final List<Field> argumentFields = ctx.getArgumentFields();
    this.setParameters(argumentFields.toArray());
    for (Field f : argumentFields) {
      if (f.value() instanceof Value.ExceptionValue) {
        this.setThrown((Throwable) f.value().raw());
        break;
      }
    }
  }

  public JULLoggingContext getLoggingContext() {
    return this.ctx;
  }
}
