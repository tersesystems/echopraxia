package com.tersesystems.echopraxia.api;

import java.util.List;

public interface FieldVisitor {

  Field visit(Field field);

  void visitAttributes(Attributes attributes);

  void visitName(String name);

  Field visitString(Value<String> v);

  Field visitException(Value<Throwable> exception);

  Field visitBoolean(Value<Boolean> aBoolean);

  Field visitNumber(Value<? extends Number> number);

  Field visitNull();

  Field visitArray(Value<List<Value<?>>> array);

  ObjectVisitor visitObject();

  interface ArrayVisitor {
    Field done();

    void visit(Value<?> value);
  }

  interface ObjectVisitor {
    Field done();

    void visit(Field child);

    FieldVisitor visitChild();
  }
}
