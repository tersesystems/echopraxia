package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.spi.EchopraxiaService;
import com.tersesystems.echopraxia.spi.EchopraxiaServiceProvider;

public class FakeEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new FakeEchopraxiaService();
  }
}
