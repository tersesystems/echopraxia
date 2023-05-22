package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.ExceptionHandler;
import com.tersesystems.echopraxia.api.ExceptionHandlerProvider;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class StaticExceptionHandlerProvider implements ExceptionHandlerProvider {

  private static final List<Throwable> exceptions = new ArrayList<>();

  private static final ExceptionHandler EXCEPTION_HANDLER = exceptions::add;

  public static Throwable head() {
    return exceptions.get(0);
  }

  public static void clear() {
    exceptions.clear();
  }

  @Override
  public @NotNull ExceptionHandler getExceptionHandler() {
    return EXCEPTION_HANDLER;
  }
}
