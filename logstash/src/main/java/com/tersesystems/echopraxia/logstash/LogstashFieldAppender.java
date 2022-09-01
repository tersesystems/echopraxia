package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.logback.TransformingAppender;

/**
 * An appender that converts the logging event from containing Field into containing
 * StructuredArgument so that it can be rendered as JSON.
 *
 * <p>This should only really be used if you are working with SLF4J directly, as it's a hack.
 */
public class LogstashFieldAppender extends TransformingAppender<ILoggingEvent>
    implements LogstashFieldConverter {
  @Override
  protected ILoggingEvent decorateEvent(ILoggingEvent eventObject) {
    // what comes in is fields, what comes out is structured arguments
    final Object[] argumentArray = eventObject.getArgumentArray();
    for (int i = 0; i < argumentArray.length; i++) {
      final Object arg = argumentArray[i];
      if (arg instanceof Field) {
        Field field = (Field) arg;
        argumentArray[i] = convertArgument(field);

        // swap out the throwable if one is found
        if (eventObject.getThrowableProxy() == null) {
          final Throwable throwable = extractThrowable(field);
          if (throwable != null) {
            ((LoggingEvent) eventObject).setThrowableProxy(new ThrowableProxy(throwable));
          }
        }
      }
    }

    return eventObject;
  }

  protected Throwable extractThrowable(Field field) {
    Value<?> value = field.value();
    if (value.type() == Value.Type.EXCEPTION) {
      Value.ExceptionValue throwable = (Value.ExceptionValue) value;
      return throwable.raw();
    } else {
      return null;
    }
  }
}
