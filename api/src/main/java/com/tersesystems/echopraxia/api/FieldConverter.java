package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

public interface FieldConverter {

  @NotNull
  Object convertArgumentField(@NotNull Field field);

  @NotNull
  Object convertLoggerField(@NotNull Field field);
}
