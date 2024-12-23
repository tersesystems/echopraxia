package echopraxia.simple;

import echopraxia.logging.spi.CoreLoggerFactory;
import org.jetbrains.annotations.NotNull;

public class LoggerFactory {

  private static final @NotNull String FQCN = "echopraxia.simple.Logger";

  public Logger getLogger(String name) {
    var core = CoreLoggerFactory.getLogger(FQCN, name);
    return new Logger(core);
  }

  public Logger getLogger(Class<?> clazz) {
    var core = CoreLoggerFactory.getLogger(FQCN, clazz);
    return new Logger(core);
  }
}
