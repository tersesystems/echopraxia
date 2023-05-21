package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/** This is a service provider interface for apps that want a custom exception handler. */
public interface ExceptionHandlerProvider {

  @NotNull
  ExceptionHandler getExceptionHandler();
}
