package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class VisitorTests {

  public static final AttributeKey<Class<?>> CLASS_TYPE_ATTR = AttributeKey.create("class");

  @Test
  public void testInBedVisitor() {
    FieldVisitor inBedVisitor =
        new SimpleFieldVisitor(DefaultField.class) {
          @Override
          public Field visitString(Value<String> v) {
            return super.visitString(Value.string(v.raw() + " IN BED"));
          }
        };
    FieldTransformer fieldTransformer = new DispatchFieldTransformer(inBedVisitor);

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
          public Field visitString(Value<String> v) {
            return super.visitString(Value.string(v.raw() + " IN BED"));
          }
        };
    FieldTransformer fieldTransformer = new DispatchFieldTransformer(inBedVisitor);

    FieldBuilder fb = FieldBuilder.instance();
    Field cookieField = fb.string("fortuneCookie", "You will have a long and illustrious career");
    Field restaurantField = fb.object("restaurant", cookieField);
    Field converted = fieldTransformer.tranformArgumentField(restaurantField);
    Field inBedField = converted.value().asObject().raw().get(0);
    assertThat(inBedField.value().asString().raw()).endsWith("IN BED");
  }

  @Test
  public void testStructuredInstant() {
    SimpleFieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new DispatchFieldTransformer(instantVisitor);

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
  public void testNestedInstant() {
    FieldVisitor instantVisitor = new InstantFieldVisitor();
    FieldTransformer fieldTransformer = new DispatchFieldTransformer(instantVisitor);

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

  static class MyFieldBuilder implements DefaultFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder();
    }

    public DefaultField instant(String name, Instant instant) {
      return string(name, instant.toString())
          .withAttribute(CLASS_TYPE_ATTR.bindValue(instant.getClass()));
    }

    public Field typedInstant(String name, Value<String> v) {
      return typed(name, "http://www.w3.org/2001/XMLSchema#dateTime", v);
    }

    private Field typed(String name, String type, Value<String> v) {
      return object(name, string("@type", type), keyValue("@value", v));
    }
  }

  static class InstantFieldVisitor extends SimpleFieldVisitor {
    private final MyFieldBuilder fb = MyFieldBuilder.instance();

    public InstantFieldVisitor() {
      super(DefaultField.class);
    }

    @Override
    public @NotNull Field visitString(Value<String> v) {
      Field textField = fieldCreator.create(name, v, attributes);
      return isInstant() ? new MappedField(textField, fb.typedInstant(name, v)) : textField;
    }

    private boolean isInstant() {
      Optional<Class<?>> optClass = attributes.getOptional(CLASS_TYPE_ATTR);
      return optClass.isPresent() && optClass.get().getName().equals("java.time.Instant");
    }
  }
  ;
}
