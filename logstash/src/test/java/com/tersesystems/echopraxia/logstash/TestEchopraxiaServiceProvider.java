package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.spi.EchopraxiaService;
import com.tersesystems.echopraxia.spi.EchopraxiaServiceProvider;

public class TestEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new TestEchopraxiaService();
  }
}
