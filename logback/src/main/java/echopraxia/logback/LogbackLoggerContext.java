package echopraxia.logback;

import echopraxia.logging.spi.LoggerContext;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/** The logback context associated with the logger across multiple logging events. */
public interface LogbackLoggerContext extends LoggerContext {

  @NotNull
  List<Marker> getMarkers();
}
