package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MappedField implements Field {

  private final Field textField;
  private final Field structuredField;

  public MappedField(Field textField, Field structuredField) {
    this.textField = textField;
    this.structuredField = structuredField;
  }

  public Field getTextField() {
    return this.textField;
  }

  public Field getStructuredField() {
    return this.structuredField;
  }

  @Override
  public @NotNull String name() {
    return structuredField.name();
  }

  @Override
  public @NotNull Value<?> value() {
    return structuredField.value();
  }

  @Override
  public @NotNull Attributes attributes() {
    return textField.attributes();
  }

  @Override
  public @NotNull List<Field> fields() {
    return structuredField.fields();
  }

  @Override
  public String toString() {
    return textField.toString();
  }
}
