package com.tersesystems.echopraxia.api;

public interface FieldFormatter {
  String formatField(Field field);

  String formatValue(Value<?> value);
}
