package echopraxia.logging.api;

import echopraxia.api.Field;
import echopraxia.logging.spi.CoreLogger;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The logging context interface is exposed to conditions as the way to inspect the available fields
 * for evaluation.
 */
public interface LoggingContext {

  /**
   * A reference back to the core logger. This may be null if the context is constructed out of the
   * usual flow and the core logger is unavailable i.e. in a Logback custom converter.
   *
   * @return core logger if possible, else null.
   */
  @Nullable
  CoreLogger getCore();

  /**
   * @return both context and argument fields, in that order.
   */
  @NotNull
  List<Field> getFields();

  /**
   * @return the fields passed in as arguments to the logger.
   */
  List<Field> getArgumentFields();

  /**
   * @return the list of fields that are part of logger's context.
   */
  List<Field> getLoggerFields();
}
