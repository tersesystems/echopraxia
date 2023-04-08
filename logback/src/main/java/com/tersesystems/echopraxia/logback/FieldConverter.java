package com.tersesystems.echopraxia.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.echopraxia.logback.AbstractEventLoggingContext;
import com.tersesystems.echopraxia.logback.AbstractPathConverter;
import org.jetbrains.annotations.NotNull;

/**
 * This class renders a fields in the logging event based off the JSON path.
 *
 * <p>Note that the logging context has a null core logger as it is created outside the usual flow.
 *
 * <p>If the same path matches arguments and logger context, the arguments take precedence.
 */
public class FieldConverter extends AbstractPathConverter {

  @Override
  protected @NotNull AbstractEventLoggingContext getLoggingContext(ILoggingEvent event) {
    return new FieldLoggingContext(null, event);
  }
}
