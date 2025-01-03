package echopraxia.api;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A very simple field visitor that returns its input.
 *
 * @since 3.0
 */
public class SimpleFieldVisitor implements FieldVisitor {

  protected final Class<? extends Field> fieldClass;
  protected Attributes attributes;
  protected String name;

  public SimpleFieldVisitor() {
    this(DefaultField.class);
  }

  public <F extends Field> SimpleFieldVisitor(Class<F> fieldClass) {
    this.fieldClass = fieldClass;
  }

  @Override
  public @NotNull Field visit(@NotNull Field f) {
    visitAttributes(f.attributes());
    visitName(f.name());
    return visitValue(f.value());
  }

  @NotNull
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
        FieldVisitor.ArrayVisitor arrayVisitor = visitArray();
        for (Value<?> el : value.asArray().raw()) {
          arrayVisitor.visitElement(el);
        }
        return arrayVisitor.done();

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
  public void visitAttributes(@NotNull Attributes attributes) {
    this.attributes = attributes;
  }

  @Override
  public void visitName(@NotNull String name) {
    this.name = name;
  }

  @Override
  public @NotNull Field visitString(@NotNull Value<String> stringValue) {
    return create(name, stringValue, attributes);
  }

  @Override
  public @NotNull Field visitException(@NotNull Value<Throwable> exceptionValue) {
    return create(name, exceptionValue, attributes);
  }

  private @NotNull Field create(String name, @NotNull Value<?> value, Attributes attributes) {
    if (fieldClass == DefaultField.class) {
      return new DefaultField(name, value, attributes);
    } else {
      try {
        Constructor<? extends Field> constructor =
            fieldClass.getConstructor(String.class, Value.class, Attributes.class);
        return constructor.newInstance(name, value, attributes);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public @NotNull Field visitBoolean(@NotNull Value<Boolean> booleanValue) {
    return create(name, booleanValue, attributes);
  }

  @Override
  public @NotNull Field visitNumber(@NotNull Value<? extends Number> numberValue) {
    return create(name, numberValue, attributes);
  }

  @Override
  public @NotNull Field visitNull() {
    return create(name, Value.nullValue(), attributes);
  }

  @Override
  public @NotNull ArrayVisitor visitArray() {
    return new SimpleArrayVisitor();
  }

  @Override
  public @NotNull ObjectVisitor visitObject() {
    return new SimpleObjectVisitor();
  }

  public class SimpleArrayVisitor implements ArrayVisitor {

    protected final List<Value<?>> elements = new ArrayList<>();

    @Override
    public @NotNull Field done() {
      return create(name, Value.array(elements), attributes);
    }

    @Override
    public void visitElement(@NotNull Value<?> value) {
      switch (value.type()) {
        case ARRAY:
          visitArrayElement(value.asArray());
          break;
        case OBJECT:
          visitObjectElement(value.asObject());
          break;
        case STRING:
          visitStringElement(value.asString());
          break;
        case NUMBER:
          visitNumberElement(value.asNumber());
          break;
        case BOOLEAN:
          visitBooleanElement(value.asBoolean());
          break;
        case EXCEPTION:
          visitExceptionElement(value.asException());
          break;
        case NULL:
          visitNullElement();
          break;
      }
    }

    @Override
    public void visitStringElement(Value.StringValue stringValue) {
      elements.add(stringValue);
    }

    @Override
    public void visitNumberElement(Value.NumberValue<?> numberValue) {
      elements.add(numberValue);
    }

    @Override
    public void visitBooleanElement(Value.BooleanValue booleanValue) {
      elements.add(booleanValue);
    }

    @Override
    public void visitArrayElement(Value.ArrayValue arrayValue) {
      elements.add(arrayValue);
    }

    @Override
    public void visitObjectElement(Value.ObjectValue objectValue) {
      elements.add(objectValue);
    }

    @Override
    public void visitExceptionElement(Value.ExceptionValue exceptionValue) {
      elements.add(exceptionValue);
    }

    @Override
    public void visitNullElement() {
      elements.add(Value.nullValue());
    }
  }

  public class SimpleObjectVisitor implements ObjectVisitor {
    protected final List<Field> fields;

    public SimpleObjectVisitor() {
      fields = new ArrayList<>();
    }

    @Override
    public @NotNull Field done() {
      return create(name, Value.object(fields), attributes);
    }

    @Override
    public void visit(@NotNull Field childField) {
      fields.add(childField);
    }

    @Override
    public @NotNull FieldVisitor visitChild() {
      return SimpleFieldVisitor.this;
    }
  }
}
