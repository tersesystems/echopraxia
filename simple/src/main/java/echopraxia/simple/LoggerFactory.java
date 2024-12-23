package echopraxia.simple;

import echopraxia.logging.spi.Caller;
import echopraxia.logging.spi.CoreLogger;
import echopraxia.logging.spi.CoreLoggerFactory;
import org.jetbrains.annotations.NotNull;

public class LoggerFactory {

  private static final @NotNull String FQCN = "echopraxia.simple.Logger";

  public static @NotNull Logger getLogger(String name) {
    var core = CoreLoggerFactory.getLogger(FQCN, name);
    return new Logger(core);
  }

  public static @NotNull Logger getLogger(Class<?> clazz) {
    var core = CoreLoggerFactory.getLogger(FQCN, clazz);
    return new Logger(core);
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  @NotNull
  public static Logger getLogger() {
    CoreLogger core = CoreLoggerFactory.getLogger(FQCN, Caller.resolveClassName());
    return new Logger(core);
  }

}
