package com.tersesystems.echopraxia.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A very simple field visitor that returns its input.
 *
 * @since 3.0
 */
public class SimpleFieldVisitor implements FieldVisitor {

  protected final FieldCreator<?> fieldCreator;

  protected Attributes attributes;
  protected String name;

  public <F extends Field> SimpleFieldVisitor(Class<F> fieldClass) {
    this.fieldCreator = EchopraxiaService.getInstance().getFieldCreator(fieldClass);
  }

  public SimpleFieldVisitor(FieldCreator<?> fieldCreator) {
    this.fieldCreator = fieldCreator;
  }

  @Override
  public Field visit(Field f) {
    visitAttributes(f.attributes());
    visitName(f.name());
    return visitValue(f.value());
  }

  public Field visitValue(Value<?> value) {
    switch (value.type()) {
      case OBJECT:
        FieldVisitor.ObjectVisitor objectVisitor = visitObject();
        for (Field child : value.asObject().raw()) {
          FieldVisitor subVisitor = objectVisitor.visitChild();
          objectVisitor.visit(subVisitor.visit(child));
        }
        return objectVisitor.done();

      case ARRAY:
        return visitArray(value.asArray());

      case STRING:
        return visitString(value.asString());

      case NUMBER:
        return visitNumber(value.asNumber());

      case BOOLEAN:
        return visitBoolean(value.asBoolean());

      case EXCEPTION:
        return visitException(value.asException());

      case NULL:
        return visitNull();

      default:
        throw new IllegalStateException("Unknown value type!");
    }
  }

  @Override
  public void visitAttributes(Attributes attributes) {
    this.attributes = attributes;
  }

  @Override
  public void visitName(String name) {
    this.name = name;
  }

  @Override
  public Field visitString(Value<String> v) {
    return fieldCreator.create(name, v, attributes);
  }

  @Override
  public Field visitException(Value<Throwable> exception) {
    return fieldCreator.create(name, exception, attributes);
  }

  @Override
  public Field visitBoolean(Value<Boolean> aBoolean) {
    return fieldCreator.create(name, aBoolean, attributes);
  }

  @Override
  public Field visitNumber(Value<? extends Number> number) {
    return fieldCreator.create(name, number, attributes);
  }

  @Override
  public Field visitNull() {
    return fieldCreator.create(name, Value.nullValue(), attributes);
  }

  @Override
  public Field visitArray(Value<List<Value<?>>> array) {
    List<Value<?>> collect =
        array.raw().stream()
            .map(
                el -> {
                  if (el.type() == Value.Type.OBJECT) {
                    List<Field> fields =
                        el.asObject().raw().stream().map(this::visit).collect(Collectors.toList());
                    return Value.object(fields);
                  }

                  // XXX What if we have array of array of object?

                  return el;
                })
            .collect(Collectors.toList());
    return fieldCreator.create(name, Value.array(collect), attributes);
  }

  @Override
  public ObjectVisitor visitObject() {
    return new DefaultObjectVisitor();
  }

  class DefaultObjectVisitor implements ObjectVisitor {
    private final List<Field> fields;

    public DefaultObjectVisitor() {
      fields = new ArrayList<>();
    }

    @Override
    public Field done() {
      return fieldCreator.create(name, Value.object(fields), attributes);
    }

    @Override
    public void visit(Field child) {
      fields.add(child);
    }

    @Override
    public FieldVisitor visitChild() {
      return SimpleFieldVisitor.this;
    }
  }
}
