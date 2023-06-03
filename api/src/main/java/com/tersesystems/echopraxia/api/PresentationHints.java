package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * These are common field attributes used for presentation hints.
 *
 * @since 3.0
 */
public class PresentationHints {
  public static final AttributeKey<Boolean> VALUE_ONLY = AttributeKey.create("valueOnly");
  public static final AttributeKey<Integer> ABBREVIATE_AFTER =
      AttributeKey.create("abbreviateAfter");

  public static final AttributeKey<Boolean> AS_CARDINAL = AttributeKey.create("asCardinal");
  public static final AttributeKey<String> DISPLAY_NAME = AttributeKey.create("displayName");

  public static final AttributeKey<Boolean> ELIDE = AttributeKey.create("elide");

  private static final Attribute<Boolean> AS_CARDINAL_ATTR = AS_CARDINAL.bindValue(true);

  private static final Attribute<Boolean> VALUE_ONLY_ATTR = VALUE_ONLY.bindValue(true);
  private static final Attribute<Boolean> ELIDE_ATTR = ELIDE.bindValue(true);

  private static final Attributes VALUE_ONLY_ATTRS =
      Attributes.create(PresentationHints.asValueOnly());

  // package-private static, we only use this in Field.value as a shortcut
  static Attributes valueOnlyAttributes() {
    return VALUE_ONLY_ATTRS;
  }

  /**
   * Tells the formatter that the field should be rendered with the value only, i.e. "value" and not
   * "name=value".
   *
   * @return valueOnly attribute
   */
  @NotNull
  public static Attribute<Boolean> asValueOnly() {
    return VALUE_ONLY_ATTR;
  }

  /**
   * Tells the formatter that the array value should be represented as a cardinal number in text.
   *
   * @return asCardinal attribute
   */
  public static Attribute<Boolean> asCardinal() {
    return AS_CARDINAL_ATTR;
  }

  /**
   * Tells the formatter to render a display name in text.
   *
   * @return displayName attribute
   */
  public static Attribute<String> withDisplayName(String displayName) {
    return DISPLAY_NAME.bindValue(displayName);
  }

  /**
   * Tells the formatter that the string value or array value should be abbreviated after the given
   * number of elements.
   *
   * @param after the maximum number of elements to render
   * @return abbreviateAfter attribute
   */
  public static Attribute<Integer> abbreviateAfter(Integer after) {
    return ABBREVIATE_AFTER.bindValue(after);
  }

  /**
   * Tells the formatter to render nothing in text.
   *
   * @return elide attribute
   */
  public static Attribute<Boolean> asElided() {
    return ELIDE_ATTR;
  }
}
