package com.tersesystems.echopraxia.api;

public interface FieldCreator<F extends Field> {

  F create(String name, Value<?> value, Attributes attributes);

  boolean canServe(Class<?> t);
}
