package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * This class transforms a field into another field, potentially using MappedField.
 *
 * @since 3.0
 */
public interface FieldTransformer {

  static FieldTransformer identity() {
    return FieldTransformerInstance.IDENTITY;
  }

  @NotNull
  default Field tranformArgumentField(@NotNull Field field) {
    return field;
  }

  @NotNull
  default Field transformLoggerField(@NotNull Field field) {
    return field;
  }
}

class FieldTransformerInstance {
  static final FieldTransformer IDENTITY = new FieldTransformer() {};
}
