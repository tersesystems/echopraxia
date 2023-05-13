package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

interface FieldAttributesAware {

  @NotNull
  Attributes attributes();

  @NotNull
  default Boolean isValueOnly() {
    return attributes().getOptional(FieldAttributes.VALUE_ONLY).orElse(false);
  }
}
