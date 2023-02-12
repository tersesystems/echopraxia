package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jetbrains.annotations.NotNull;

/** This converter renders the fields in the logger context based off JSON path. */
public class LoggerFieldConverter extends AbstractPathConverter {
  @Override
  protected @NotNull AbstractEventLoggingContext getLoggingContext(ILoggingEvent event) {
    return new MarkerLoggingContext(event);
  }
}
