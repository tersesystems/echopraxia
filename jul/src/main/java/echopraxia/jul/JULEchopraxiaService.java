package echopraxia.jul;

import echopraxia.logging.spi.AbstractEchopraxiaService;
import echopraxia.logging.spi.CoreLogger;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class JULEchopraxiaService extends AbstractEchopraxiaService {

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getCoreLogger(fqcn, clazz.getName());
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    Logger logger = Logger.getLogger(name);
    return new JULCoreLogger(fqcn, logger);
  }
}
