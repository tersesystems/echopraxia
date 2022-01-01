package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;

public class FluentLoggerFactory {

  public static FluentLogger<Field.Builder> getLogger(Class<?> clazz) {
    return getLogger(clazz, Logger.defaultFieldBuilder());
  }

  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(Class<?> clazz, FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(clazz).core();
    return new FluentLogger<>(coreLogger, builder);
  }

  public static FluentLogger<Field.Builder> getLogger(String name) {
    return getLogger(name, Logger.defaultFieldBuilder());
  }

  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(String name, FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(name).core();
    return new FluentLogger<>(coreLogger, builder);
  }
}
