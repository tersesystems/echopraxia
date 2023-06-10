package com.tersesystems.echopraxia.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tersesystems.echopraxia.api.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class VisitorTests {

  private static final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

  private JsonNode toJson(Field field) {
    return mapper.valueToTree(field);
  }

  @Test
  public void testInBedVisitor() {
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field field = fb.string("fortuneCookie", "You will have a long and illustrious career").withFieldVisitor(new SimpleFieldVisitor(DefaultField.class) {
      @Override
      public @NotNull Field visitString(@NotNull Value<String> stringValue) {
        return fb.string(name, Value.string(stringValue.raw() + " IN BED"));
      }
    });

    assertThatJson(toJson(field))
      .inPath("$.fortuneCookie")
      .asString()
      .endsWith("IN BED");
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
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field cookieField = fb.string("fortuneCookie", "You will have a long and illustrious career").withFieldVisitor(inBedVisitor);
    Field restaurantField = fb.object("restaurant", cookieField);
    assertThatJson(restaurantField).inPath("$.restaurant.fortuneCookie").asString().endsWith("IN BED");
  }


  @Test
  public void testInstant() {
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field instantField = fb.instant("instant", Instant.ofEpochMilli(0));

    assertThatJson(toJson(instantField)).inPath("$.instant.@type").asString().isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }
  
  //
  //  @Test
  //  public void testInstantInArray() {
  //    SimpleFieldVisitor instantVisitor = new InstantFieldVisitor();
  //    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);
  //
  //    MyFieldBuilder fb = MyFieldBuilder.instance();
  //    Instant[] instants = {Instant.ofEpochMilli(0)};
  //    Field instantArrayField =
  //        fb.array("instantArray", Value.array(i -> Value.string(i.toString()), instants))
  //            .withClassType(Instant.class);
  //
  //    MappedField transformed = (MappedField) fieldTransformer.tranformArgumentField(instantArrayField);
  //
  //    // text field has an array of strings
  //    Field textField = transformed.getTextField();
  //
  //    // structured field has an array of objects.
  //    Field structuredField = transformed.getStructuredField();
  //
  //    List<Value<?>> objectElements = structuredField.value().asArray().raw();
  //    List<Field> fields = objectElements.get(0).asObject().raw();
  //    assertThat(fields.get(0).name()).isEqualTo("@type");
  //    assertThat(fields.get(0).value().asString().raw())
  //      .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  //
  //    List<Value<?>> stringElements = textField.value().asArray().raw();
  //    String dateString = stringElements.get(0).asString().raw();
  //    assertThat(dateString).isEqualTo(Instant.ofEpochMilli(0).toString());
  //  }
  //
  //  @Test
  //  public void testInstantInObject() {
  //    FieldVisitor instantVisitor = new InstantFieldVisitor();
  //    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);
  //
  //    MyFieldBuilder fb = MyFieldBuilder.instance();
  //
  //    Field dateRange =
  //        fieldTransformer.tranformArgumentField(
  //            fb.object(
  //                "dateRange",
  //                fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time"),
  //                fb.instant("endTime", Instant.ofEpochMilli(0)).withDisplayName("end time")));
  //
  //    MappedField startTime = (MappedField) dateRange.value().asObject().raw().get(0);
  //    Field textField = startTime.getTextField();
  //    Field structuredField = startTime.getStructuredField();
  //
  //    List<Field> jsonFields = structuredField.value().asObject().raw();
  //    assertThat(jsonFields.get(0).name()).isEqualTo("@type");
  //    assertThat(jsonFields.get(0).value().asString().raw())
  //        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  //
  //    assertThat(textField.toString()).isEqualTo("\"start time\"=1970-01-01T00:00:00Z");
  //  }
  //
  //  @Test
  //  public void testInstantInObjectInArray() {
  //    FieldVisitor instantVisitor = new InstantFieldVisitor();
  //    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);
  //
  //    MyFieldBuilder fb = MyFieldBuilder.instance();
  //
  //    Value.ObjectValue obj =
  //        Value.object(
  //            fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time"),
  //            fb.instant("endTime", Instant.ofEpochMilli(0)).withDisplayName("end time"));
  //    Field array = fb.array("arrayOfDateRanges", obj);
  //    Field dateRange = fieldTransformer.tranformArgumentField(array);
  //
  //    Value.ArrayValue array1 = dateRange.value().asArray();
  //    List<Field> value = array1.raw().get(0).asObject().raw();
  //    MappedField field = (MappedField) value.get(0);
  //    assertThat(field.getStructuredField().value().asObject().raw().get(0).name())
  //        .isEqualTo("@type");
  //  }
  //
  //  @Test
  //  public void testArrayOfArrayOfArrayOfInstant() {
  //    FieldVisitor instantVisitor = new InstantFieldVisitor();
  //    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);
  //
  //    MyFieldBuilder fb = MyFieldBuilder.instance();
  //
  //    Field instant = fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time");
  //    Field array3 = fb.array("array3", Value.array(Value.array(Value.array(Value.object(instant)))));
  //    Field a3 = fieldTransformer.tranformArgumentField(array3);
  //
  //    Field f = a3.value().asArray().raw().get(0).asArray().raw().get(0).asArray().raw().get(0).asObject().raw().get(0);
  //    assertThat(f).isInstanceOf(MappedField.class);
  //    MappedField mapped = (MappedField) f;
  //    //System.out.println(mapped);
  //  }
  //
  //  @Test
  //  public void testArrayObjectArrayObjectInstant() {
  //    FieldVisitor instantVisitor = new InstantFieldVisitor();
  //    FieldTransformer fieldTransformer = new VisitorFieldTransformer(instantVisitor);
  //
  //    MyFieldBuilder fb = MyFieldBuilder.instance();
  //
  //    Field instant = fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time");
  //    Field array1 = fb.array("array1", Value.array(Value.object(fb.array("array2", Value.object(instant)))));
  //    Field a1 = fieldTransformer.tranformArgumentField(array1);
  //
  //    System.out.println(a1);
  //    assertThat(a1.name()).isEqualTo("array1");
  //  }

  static class MyFieldBuilder implements DefaultFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder();
    }

    public DefaultField instant(String name, Instant instant) {
      return string(name, instant.toString()).withFieldVisitor(new InstantFieldVisitor());
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

    public <F extends Field> InstantFieldVisitor() {
      super(DefaultField.class);
    }

    @Override
    public @NotNull Field visitString(@NotNull Value<String> stringValue) {
      return fb.typedInstant(name, stringValue);
    }

    @Override
    public @NotNull ArrayVisitor visitArray() {
      return new InstantArrayVisitor();
    }

    private Value<?> mapInstant(Value<?> el) {
      return el.type() == Value.Type.STRING ? fb.typedInstantValue(el.asString()) : el;
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
}
