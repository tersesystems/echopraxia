package com.tersesystems.echopraxia.api;

public class DefaultFieldCreator implements FieldCreator {
  @Override
  public Field keyValue(String name, Value<?> value) {
    return new DefaultField(name, value, Attributes.empty());
  }

  @Override
  public <F extends Field> F keyValue(String name, Value<?> value, Class<F> fieldClass) {
    return (F) keyValue(name, value);
  }

  @Override
  public Field value(String name, Value<?> value) {
    return new DefaultField(name, value, FieldAttributes.valueOnlyAttributes());
  }

  @Override
  public <F extends Field> F value(String name, Value<?> value, Class<F> fieldClass) {
    return (F) value(name, value);
  }
}
