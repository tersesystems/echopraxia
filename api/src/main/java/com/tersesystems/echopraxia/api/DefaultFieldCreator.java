package com.tersesystems.echopraxia.api;

public class DefaultFieldCreator implements FieldCreator<Field> {
  @Override
  public Field keyValue(String name, Value<?> value) {
    return new DefaultField(name, value, Attributes.empty());
  }

  @Override
  public Field value(String name, Value<?> value) {
    return new DefaultField(name, value, FieldAttributes.valueOnlyAttributes());
  }

  @Override
  public boolean canServe(Class<?> t) {
    return t.isAssignableFrom(DefaultField.class);
  }
}
