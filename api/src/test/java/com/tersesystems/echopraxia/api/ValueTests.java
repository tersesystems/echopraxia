package com.tersesystems.echopraxia.api;

import org.junit.jupiter.api.Test;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

public class ValueTests {

  @Test
  void testObjectValueAdd() {
    Value.ObjectValue object = Value.object();
    Value.StringValue stringValue = Value.string("some string value");
    Field stringField = Field.value("string", stringValue);
    Value.ObjectValue objectPlus = object.add(stringField);
    assertThat(objectPlus.raw().size()).isEqualTo(1);
  }

  @Test
  void testObjectValueAddAll() {
    Value.ObjectValue object = Value.object();
    Value.StringValue stringValue = Value.string("some string value");
    Field stringField = Field.value("string", stringValue);
    Value.ObjectValue objectPlus = object.addAll(singleton(stringField));
    assertThat(objectPlus.raw().size()).isEqualTo(1);
  }

  @Test
  void testArrayValueAdd() {
    Value.StringValue stringValue = Value.string("some string value");
    Value.NumberValue<Integer> numberValue = Value.number(1);
    Value.ArrayValue arrayValue = Value.array(stringValue);

    Value.ArrayValue arrayPlus = arrayValue.add(numberValue);
    assertThat(arrayPlus.raw().size()).isEqualTo(2);
  }

  @Test
  void testArrayValueAddAll() {
    Value.StringValue stringValue = Value.string("some string value");
    Value.NumberValue<Integer> numberValue = Value.number(1);
    Value.ArrayValue arrayValue = Value.array(stringValue);

    Value.ArrayValue arrayPlus = arrayValue.addAll(singleton(numberValue));
    assertThat(arrayPlus.raw().size()).isEqualTo(2);
  }

}
