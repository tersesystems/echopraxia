package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.Value.optional;
import static com.tersesystems.echopraxia.api.Value.string;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ValueTests {

  @Test
  void testObjectValueAdd() {
    Value.ObjectValue object = Value.object();
    Value.StringValue stringValue = string("some string value");
    Field stringField = Field.value("string", stringValue);
    Value.ObjectValue objectPlus = object.add(stringField);
    assertThat(objectPlus.raw()).hasSize(1);
  }

  @Test
  void testObjectValueAddAll() {
    Value.ObjectValue object = Value.object();
    Value.StringValue stringValue = string("some string value");
    Field stringField = Field.value("string", stringValue);
    Value.ObjectValue objectPlus = object.addAll(singleton(stringField));
    assertThat(objectPlus.raw()).hasSize(1);
  }

  @Test
  void testArrayValueAdd() {
    Value.StringValue stringValue = string("some string value");
    Value.NumberValue<Integer> numberValue = Value.number(1);
    Value.ArrayValue arrayValue = Value.array(stringValue);

    Value.ArrayValue arrayPlus = arrayValue.add(numberValue);
    assertThat(arrayPlus.raw()).hasSize(2);
  }

  @Test
  void testArrayValueAddAll() {
    Value.StringValue stringValue = string("some string value");
    Value.NumberValue<Integer> numberValue = Value.number(1);
    Value.ArrayValue arrayValue = Value.array(stringValue);

    Value.ArrayValue arrayPlus = arrayValue.addAll(singleton(numberValue));
    assertThat(arrayPlus.raw()).hasSize(2);
  }

  @Test
  void testOptionalWithNull() {
    Value<?> optional = optional(null);
    assertThat(optional).isEqualTo(Value.nullValue());
  }

  @Test
  void testOptionalWithEmpty() {
    Value<?> optional = optional(Optional.empty());
    assertThat(optional).isEqualTo(Value.nullValue());
  }

  @Test
  void testOptionalWithSome() {
    Value<?> optional = optional(Optional.of(string("foo")));
    assertThat(optional).isEqualTo(string("foo"));
  }

  @Test
  void testOptionalMap() {
    Instant instant = Instant.ofEpochSecond(0);
    var v = optional(Optional.ofNullable(instant).map(i -> string(i.toString())));
    assertThat(v).isEqualTo(Value.string("1970-01-01T00:00:00Z"));
  }
}
