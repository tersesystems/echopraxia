package com.tersesystems.echopraxia.api;

import com.tersesystems.echopraxia.spi.DefaultToStringFormatter;
import org.jetbrains.annotations.NotNull;

/**
 * This is the text formaatter interface that handles the "logfmt" like text serialization of fields
 * and values.
 *
 * @since 3.0
 */
public interface ToStringFormatter {

  static ToStringFormatter getInstance() {
    return DefaultToStringFormatter.getInstance();
  }

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
