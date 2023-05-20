package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * These are common field attributes used for rendering.
 */
public class FieldAttributes {
  public static final AttributeKey<Boolean> VALUE_ONLY = AttributeKey.create("valueOnly");

  private static final Attributes VALUE_ONLY_ATTRS = Attributes.create(VALUE_ONLY.bindValue(true));

  @NotNull
  public static Attributes valueOnly() {
    return VALUE_ONLY_ATTRS;
  }
}
