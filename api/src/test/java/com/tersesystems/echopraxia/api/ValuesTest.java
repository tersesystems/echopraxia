package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ValuesTest {

  // two string values are equal
  @Test
  void testStringValue() {
    Value<String> foo1 = Value.string("foo");
    Value<String> foo2 = Value.string("foo");

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueInteger() {
    Value<Number> foo1 = Value.number(1);
    Value<Number> foo2 = Value.number(1);

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueLong() {
    Value<Number> foo1 = Value.number(1L);
    Value<Number> foo2 = Value.number(1L);

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueFloat() {
    Value<Number> foo1 = Value.number(1.5);
    Value<Number> foo2 = Value.number(1.5);

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueLongVsInt() {
    Value<Number> foo1 = Value.number(1);
    Value<Number> foo2 = Value.number(1L);

    // https://stackoverflow.com/questions/3214448/comparing-numbers-in-java
    // https://stackoverflow.com/questions/2683202/comparing-the-values-of-two-generic-numbers
    // numbers are not generally comparable
    assertThat(foo1).isNotEqualTo(foo2);
  }

  @Test
  void testNull() {
    assertThat(Value.nullValue()).isEqualTo(Value.nullValue());
  }

  @Test
  void testArray() {
    Value<List<Value<?>>> array1 = Value.array(1, 2, 3);
    Value<List<Value<?>>> array2 = Value.array(1, 2, 3);

    assertThat(array1).isEqualTo(array2);
  }

  @Test
  void testArrayWithDifferentElements() {
    Value<List<Value<?>>> array1 = Value.array(1, 2, 3);
    Value<List<Value<?>>> array2 = Value.array(1, 2, 4);

    assertThat(array1).isNotEqualTo(array2);
  }

  @Test
  void testArrayWithObjects() {
    Field name = Field.keyValue("name", Value.string("foo"));
    Value<List<Value<?>>> array1 = Value.array(Value.object(name));
    Value<List<Value<?>>> array2 = Value.array(Value.object(name));

    assertThat(array1).isEqualTo(array2);
  }

  @Test
  void testObjectWithSameField() {
    Field name = Field.keyValue("name", Value.string("foo"));
    Value<?> o1 = Value.object(name);
    Value<?> o2 = Value.object(name);

    assertThat(o1).isEqualTo(o2);
  }

  @Test
  void testObjectWithEqualFields() {
    Field name1 = Field.keyValue("name", Value.string("foo"));
    Field name2 = Field.keyValue("name", Value.string("foo"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isEqualTo(o2);
  }

  @Test
  void testObjectWithEqualFieldsWithDiffPresentation() {
    Field name1 = Field.keyValue("name", Value.string("foo"));
    Field name2 = Field.value("name", Value.string("foo"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isEqualTo(o2);
  }
}
