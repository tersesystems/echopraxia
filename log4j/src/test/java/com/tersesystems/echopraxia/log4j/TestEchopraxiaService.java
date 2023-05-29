package com.tersesystems.echopraxia.log4j;

public class TestEchopraxiaService extends Log4JEchopraxiaService {

  public TestEchopraxiaService() {
    super();
    this.exceptionHandler = new StaticExceptionHandler();
  }
}
