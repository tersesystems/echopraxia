package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldConverter;
import org.jetbrains.annotations.NotNull;

public class LogstashFieldConverter implements FieldConverter {

  private static final LogstashFieldConverter SINGLETON = new LogstashFieldConverter();

  public static FieldConverter singleton() {
    return SINGLETON;
  }

  public Object convertField(@NotNull Field field) {
    return new FieldMarker(field);
  }

  @Override
  public @NotNull Object convertArgumentField(@NotNull Field field) {
    return convertField(field);
  }

  @Override
  public @NotNull Object convertLoggerField(@NotNull Field field) {
    return convertField(field);
  }
}
