package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.EchopraxiaService;
import com.tersesystems.echopraxia.api.EchopraxiaServiceProvider;

public class FakeEchopraxiaServiceProvider implements EchopraxiaServiceProvider {

  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new FakeEchopraxiaService();
  }
}
