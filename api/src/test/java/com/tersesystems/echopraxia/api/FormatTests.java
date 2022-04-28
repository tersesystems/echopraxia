package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.Field.Value.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FormatTests {

  @Test
  public void testNull() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.nullField("foo");
    assertThat(f.toString()).isEqualTo("null");
  }

  @Test
  public void testString() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.string("foo", "bar");

    assertThat(f.toString()).isEqualTo("bar");
  }

  @Test
  public void testNumber() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.number("foo", 1);
    assertThat(f.toString()).isEqualTo("1");
  }

  @Test
  public void testBoolean() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.bool("foo", true);
    assertThat(f.toString()).isEqualTo("true");
  }

  @Test
  public void testArrayOfString() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.array("foo", "one", "two", "three");
    assertThat(f.toString()).isEqualTo("foo=[one, two, three]");
  }

  @Test
  public void testArrayOfNumber() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.array("foo", 1, 2, 3);
    assertThat(f.toString()).isEqualTo("foo=[1, 2, 3]");
  }

  @Test
  public void testArrayOfBoolean() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.array("foo", false, true, false);
    assertThat(f.toString()).isEqualTo("foo=[false, true, false]");
  }

  @Test
  public void testArrayOfNull() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f = fb.array("foo", Field.Value.array(nullValue(), nullValue(), nullValue()));
    assertThat(f.toString()).isEqualTo("foo=[null, null, null]");
  }

  @Test
  public void testObject() {
    final FieldBuilder fb = FieldBuilder.instance();
    final Field f =
        fb.object(
            "foo",
            object(
                fb.string("stringName", "value"),
                fb.number("numName", 43),
                fb.bool("boolName", true),
                fb.array("arrayName", array(Field.Value.string("a"), Field.Value.nullValue())),
                fb.nullField("nullName")));
    assertThat(f.toString()).isEqualTo("foo={value, 43, true, arrayName=[a, null], null}");
  }
}
