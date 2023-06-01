package com.tersesystems.echopraxia.api;

public class ExtendedFieldCreator implements FieldCreator<ExtendedField> {
  @Override
  public ExtendedField keyValue(String name, Value<?> value) {
    return new ExtendedField(name, value, Attributes.empty());
  }

  @Override
  public ExtendedField value(String name, Value<?> value) {
    return new ExtendedField(name, value, FieldAttributes.valueOnlyAttributes());
  }

  @Override
  public boolean canServe(Class<?> t) {
    return t.isAssignableFrom(ExtendedField.class);
  }
}
