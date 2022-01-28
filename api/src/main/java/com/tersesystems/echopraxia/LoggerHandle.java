package com.tersesystems.echopraxia;

public interface LoggerHandle<FB extends Field.Builder> {

  void log(String message);

  void log(String message, Field.BuilderFunction<FB> f);

  void log(String message, Exception e);
}
