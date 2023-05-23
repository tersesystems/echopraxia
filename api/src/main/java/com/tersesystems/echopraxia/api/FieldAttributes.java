package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/** These are common field attributes used for rendering. */
public class FieldAttributes {
  public static final AttributeKey<Boolean> VALUE_ONLY = AttributeKey.create("valueOnly");
  public static final AttributeKey<Integer> ABBREVIATE_AFTER =
      AttributeKey.create("abbreviateAfter");

  private static final Attribute<Boolean> VALUE_ONLY_ATTR = VALUE_ONLY.bindValue(true);
  private static final Attributes VALUE_ONLY_ATTRS = Attributes.create(FieldAttributes.valueOnly());

  // package-private static, we only use this in Field.value as a shortcut
  static Attributes valueOnlyAttributes() {
    return VALUE_ONLY_ATTRS;
  }

  @NotNull
  public static Attribute<Boolean> valueOnly() {
    return VALUE_ONLY_ATTR;
  }

  public static Attribute<Integer> abbreviateAfter(Integer after) {
    return ABBREVIATE_AFTER.bindValue(after);
  }
}
