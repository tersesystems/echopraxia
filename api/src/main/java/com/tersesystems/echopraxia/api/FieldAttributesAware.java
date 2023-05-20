package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * This interface wraps some components to facilitate rendering.
 */
interface FieldAttributesAware {

  /**
   * @return the attributes associated with the component.
   */
  @NotNull
  Attributes attributes();

  /**
   * @return true if the VALUE_ONLY attribue is exists and is true, false otherwise.
   */
  @NotNull
  default boolean isValueOnly() {
    return attributes().getOptional(FieldAttributes.VALUE_ONLY).orElse(false);
  }
}
