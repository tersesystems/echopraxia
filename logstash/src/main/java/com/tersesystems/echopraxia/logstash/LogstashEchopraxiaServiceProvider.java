package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.EchopraxiaService;
import com.tersesystems.echopraxia.api.EchopraxiaServiceProvider;

public class LogstashEchopraxiaServiceProvider implements EchopraxiaServiceProvider {
  @Override
  public EchopraxiaService getEchopraxiaService() {
    return new LogstashEchopraxiaService();
  }
}
