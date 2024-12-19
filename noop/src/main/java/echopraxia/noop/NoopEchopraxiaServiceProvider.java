package echopraxia.noop;

import echopraxia.logging.spi.EchopraxiaService;
import echopraxia.logging.spi.EchopraxiaServiceProvider;

public class NoopEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new NoopEchopraxiaService();
  }
}
