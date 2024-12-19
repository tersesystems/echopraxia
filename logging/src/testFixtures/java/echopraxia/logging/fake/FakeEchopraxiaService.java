package echopraxia.logging.fake;

import echopraxia.logging.spi.AbstractEchopraxiaService;
import echopraxia.logging.spi.CoreLogger;
import org.jetbrains.annotations.NotNull;

public class FakeEchopraxiaService extends AbstractEchopraxiaService {

  public FakeEchopraxiaService() {
    super();
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return new FakeCoreLogger(fqcn);
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    return new FakeCoreLogger(fqcn);
  }
}
