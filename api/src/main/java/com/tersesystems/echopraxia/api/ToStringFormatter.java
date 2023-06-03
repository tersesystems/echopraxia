package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * This interface handles the "logfmt" like text serialization of fields and values.
 *
 * @since 3.0
 */
public interface ToStringFormatter {

  /**
   * Formats a field, applying attributes to the field name and value as needed.
   *
   * <p>This method is called by field.toString().
   *
   * @return a field formatted in text format.
   */
  @NotNull
  String formatField(@NotNull Field field);

  /**
   * Formats a value, without any attributes applied.
   *
   * <p>This method is called by value.toString().
   *
   * @return a value formatted in text format.
   */
  @NotNull
  String formatValue(@NotNull Value<?> value);
}
