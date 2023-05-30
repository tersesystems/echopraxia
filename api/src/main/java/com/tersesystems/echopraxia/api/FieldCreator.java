package com.tersesystems.echopraxia.api;

public interface FieldCreator {

  Field keyValue(String name, Value<?> value);

  Field value(String name, Value<?> value);
}
