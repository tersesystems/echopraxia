package com.tersesystems.echopraxia.logstash;

public class TestEchopraxiaService extends LogstashEchopraxiaService {
  public TestEchopraxiaService() {
    super();
    this.exceptionHandler = new StaticExceptionHandler();
  }
}
