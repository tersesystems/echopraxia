package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.spi.DefaultField;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EqualityTests {

  // two string values are equal
  @Test
  void testStringValue() {
    Value<String> foo1 = Value.string("foo");
    Value<String> foo2 = Value.string("foo");

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueInteger() {
    Value<Integer> foo1 = Value.number(1);
    Value<Integer> foo2 = Value.number(1);

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueLong() {
    Value<Long> foo1 = Value.number(1L);
    Value<Long> foo2 = Value.number(1L);

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberCompareToMoreThan() {
    Value.NumberValue<BigDecimal> foo1 = Value.number(BigDecimal.ONE);
    Value.NumberValue<BigDecimal> foo2 = Value.number(BigDecimal.ZERO);

    assertThat(foo1.compareTo(foo2)).isEqualTo(1);
  }

  @Test
  void testNumberCompareToLessThan() {
    Value.NumberValue<Long> foo1 = Value.number(0L);
    Value.NumberValue<Long> foo2 = Value.number(1L);

    assertThat(foo1.compareTo(foo2)).isEqualTo(-1);
  }

  @Test
  void testNumberValueFloat() {
    Value<Double> foo1 = Value.number(1.5);
    Value<Double> foo2 = Value.number(1.5);

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberBigDecimal() {
    Value<BigDecimal> foo1 = Value.number(new BigDecimal("132323428342342323423423421212312"));
    Value<BigDecimal> foo2 = Value.number(new BigDecimal("132323428342342323423423421212312"));

    assertThat(foo1).isEqualTo(foo2);
  }

  @Test
  void testNumberValueLongVsInt() {
    Value<Integer> foo1 = Value.number(1);
    Value<Long> foo2 = Value.number(1L);

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
    assertThat(o2).isEqualTo(o1);
  }

  @Test
  void testObjectWithEqualKeyValueFields() {
    Field name1 = Field.keyValue("name", Value.string("foo"));
    Field name2 = Field.keyValue("name", Value.string("foo"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isEqualTo(o2);
    assertThat(o2).isEqualTo(o1);
  }

  @Test
  void testObjectWithNotEqualKeyValueFields() {
    Field name1 = Field.keyValue("name", Value.string("foo"));
    Field name2 = Field.keyValue("name", Value.string("bar"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isNotEqualTo(o2);
    assertThat(o2).isNotEqualTo(o1);
  }

  @Test
  void testObjectWithNotEqualKeyNameFields() {
    Field name1 = Field.keyValue("name1", Value.string("foo"));
    Field name2 = Field.keyValue("name2", Value.string("foo"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isNotEqualTo(o2);
    assertThat(o2).isNotEqualTo(o1);
  }

  @Test
  void testObjectWithEqualValueFields() {
    Field name1 = Field.value("name", Value.string("foo"));
    Field name2 = Field.value("name", Value.string("foo"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isEqualTo(o2);
    assertThat(o2).isEqualTo(o1);
  }

  @Test
  void testObjectWithEqualFieldsWithDiffPresentation() {
    Field name1 = Field.keyValue("name", Value.string("foo"));
    Field name2 = Field.value("name", Value.string("foo"));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isNotEqualTo(o2);
  }

  @Test
  void testObjectWithEqualFieldsWithDiffAttributeValues() {
    AttributeKey<Integer> myKey = AttributeKey.create("myKey");

    Field name1 =
        Field.value("name", Value.string("foo"), DefaultField.class)
            .withAttribute(myKey.bindValue(2));
    Field name2 =
        Field.value("name", Value.string("foo"), DefaultField.class)
            .withAttribute(myKey.bindValue(1));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isNotEqualTo(o2);
  }

  @Test
  void testObjectWithEqualFieldsWithSameAttributeValues() {
    AttributeKey<Integer> myKey = AttributeKey.create("myKey");

    Field name1 =
        Field.value("name", Value.string("foo"), DefaultField.class)
            .withAttribute(myKey.bindValue(1));
    Field name2 =
        Field.value("name", Value.string("foo"), DefaultField.class)
            .withAttribute(myKey.bindValue(1));
    Value<?> o1 = Value.object(name1);
    Value<?> o2 = Value.object(name2);

    assertThat(o1).isEqualTo(o2);
  }
}
