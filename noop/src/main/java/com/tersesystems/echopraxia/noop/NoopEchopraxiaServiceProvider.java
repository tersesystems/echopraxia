package com.tersesystems.echopraxia.noop;

import com.tersesystems.echopraxia.spi.EchopraxiaService;
import com.tersesystems.echopraxia.spi.EchopraxiaServiceProvider;

public class NoopEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new NoopEchopraxiaService();
  }
}
