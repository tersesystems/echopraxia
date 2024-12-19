package echopraxia.jul;

import echopraxia.logging.spi.EchopraxiaService;
import echopraxia.logging.spi.EchopraxiaServiceProvider;

public class TestEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new TestEchopraxiaService();
  }
}
