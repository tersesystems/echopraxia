package echopraxia.log4j;

import echopraxia.logging.spi.EchopraxiaService;
import echopraxia.logging.spi.EchopraxiaServiceProvider;

public class Log4JEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new Log4JEchopraxiaService();
  }
}
