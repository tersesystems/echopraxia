package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

public interface ToStringFormatter {

  /**
   * @return a field formatted in text format.
   */
  @NotNull
  String formatField(@NotNull Field field);

  /**
   * @return a value formatted in text format.
   */
  @NotNull
  String formatValue(@NotNull Value<?> value);
}
