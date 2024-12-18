package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.model.PresentationHintAttributes.asCardinal;
import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.model.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class CardinalTests {

  @Test
  public void testCardinalArray() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field =
        Field.keyValue("longArray", value, DefaultField.class).withAttribute(asCardinal());
    assertThat(field).hasToString("longArray=|9|");
  }

  @Test
  public void testCardinalArrayWithExtended() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.keyValue("longArray", value, DefaultField.class).asCardinal();
    assertThat(field).hasToString("longArray=|9|");
  }

  @Test
  public void testCardinalArrayWithValueOnly() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.value("longArray", value, DefaultField.class).withAttribute(asCardinal());
    assertThat(field).hasToString("|9|");
  }

  @Test
  public void testCardinalString() {
    String generatedString = UUID.randomUUID().toString();
    Value<?> value = Value.string(generatedString);
    Field field =
        Field.keyValue("longString", value, DefaultField.class).withAttribute(asCardinal());
    assertThat(field).hasToString("longString=|36|");
  }

  @Test
  public void testCardinalStringWithValueOnly() {
    String generatedString = UUID.randomUUID().toString();
    Value<?> value = Value.string(generatedString);
    Field field = Field.value("longString", value, DefaultField.class).withAttribute(asCardinal());
    assertThat(field).hasToString("|36|");
  }

  @Test
  void testStringWithAsCardinal() {
    var string = Value.string("foo");
    var asCardinal =
        string.withAttributes(Attributes.create(PresentationHintAttributes.asCardinal()));
    assertThat(asCardinal).hasToString("|3|");
  }

  @Test
  void testArrayWithAsCardinal() {
    var array = Value.array("one", "two", "three");
    var asCardinal =
        array.withAttributes(Attributes.create(PresentationHintAttributes.asCardinal()));
    assertThat(asCardinal).hasToString("|3|");
  }
}
