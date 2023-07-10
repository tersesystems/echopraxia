package com.tersesystems.echopraxia.jackson;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tersesystems.echopraxia.api.*;
import java.time.Duration;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class VisitorTests {

  private static final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

  private JsonNode toJson(Field field) {
    return mapper.valueToTree(field);
  }

  @Test
  public void testInBedVisitor() {
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field field =
        fb.string("fortuneCookie", "You will have a long and illustrious career")
            .withStructuredFormat(
                new SimpleFieldVisitor() {
                  @Override
                  public @NotNull Field visitString(@NotNull Value<String> stringValue) {
                    return fb.string(name, Value.string(stringValue.raw() + " IN BED"));
                  }
                });

    assertThatJson(toJson(field)).inPath("$.fortuneCookie").asString().endsWith("IN BED");
  }

  @Test
  public void testNestedInBedVisitor() {
    FieldVisitor inBedVisitor =
        new SimpleFieldVisitor() {
          @Override
          public @NotNull Field visitString(@NotNull Value<String> stringValue) {
            return super.visitString(Value.string(stringValue.raw() + " IN BED"));
          }
        };
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field cookieField =
        fb.string("fortuneCookie", "You will have a long and illustrious career")
            .withStructuredFormat(inBedVisitor);
    Field restaurantField = fb.object("restaurant", cookieField);
    assertThatJson(restaurantField)
        .inPath("$.restaurant.fortuneCookie")
        .asString()
        .endsWith("IN BED");
  }

  @Test
  public void testDuration() {

    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field durationField = fb.duration("duration", Duration.ofDays(1));

    assertThat(durationField.toString()).isEqualTo("1 day");
    assertThatJson(durationField).inPath("$.duration").asString().isEqualTo("PT24H");
  }

  @Test
  public void testInstant() {
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Field instantField = fb.instant("instant", Instant.ofEpochMilli(0));

    assertThatJson(toJson(instantField))
        .inPath("$.instant.@type")
        .asString()
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }

  @Test
  public void testInstantInArray() {
    SimpleFieldVisitor instantVisitor = new InstantFieldVisitor();

    MyFieldBuilder fb = MyFieldBuilder.instance();
    Instant[] instants = {Instant.ofEpochMilli(0)};
    Field instantArrayField =
        fb.array("instantArray", Value.array(i -> Value.string(i.toString()), instants))
            .withStructuredFormat(instantVisitor);

    var objectNode = toJson(instantArrayField);
    System.out.println(objectNode);
    var element = objectNode.get("instantArray").get(0);
    assertThatJson(element)
        .inPath("$.@type")
        .asString()
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }

  @Test
  public void testInstantInObject() {
    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field dateRange =
        fb.object(
            "dateRange",
            fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time"),
            fb.instant("endTime", Instant.ofEpochMilli(0)).withDisplayName("end time"));

    var objectNode = toJson(dateRange);
    assertThatJson(objectNode)
        .inPath("$.dateRange.startTime.@type")
        .asString()
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }

  @Test
  public void testInstantInObjectInArray() {
    MyFieldBuilder fb = MyFieldBuilder.instance();

    Value.ObjectValue obj =
        Value.object(
            fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time"),
            fb.instant("endTime", Instant.ofEpochMilli(0)).withDisplayName("end time"));
    Field array = fb.array("arrayOfDateRanges", obj);

    var objectNode = toJson(array);
    assertThatJson(objectNode)
        .inPath("$.arrayOfDateRanges[0].startTime.@type")
        .asString()
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }

  @Test
  public void testArrayOfArrayOfArrayOfInstant() {
    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field instant = fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time");
    Field array3 = fb.array("array3", Value.array(Value.array(Value.array(Value.object(instant)))));

    var objectNode = toJson(array3);
    assertThatJson(objectNode)
        .inPath("$.array3[0][0][0].startTime.@type")
        .asString()
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }

  @Test
  public void testArrayObjectArrayObjectInstant() {
    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field instant = fb.instant("startTime", Instant.ofEpochMilli(0)).withDisplayName("start time");
    Field array1 =
        fb.array("array1", Value.array(Value.object(fb.array("array2", Value.object(instant)))));

    var objectNode = toJson(array1);
    assertThatJson(objectNode)
        .inPath("$.array1[0].array2[0].startTime.@type")
        .asString()
        .isEqualTo("http://www.w3.org/2001/XMLSchema#dateTime");
  }

  static class MyFieldBuilder implements PresentationFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder();
    }

    public PresentationField instant(String name, Instant instant) {
      return string(name, instant.toString()).withStructuredFormat(new InstantFieldVisitor());
    }

    public PresentationField typedInstant(String name, Value<String> v) {
      return object(name, typedInstantValue(v));
    }

    Value.ObjectValue typedInstantValue(Value<String> v) {
      return Value.object(
          string("@type", "http://www.w3.org/2001/XMLSchema#dateTime"), keyValue("@value", v));
    }

    public PresentationField duration(String name, Duration duration) {
      Field structuredField = string(name, duration.toString());
      return string(name, duration.toDays() + " day")
          .asValueOnly()
          .withStructuredFormat(
              new SimpleFieldVisitor() {
                @Override
                public @NotNull Field visitString(@NotNull Value<String> stringValue) {
                  return structuredField;
                }
              });
    }
  }

  static class InstantFieldVisitor extends SimpleFieldVisitor {
    private static final MyFieldBuilder fb = MyFieldBuilder.instance();

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

    class InstantArrayVisitor extends SimpleArrayVisitor {
      @Override
      public void visitStringElement(Value.StringValue stringValue) {
        this.elements.add(mapInstant(stringValue));
      }
    }
  }
}
