package com.tersesystems.echopraxia.api;

public interface FieldCreator {

  Field keyValue(String name, Value<?> value);

  <F extends Field> F keyValue(String name, Value<?> value, Class<F> fieldClass);

  Field value(String name, Value<?> value);

  <F extends Field> F value(String name, Value<?> value, Class<F> fieldClass);
}
