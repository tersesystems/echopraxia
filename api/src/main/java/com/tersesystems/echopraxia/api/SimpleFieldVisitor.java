package com.tersesystems.echopraxia.api;

import java.util.ArrayList;
import java.util.List;

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
    switch (f.value().type()) {
      case ARRAY:
        FieldVisitor.ArrayVisitor arrayVisitor = visitArray();
        List<Value<?>> raw = f.value().asArray().raw();
        for (Value<?> value : raw) {
          // XXX How do we map this back through dispatch?
          arrayVisitor.visit(value);
        }
        return arrayVisitor.done();

      case OBJECT:
        FieldVisitor.ObjectVisitor objectVisitor = visitObject();
        for (Field child : f.value().asObject().raw()) {
          FieldVisitor subVisitor = objectVisitor.visitChild();
          objectVisitor.visit(subVisitor.visit(child));
        }
        return objectVisitor.done();

      case STRING:
        return visitString(f.value().asString());

      case NUMBER:
        return visitNumber(f.value().asNumber());

      case BOOLEAN:
        return visitBoolean(f.value().asBoolean());

      case EXCEPTION:
        return visitException(f.value().asException());

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
  public ArrayVisitor visitArray() {
    return new DefaultArrayVisitor();
  }

  @Override
  public ObjectVisitor visitObject() {
    return new DefaultObjectVisitor();
  }

  class DefaultArrayVisitor implements ArrayVisitor {
    private final List<Value<?>> values;

    public DefaultArrayVisitor() {
      this.values = new ArrayList<>();
    }

    @Override
    public Field done() {
      return fieldCreator.create(name, Value.array(values), attributes);
    }

    @Override
    public void visit(Value<?> value) {
      values.add(value);
    }
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
