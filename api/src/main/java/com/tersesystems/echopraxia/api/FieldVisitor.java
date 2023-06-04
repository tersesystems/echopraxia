package com.tersesystems.echopraxia.api;

public interface FieldVisitor {

  void visitAttributes(Attributes attributes);

  void visitName(String name);

  Field visitString(Value<String> v);

  Field visitException(Value<Throwable> exception);

  Field visitBoolean(Value<Boolean> aBoolean);

  Field visitNumber(Value<? extends Number> number);

  Field visitNull();

  ArrayVisitor visitArray();

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
