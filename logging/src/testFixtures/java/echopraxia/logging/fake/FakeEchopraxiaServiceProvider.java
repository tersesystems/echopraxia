package echopraxia.logging.fake;

import echopraxia.logging.spi.EchopraxiaService;
import echopraxia.logging.spi.EchopraxiaServiceProvider;

public class FakeEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new FakeEchopraxiaService();
  }
}
