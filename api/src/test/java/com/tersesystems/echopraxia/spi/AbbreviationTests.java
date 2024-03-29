package com.tersesystems.echopraxia.spi;

import static com.tersesystems.echopraxia.spi.PresentationHintAttributes.abbreviateAfter;
import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import org.junit.jupiter.api.Test;

public class AbbreviationTests {

  @Test
  public void abbreviateString() {
    Value<String> value = Value.string("123456789");
    Field field =
        Field.keyValue("longString", value, DefaultField.class).withAttribute(abbreviateAfter(5));
    assertThat(field.toString()).isEqualTo("longString=12345...");
  }

  @Test
  public void abbreviateStringWithExtended() {
    Value<String> value = Value.string("123456789");
    DefaultField field = Field.keyValue("longString", value, DefaultField.class).abbreviateAfter(5);
    assertThat(field.toString()).isEqualTo("longString=12345...");
  }

  @Test
  public void abbreviateStringWithValueOnly() {
    Value<String> value = Value.string("123456789");
    Field field = Field.value("longString", value, DefaultField.class).abbreviateAfter(5);
    assertThat(field.toString()).isEqualTo("12345...");
  }

  @Test
  public void abbreviateShortString() {
    Value<String> value = Value.string("12345");
    Field field = Field.keyValue("longString", value, DefaultField.class).abbreviateAfter(5);
    assertThat(field.toString()).isEqualTo("longString=12345");
  }

  @Test
  public void abbreviateArray() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.keyValue("longArray", value, DefaultField.class).abbreviateAfter(5);
    assertThat(field.toString()).isEqualTo("longArray=[1, 2, 3, 4, 5...]");
  }

  @Test
  public void abbreviateArrayWithValueOnly() {
    Value<?> value = Value.array(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Field field = Field.value("longArray", value, DefaultField.class).abbreviateAfter(5);
    assertThat(field.toString()).isEqualTo("[1, 2, 3, 4, 5...]");
  }

  @Test
  public void abbreviateShortArray() {
    Value<?> value = Value.array(1, 2, 3, 4, 5);
    Field field = Field.keyValue("longArray", value, DefaultField.class).abbreviateAfter(5);
    assertThat(field.toString()).isEqualTo("longArray=[1, 2, 3, 4, 5]");
  }
}
