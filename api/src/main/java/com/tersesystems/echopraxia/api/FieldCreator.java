package com.tersesystems.echopraxia.api;

public interface FieldCreator<F extends Field> {

  F keyValue(String name, Value<?> value);

  F value(String name, Value<?> value);

  boolean canServe(Class<?> t);
}
