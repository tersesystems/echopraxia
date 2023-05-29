package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.EchopraxiaService;
import com.tersesystems.echopraxia.api.EchopraxiaServiceProvider;

public class JULEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new JULEchopraxiaService();
  }
}
