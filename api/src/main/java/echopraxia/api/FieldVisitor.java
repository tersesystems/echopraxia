package echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * A visitor interface that transforms the field in a structured JSON view.
 *
 * @since 3.0
 */
public interface FieldVisitor {

  @NotNull
  Field visit(@NotNull Field field);

  void visitAttributes(@NotNull Attributes attributes);

  void visitName(@NotNull String name);

  @NotNull
  Field visitString(@NotNull Value<String> stringValue);

  @NotNull
  Field visitException(@NotNull Value<Throwable> exceptionValue);

  @NotNull
  Field visitBoolean(@NotNull Value<Boolean> booleanValue);

  @NotNull
  Field visitNumber(@NotNull Value<? extends Number> numberValue);

  @NotNull
  Field visitNull();

  @NotNull
  ArrayVisitor visitArray();

  @NotNull
  ObjectVisitor visitObject();

  interface ArrayVisitor {
    @NotNull
    Field done();

    void visitElement(@NotNull Value<?> value);

    void visitStringElement(Value.StringValue stringValue);

    void visitNumberElement(Value.NumberValue<?> numberValue);

    void visitBooleanElement(Value.BooleanValue booleanValue);

    void visitArrayElement(Value.ArrayValue arrayValue);

    void visitObjectElement(Value.ObjectValue objectValue);

    void visitExceptionElement(Value.ExceptionValue exceptionValue);

    void visitNullElement();
  }

  interface ObjectVisitor {
    @NotNull
    Field done();

    void visit(@NotNull Field childField);

    @NotNull
    FieldVisitor visitChild();
  }
}
