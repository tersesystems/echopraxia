package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;

public class FluentLoggerFactory {

  public static FluentLogger<Field.Builder> getLogger(Class<?> clazz) {
    CoreLogger coreLogger = LoggerFactory.getLogger(clazz).core();
    return new FluentLogger<>(coreLogger, Logger.defaultFieldBuilder());
  }

  public static FluentLogger<Field.Builder> getLogger(String name) {
    CoreLogger coreLogger = LoggerFactory.getLogger(name).core();
    return new FluentLogger<>(coreLogger, Logger.defaultFieldBuilder());
  }
}
