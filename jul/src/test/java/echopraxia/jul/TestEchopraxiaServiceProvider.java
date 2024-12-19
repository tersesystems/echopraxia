package echopraxia.jul;

import echopraxia.spi.EchopraxiaService;
import echopraxia.spi.EchopraxiaServiceProvider;

public class TestEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new TestEchopraxiaService();
  }
}
