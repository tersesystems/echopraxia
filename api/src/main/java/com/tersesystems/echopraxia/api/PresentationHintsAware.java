package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * An interface for fields that know about presentation hint attributes.
 *
 * @since 3.0
 */
public interface PresentationHintsAware<F extends Field> {

  /**
   * Tells the formatter that the field should be rendered with the value only, i.e. "value" and not
   * "name=value".
   *
   * @return valueOnly field
   */
  @NotNull
  F asValueOnly();

  /**
   * Tells the formatter that the string value or array value should be abbreviated after the given
   * number of elements.
   *
   * @param after the maximum number of elements to render
   * @return the field with the attribute applied
   */
  @NotNull
  F abbreviateAfter(int after);

  /**
   * Tells the formatter that the array value should be represented as a cardinal number in text.
   *
   * @return field with cardinal attribute
   */
  @NotNull
  F asCardinal();

  /**
   * Tells the formatter that this field should be elided in text.
   *
   * @return field with elide attribute
   */
  @NotNull
  F asElided();

  /**
   * Tells the formatter to render a display name in text.
   *
   * @return displayName field
   */
  @NotNull
  F withDisplayName(@NotNull String displayName);

  @NotNull
  F withFieldVisitor(@NotNull FieldVisitor fieldVisitor);
}
