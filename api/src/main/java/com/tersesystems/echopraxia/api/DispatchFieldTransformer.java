package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DispatchFieldTransformer implements FieldTransformer {

  private final FieldVisitor visitor;

  public DispatchFieldTransformer(FieldVisitor visitor) {
    this.visitor = visitor;
  }

  @Override
  public @NotNull Field convertArgumentField(@NotNull Field field) {
    return dispatch(field, visitor);
  }

  @Override
  public @NotNull Field convertLoggerField(@NotNull Field field) {
    return dispatch(field, visitor);
  }

  public Field dispatch(Field f, FieldVisitor visitor) {
    visitor.visitAttributes(f.attributes());
    visitor.visitName(f.name());
    switch (f.value().type()) {
      case ARRAY:
        FieldVisitor.ArrayVisitor arrayVisitor = visitor.visitArray();
        List<Value<?>> raw = f.value().asArray().raw();
        for (Value<?> value : raw) {
          arrayVisitor.visit(value);
        }
        return arrayVisitor.done();

      case OBJECT:
        FieldVisitor.ObjectVisitor objectVisitor = visitor.visitObject();
        for (Field child : f.value().asObject().raw()) {
          objectVisitor.visit(child);
        }
        return objectVisitor.done();

      case STRING:
        return visitor.visitString(f.value().asString());

      case NUMBER:
        return visitor.visitNumber(f.value().asNumber());

      case BOOLEAN:
        return visitor.visitBoolean(f.value().asBoolean());

      case EXCEPTION:
        return visitor.visitException(f.value().asException());

      case NULL:
        return visitor.visitNull();

      default:
        throw new IllegalStateException("Unknown value type!");
    }
  }
}
