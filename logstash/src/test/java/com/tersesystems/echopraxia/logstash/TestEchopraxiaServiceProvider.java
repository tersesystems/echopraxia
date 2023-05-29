package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.EchopraxiaService;
import com.tersesystems.echopraxia.api.EchopraxiaServiceProvider;

public class TestEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new TestEchopraxiaService();
  }
}
