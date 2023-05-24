package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

public class DefaultEchopraxiaService implements EchopraxiaService {

  private ExceptionHandler exceptionHandler =
      new ExceptionHandler() {
        @Override
        public void handleException(Throwable e) {
          e.printStackTrace();
        }
      };

  private ToStringFormatter toStringFormatter = new DefaultToStringFormatter();

  @Override
  public @NotNull ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  @Override
  public ToStringFormatter getToStringFormatter() {
    return toStringFormatter;
  }
}
