package echopraxia.fake;

import echopraxia.spi.EchopraxiaService;
import echopraxia.spi.EchopraxiaServiceProvider;

public class FakeEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new FakeEchopraxiaService();
  }
}
