package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class VisitorTests {

  public static final AttributeKey<Class<?>> CLASS_TYPE_ATTR = AttributeKey.create("class");

  static final FieldCreator<InstantField> fieldCreator = new InstantFieldCreator();

  @Test
  public void testInBedVisitor() {
    FieldVisitor inBedVisitor =
        new SimpleFieldVisitor(DefaultField.class) {
          @Override
          public @NotNull Field visitString(@NotNull Value<String> stringValue) {
            return super.visitString(Value.string(stringValue.raw() + " IN BED"));
          }
        };
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(inBedVisitor);

    FieldBuilder fb = FieldBuilder.instance();
    Field field = fb.string("fortuneCookie", "You will have a long and illustrious career");
    Field converted = fieldTransformer.tranformArgumentField(field);
    assertThat(converted.value().asString().raw()).endsWith("IN BED");
  }

  @Test
  public void testNestedInBedVisitor() {
    FieldVisitor inBedVisitor =
        new SimpleFieldVisitor(DefaultField.class) {
          @Override
          public @NotNull Field visitString(@NotNull Value<String> stringValue) {
            return super.visitString(Value.string(stringValue.raw() + " IN BED"));
          }
        };
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(inBedVisitor);

    FieldBuilder fb = FieldBuilder.instance();
    Field cookieField = fb.string("fortuneCookie", "You will have a long and illustrious career");
    Field restaurantField = fb.object("restaurant", cookieField);
    Field converted = fieldTransformer.tranformArgumentField(restaurantField);
    Field inBedField = converted.value().asObject().raw().get(0);
    assertThat(inBedField.value().asString().raw()).endsWith("IN BED");
  }

  @Test
  public void testInstant() {
    SimpleFieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);

    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field instantField = fb.instant("instant", Instant.ofEpochMilli(0));

    MappedField transformed = (MappedField) fieldTransformer.tranformArgumentField(instantField);
    Field textField = transformed.getTextField();
    Field structuredField = transformed.getStructuredField();

    List<Field> jsonFields = structuredField.value().asObject().raw();
    assertThat(jsonFields.get(0).name()).isEqualTo("@type");
    assertThat(jsonFields.get(0).value().asString().raw())
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");

    assertThat(textField.value().asString().raw()).isEqualTo(Instant.ofEpochMilli(0).toString());
  }

  @Test
  public void testInstantInArray() {
    SimpleFieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);

    MyFieldBuilder fb = MyFieldBuilder.instance();
    Instant[] instants = {Instant.ofEpochMilli(0)};
    Field instantArrayField =
        fb.array("instantArray", Value.array(i -> Value.string(i.toString()), instants))
            .withClassType(Instant.class);

    MappedField transformed = (MappedField) fieldTransformer.tranformArgumentField(instantArrayField);

    // text field has an array of strings
    Field textField = transformed.getTextField();

    // structured field has an array of objects.
    Field structuredField = transformed.getStructuredField();

    List<Value<?>> objectElements = structuredField.value().asArray().raw();
    List<Field> fields = objectElements.get(0).asObject().raw();
    assertThat(fields.get(0).name()).isEqualTo("@type");
    assertThat(fields.get(0).value().asString().raw())
      .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");

    List<Value<?>> stringElements = textField.value().asArray().raw();
    String dateString = stringElements.get(0).asString().raw();
    assertThat(dateString).isEqualTo(Instant.ofEpochMilli(0).toString());
  }

  @Test
  public void testInstantInObject() {
    FieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);

    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field dateRange =
        fieldTransformer.tranformArgumentField(
            fb.object(
                "dateRange",
                fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time"),
                fb.instant("endTime", Instant.ofEpochMilli(0)).withDisplayName("end time")));

    MappedField startTime = (MappedField) dateRange.value().asObject().raw().get(0);
    Field textField = startTime.getTextField();
    Field structuredField = startTime.getStructuredField();

    List<Field> jsonFields = structuredField.value().asObject().raw();
    assertThat(jsonFields.get(0).name()).isEqualTo("@type");
    assertThat(jsonFields.get(0).value().asString().raw())
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");

    assertThat(textField.toString()).isEqualTo("\"start time\"=1970-01-01T00:00:00Z");
  }

  @Test
  public void testInstantInObjectInArray() {
    FieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);

    MyFieldBuilder fb = MyFieldBuilder.instance();

    Value.ObjectValue obj =
        Value.object(
            fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time"),
            fb.instant("endTime", Instant.ofEpochMilli(0)).withDisplayName("end time"));
    Field array = fb.array("arrayOfDateRanges", obj);
    Field dateRange = fieldTransformer.tranformArgumentField(array);

    Value.ArrayValue array1 = dateRange.value().asArray();
    List<Field> value = array1.raw().get(0).asObject().raw();
    MappedField field = (MappedField) value.get(0);
    assertThat(field.getStructuredField().value().asObject().raw().get(0).name())
        .isEqualTo("@type");
  }

  @Test
  public void testArrayOfArrayOfArrayOfInstant() {
    FieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);

    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field instant = fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time");
    Field array3 = fb.array("array3", Value.array(Value.array(Value.array(Value.object(instant)))));
    Field a3 = fieldTransformer.tranformArgumentField(array3);

    Field f = a3.value().asArray().raw().get(0).asArray().raw().get(0).asArray().raw().get(0).asObject().raw().get(0);
    assertThat(f).isInstanceOf(MappedField.class);
    MappedField mapped = (MappedField) f;
    //System.out.println(mapped);
  }

  @Test
  public void testArrayObjectArrayObjectInstant() {
    FieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);

    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field instant = fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time");
    Field array1 = fb.array("array1", Value.array(Value.object(fb.array("array2", Value.object(instant)))));
    Field a1 = fieldTransformer.tranformArgumentField(array1);

    System.out.println(a1);
    assertThat(a1.name()).isEqualTo("array1");
  }

  static class MyFieldBuilder implements DefaultFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder();
    }

    public InstantField instant(String name, Instant instant) {
      return string(name, instant.toString()).withClassType(Instant.class);
    }

    public InstantField string(@NotNull String name, @NotNull String value) {
      return keyValue(name, Value.string(value));
    }

    public InstantField array(@NotNull String name, @NotNull Value.ArrayValue value) {
      return keyValue(name, value);
    }

    @Override
    public InstantField array(@NotNull String name, @NotNull Value.ObjectValue... values) {
      return keyValue(name, Value.array(values));
    }

    public InstantField keyValue(@NotNull String name, @NotNull Value<?> value) {
      return fieldCreator.create(name, value, Attributes.empty());
    }

    @Override
    public InstantField value(@NotNull String name, @NotNull Value<?> value) {
      return fieldCreator.create(name, value, PresentationHints.valueOnlyAttributes());
    }

    public Field typedInstant(String name, Value<String> v) {
      return object(name, typedInstantValue(v));
    }

    Value.ObjectValue typedInstantValue(Value<String> v) {
      return Value.object(
          string("@type", "http://www.w3.org/2001/XMLSchema#dateTime"), keyValue("@value", v));
    }
  }

  static class InstantFieldVisitor extends LoggingFieldVisitor {
    private final MyFieldBuilder fb = MyFieldBuilder.instance();

    public InstantFieldVisitor() {
      super(VisitorTests.fieldCreator);
    }

    @Override
    public @NotNull Field visitString(@NotNull Value<String> stringValue) {
      Field textField = fieldCreator.create(name, stringValue, attributes);
      return isInstant() ? new MappedField(textField, fb.typedInstant(name, stringValue)) : textField;
    }

    @Override
    public @NotNull ArrayVisitor visitArray() {
      return new InstantArrayVisitor();
    }

    private Value<?> mapInstant(Value<?> el) {
      return el.type() == Value.Type.STRING ? fb.typedInstantValue(el.asString()) : el;
    }

    private boolean isInstant() {
      Optional<Class<?>> optClass = attributes.getOptional(CLASS_TYPE_ATTR);
      return optClass.isPresent() && optClass.get().getName().equals("java.time.Instant");
    }

    class InstantArrayVisitor extends LoggingArrayVisitor {
      @Override
      public @NotNull Field done() {
        //        if (isInstant()) {
        //          List<Value<?>> objectValues =
        //            arrayValue.raw().stream().map(this::mapInstant).collect(Collectors.toList());
        //          Field textField = fieldCreator.create(name, arrayValue, attributes);
        //          Field structuredField = fieldCreator.create(name, Value.array(objectValues), attributes);
        //          return new MappedField(textField, structuredField);
        //        } else {
        return fieldCreator.create(name, Value.array(elements), attributes);
      }
    }
  }

  static class InstantField extends DefaultField {

    protected InstantField(
        @NotNull String name, @NotNull Value<?> value, @NotNull Attributes attributes) {
      super(name, value, attributes);
    }

    public InstantField withClassType(Class<?> clazz) {
      return withAttribute(CLASS_TYPE_ATTR.bindValue(clazz));
    }

    @Override
    public <A> @NotNull InstantField withAttribute(@NotNull Attribute<A> attr) {
      return newAttributes(attributes.plus(attr));
    }

    private @NotNull InstantField newAttributes(@NotNull Attributes attrs) {
      return new InstantField(name, value, attrs);
    }
  }

  static class InstantFieldCreator implements FieldCreator<InstantField> {

    @Override
    public @NotNull InstantField create(
        @NotNull String name, @NotNull Value<?> value, @NotNull Attributes attributes) {
      return new InstantField(name, value, attributes);
    }

    @Override
    public boolean canServe(@NotNull Class<?> t) {
      return t.isAssignableFrom(InstantField.class);
    }
  }
}
