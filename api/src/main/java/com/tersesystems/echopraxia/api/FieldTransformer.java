package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

public interface FieldTransformer {

  static FieldTransformer identity() {
    return FieldConverterInstance.IDENTITY;
  }

  @NotNull
  default Field convertArgumentField(@NotNull Field field) {
    return field;
  }

  @NotNull
  default Field convertLoggerField(@NotNull Field field) {
    return field;
  }
}

class FieldConverterInstance {
  static final FieldTransformer IDENTITY = new FieldTransformer() {};
}
