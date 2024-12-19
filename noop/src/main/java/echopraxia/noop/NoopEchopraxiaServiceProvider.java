package echopraxia.noop;

import echopraxia.spi.EchopraxiaService;
import echopraxia.spi.EchopraxiaServiceProvider;

public class NoopEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new NoopEchopraxiaService();
  }
}
