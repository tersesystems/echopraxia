package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class renders a fields in the logging event based off the JSON path.
 *
 * <p>If the same path matches arguments and logger context, the arguments take precedence.
 */
public class FieldConverter extends AbstractPathConverter {

  @Override
  protected @NotNull AbstractEventLoggingContext getLoggingContext(ILoggingEvent event) {
    return new FieldLoggingContext(event);
  }
}
