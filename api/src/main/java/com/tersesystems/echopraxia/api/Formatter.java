package com.tersesystems.echopraxia.api;

public interface Formatter {

  /**
   * @return a field formatted in text format.
   */
  String formatField(Field field);

  /**
   * @return a value formatted in text format.
   */
  String formatValue(Value<?> value);

}
