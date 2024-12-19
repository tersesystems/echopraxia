package echopraxia.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This converter renders the fields in the logger context based off JSON path.
 *
 * <p>Note that the core logger is null as this context was created outside the usual flow.
 */
public class LoggerFieldConverter extends AbstractPathConverter {
  @Override
  protected @NotNull AbstractEventLoggingContext getLoggingContext(ILoggingEvent event) {
    return new MarkerLoggingContext(null, event);
  }
}
