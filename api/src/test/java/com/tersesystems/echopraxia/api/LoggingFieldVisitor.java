package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoggingFieldVisitor extends SimpleFieldVisitor {
  public <F extends Field> LoggingFieldVisitor(Class<F> fieldClass) {
    super(fieldClass);
  }

  public LoggingFieldVisitor(FieldCreator<?> fieldCreator) {
    super(fieldCreator);
  }

  @Override
  public Field visit(@NotNull Field field) {
    System.out.println("visit: " + field);
    return super.visit(field);
  }

  @Override
  public void visitAttributes(@NotNull Attributes attributes) {
    System.out.println("visitAttributes: " + attributes);
    super.visitAttributes(attributes);
  }

  @Override
  public void visitName(@NotNull String name) {
    System.out.println("visitName: " + name);
    super.visitName(name);
  }

  @Override
  public @NotNull Field visitString(@NotNull Value<String> stringValue) {
    System.out.println("visitString: " + stringValue);
    return super.visitString(stringValue);
  }

  @Override
  public @NotNull Field visitException(@NotNull Value<Throwable> exceptionValue) {
    System.out.println("visitException: " + exceptionValue);
    return super.visitException(exceptionValue);
  }

  @Override
  public @NotNull Field visitBoolean(@NotNull Value<Boolean> booleanValue) {
    System.out.println("visitBoolean: " + booleanValue);
    return super.visitBoolean(booleanValue);
  }

  @Override
  public @NotNull Field visitNumber(@NotNull Value<? extends Number> numberValue) {
    System.out.println("visitNumber: " + numberValue);
    return super.visitNumber(numberValue);
  }

  @Override
  public @NotNull Field visitNull() {
    System.out.println("visitNull: ");
    return super.visitNull();
  }

  @Override
  public @NotNull ArrayVisitor visitArray() {
    return new LoggingArrayVisitor();
  }

  @Override
  public @NotNull ObjectVisitor visitObject() {
    return new LoggingObjectVisitor();
  }

  class LoggingArrayVisitor extends SimpleArrayVisitor {

    @Override
    public void visitElement(@NotNull Value<?> value) {
      System.out.println("visitElement: " + value);
      super.visitElement(value);
    }

    @Override
    public void visitArrayElement(Value.ArrayValue arrayValue) {
      System.out.println("visitArrayElement: " + arrayValue);
      super.visitArrayElement(arrayValue);
    }
  }

  class LoggingObjectVisitor extends SimpleObjectVisitor {
    @Override
    public void visit(@NotNull Field childField) {
      System.out.println("objectVisit: " + childField);
      super.visit(childField);
    }
  }
}
