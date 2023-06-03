package com.tersesystems.echopraxia.api;

/** The default field creator. */
public class DefaultFieldCreator implements FieldCreator<DefaultField> {
  @Override
  public DefaultField create(String name, Value<?> value, Attributes attributes) {
    return new DefaultField(name, value, attributes);
  }

  @Override
  public boolean canServe(Class<?> t) {
    return t.isAssignableFrom(DefaultField.class);
  }
}
