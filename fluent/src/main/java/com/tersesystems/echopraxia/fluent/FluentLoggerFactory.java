package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.core.CoreLogger;

/** The factory for FluentLogger. */
public class FluentLoggerFactory {

  public static FluentLogger<Field.Builder> getLogger(Class<?> clazz) {
    return getLogger(clazz, Logger.defaultFieldBuilder());
  }

  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(Class<?> clazz, FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(clazz).core();
    return getLogger(coreLogger, builder);
  }

  public static FluentLogger<Field.Builder> getLogger(String name) {
    return getLogger(name, Logger.defaultFieldBuilder());
  }

  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(String name, FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(name).core();
    return getLogger(coreLogger, builder);
  }

  public static FluentLogger<Field.Builder> getLogger() {
    return getLogger(LoggerFactory.Caller.resolveClassName());
  }

  public static <FB extends Field.Builder> FluentLogger<Field.Builder> getLogger(FB builder) {
    return getLogger(LoggerFactory.Caller.resolveClassName(), builder);
  }

  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(
      CoreLogger coreLogger, FB builder) {
    return new FluentLogger<>(coreLogger, builder);
  }
}
