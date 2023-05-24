package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.ExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class StaticExceptionHandlerProvider {

  private static final List<Throwable> exceptions = new ArrayList<>();

  private static final ExceptionHandler EXCEPTION_HANDLER = exceptions::add;

  public static Throwable head() {
    return exceptions.get(0);
  }

  public static void clear() {
    exceptions.clear();
  }

  public @NotNull ExceptionHandler getExceptionHandler() {
    return EXCEPTION_HANDLER;
  }
}
