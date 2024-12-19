package echopraxia.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jetbrains.annotations.NotNull;

/** This converter searches field arguments using a JSON path. */
public class ArgumentFieldConverter extends AbstractPathConverter {

  @NotNull
  @Override
  protected AbstractEventLoggingContext getLoggingContext(ILoggingEvent event) {
    return new ArgumentLoggingContext(null, event);
  }
}
