package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.FieldAttributes.asCardinal;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class CardinalTests {

  @Test
  public void testCardinalArray() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.keyValue("longArray", value).withAttribute(asCardinal());
    assertThat(field.toString()).isEqualTo("longArray=|9|");
  }

  @Test
  public void testCardinalArrayWithExtended() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.keyValue("longArray", value, ExtendedField.class).asCardinal();
    assertThat(field.toString()).isEqualTo("longArray=|9|");
  }

  @Test
  public void testCardinalArrayWithValueOnly() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.value("longArray", value).withAttribute(asCardinal());
    assertThat(field.toString()).isEqualTo("|9|");
  }

  @Test
  public void testCardinalString() {
    String generatedString = UUID.randomUUID().toString();
    Value<?> value = Value.string(generatedString);
    Field field = Field.keyValue("longString", value).withAttribute(asCardinal());
    assertThat(field.toString()).isEqualTo("longString=|36|");
  }

  @Test
  public void testCardinalStringWithValueOnly() {
    String generatedString = UUID.randomUUID().toString();
    Value<?> value = Value.string(generatedString);
    Field field = Field.value("longString", value).withAttribute(asCardinal());
    assertThat(field.toString()).isEqualTo("|36|");
  }
}
