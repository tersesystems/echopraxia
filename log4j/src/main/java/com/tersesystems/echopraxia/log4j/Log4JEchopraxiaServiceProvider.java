package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.EchopraxiaService;
import com.tersesystems.echopraxia.api.EchopraxiaServiceProvider;

public class Log4JEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new Log4JEchopraxiaService();
  }
}
