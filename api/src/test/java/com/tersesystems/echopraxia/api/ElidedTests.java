package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.Field.keyValue;
import static com.tersesystems.echopraxia.api.Value.string;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ElidedTests {

  @Test
  public void elideField() {
    Field elided = keyValue("foo", string("bar"), DefaultField.class).asElided();
    assertThat(elided.toString()).isEmpty();
  }

  @Test
  public void elideChildOfObject() {
    Field notElided = keyValue("first", string("bar"), DefaultField.class);
    Field elided = keyValue("second", string("bar"), DefaultField.class).asElided();
    List<Field> fields = List.of(notElided, elided);
    Field object = keyValue("object", Value.object(fields), DefaultField.class);
    assertThat(object.toString()).isEqualTo("object={first=bar}");
  }

  @Test
  public void elideFirstAndLast() {
    Field first = keyValue("first", string("bar"), DefaultField.class).asElided();
    Field second = keyValue("second", string("bar"), DefaultField.class);
    Field third = keyValue("third", string("bar"), DefaultField.class).asElided();
    List<Field> fields = List.of(first, second, third);
    Field object = keyValue("object", Value.object(fields), DefaultField.class);
    assertThat(object.toString()).isEqualTo("object={second=bar}");
  }

  @Test
  public void elideMiddle() {
    Field first = keyValue("first", string("bar"), DefaultField.class);
    Field second = keyValue("second", string("bar"), DefaultField.class).asElided();
    Field third = keyValue("third", string("bar"), DefaultField.class);
    List<Field> fields = List.of(first, second, third);
    Field object = keyValue("object", Value.object(fields), DefaultField.class);
    assertThat(object.toString()).isEqualTo("object={first=bar, third=bar}");
  }

  @Test
  public void elideFirstAndSecond() {
    Field first = keyValue("first", string("bar"), DefaultField.class).asElided();
    Field second = keyValue("second", string("bar"), DefaultField.class).asElided();
    Field third = keyValue("third", string("bar"), DefaultField.class);
    List<Field> fields = List.of(first, second, third);
    Field object = keyValue("object", Value.object(fields), DefaultField.class);
    assertThat(object.toString()).isEqualTo("object={third=bar}");
  }

  @Test
  public void elideSecondAndThird() {
    Field first = keyValue("first", string("bar"), DefaultField.class);
    Field second = keyValue("second", string("bar"), DefaultField.class).asElided();
    Field third = keyValue("third", string("bar"), DefaultField.class).asElided();
    List<Field> fields = List.of(first, second, third);
    Field object = keyValue("object", Value.object(fields), DefaultField.class);
    assertThat(object.toString()).isEqualTo("object={first=bar}");
  }

  @Test
  public void elideAll() {
    Field first = keyValue("first", string("bar"), DefaultField.class).asElided();
    Field second = keyValue("second", string("bar"), DefaultField.class).asElided();
    Field third = keyValue("third", string("bar"), DefaultField.class).asElided();
    List<Field> fields = List.of(first, second, third);
    Field object = keyValue("object", Value.object(fields), DefaultField.class);
    assertThat(object.toString()).isEqualTo("object={}");
  }
}
