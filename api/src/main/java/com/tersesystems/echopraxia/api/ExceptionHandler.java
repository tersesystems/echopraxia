package com.tersesystems.echopraxia.api;

/** This interface is used to handle exceptions thrown from inside functions. */
public interface ExceptionHandler {

  /** */
  void handleException(Throwable e);
}
