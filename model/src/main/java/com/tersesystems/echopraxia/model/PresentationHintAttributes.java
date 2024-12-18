package com.tersesystems.echopraxia.model;

import org.jetbrains.annotations.NotNull;

/**
 * These are common field attributes used for presentation hints.
 *
 * @since 3.0
 */
public class PresentationHintAttributes {

  // Field attributes
  public static final AttributeKey<Boolean> VALUE_ONLY = AttributeKey.create("valueOnly");
  public static final AttributeKey<String> DISPLAY_NAME = AttributeKey.create("displayName");
  public static final AttributeKey<Boolean> ELIDE = AttributeKey.create("elide");

  /** AbbreviateAfter will abbreviate a string or array after the given number of elements. */
  public static final AttributeKey<Integer> ABBREVIATE_AFTER =
      AttributeKey.create("abbreviateAfter");

  /**
   * AsCardinal will render the cardinal value of a string or array value, i.e. string length or
   * array size.
   */
  public static final AttributeKey<Boolean> AS_CARDINAL = AttributeKey.create("asCardinal");

  public static final AttributeKey<FieldVisitor> STRUCTURED_FORMAT =
      AttributeKey.create("structuredFormat");

  public static final AttributeKey<FieldVisitor> TOSTRING_FORMAT =
      AttributeKey.create("toStringFormat");

  public static final AttributeKey<String> TOSTRING_VALUE = AttributeKey.create("toStringValue");

  private static final Attribute<Boolean> AS_CARDINAL_ATTR = AS_CARDINAL.bindValue(true);

  private static final Attribute<Boolean> VALUE_ONLY_ATTR = VALUE_ONLY.bindValue(true);
  private static final Attribute<Boolean> ELIDE_ATTR = ELIDE.bindValue(true);

  private static final Attributes VALUE_ONLY_ATTRS =
      Attributes.create(PresentationHintAttributes.asValueOnly());

  // package-private static, we only use this in Field.value as a shortcut
  public static Attributes valueOnlyAttributes() {
    return VALUE_ONLY_ATTRS;
  }

  /**
   * Tells the text formatter that the field should be rendered with the value only, i.e. "value"
   * and not "name=value".
   *
   * @return valueOnly attribute
   */
  @NotNull
  public static Attribute<Boolean> asValueOnly() {
    return VALUE_ONLY_ATTR;
  }

  /**
   * Tells the text formatter that the array value should be represented as a cardinal number in
   * text.
   *
   * @return asCardinal attribute
   */
  public static Attribute<Boolean> asCardinal() {
    return AS_CARDINAL_ATTR;
  }

  /**
   * Tells the text formatter to render a display name in text.
   *
   * @return displayName attribute
   */
  public static Attribute<String> withDisplayName(String displayName) {
    return DISPLAY_NAME.bindValue(displayName);
  }

  /**
   * Tells the text formatter that the string value or array value should be abbreviated after the
   * given number of elements.
   *
   * @param after the maximum number of elements to render
   * @return abbreviateAfter attribute
   */
  public static Attribute<Integer> abbreviateAfter(Integer after) {
    return ABBREVIATE_AFTER.bindValue(after);
  }

  /**
   * Tells the text formatter to render nothing in text.
   *
   * @return elide attribute
   */
  public static Attribute<Boolean> asElided() {
    return ELIDE_ATTR;
  }

  /**
   * Tells the JSON formatter to render using the field visitor
   *
   * <p>This is very low level and should probably not be used directly.
   *
   * @param visitor the field visitor
   * @return structured format attribute
   */
  public static @NotNull Attribute<FieldVisitor> withStructuredFormat(FieldVisitor visitor) {
    return STRUCTURED_FORMAT.bindValue(visitor);
  }

  /**
   * Tells the text formatter to render the field using the field visitor.
   *
   * <p>This is very low level and should probably not be used directly.
   *
   * @param visitor the field visitor
   * @return text format attribute
   */
  public static @NotNull Attribute<FieldVisitor> withToStringFormat(FieldVisitor visitor) {
    return TOSTRING_FORMAT.bindValue(visitor);
  }

  /**
   * Sets the value to render with the given string with the ToString formatter.
   *
   * @param string the value to use in line oriented format.
   * @return the attribute.
   */
  public static @NotNull Attribute<String> withToStringValue(String string) {
    return TOSTRING_VALUE.bindValue(string);
  }
}
