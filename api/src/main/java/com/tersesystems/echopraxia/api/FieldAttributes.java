package com.tersesystems.echopraxia.api;

public class FieldAttributes {
  public static final AttributeKey<Boolean> VALUE_ONLY = AttributeKey.create("valueOnly");

  private static final Attributes VALUE_ONLY_ATTRS = Attributes.create(VALUE_ONLY.bindValue(true));

  public static Attributes valueOnly() {
    return VALUE_ONLY_ATTRS;
  }
}
