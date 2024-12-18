package com.tersesystems.echopraxia.spi;

import static com.tersesystems.echopraxia.spi.PresentationHintAttributes.withDisplayName;
import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.api.*;
import org.junit.jupiter.api.Test;

public class DisplayNameTests {

  @Test
  public void testDisplayName() {
    Value<?> value = Value.string("derp");
    Field field =
        new DefaultField("longArray", value, Attributes.create(withDisplayName("My Display Name")));
    assertThat(field.toString()).isEqualTo("\"My Display Name\"=derp");
  }

  @Test
  public void testDisplayNameWithExtended() {
    Value<?> value = Value.string("derp");
    Field field =
        Field.keyValue("longArray", value, DefaultField.class).withDisplayName("My Display Name");
    assertThat(field.toString()).isEqualTo("\"My Display Name\"=derp");
  }
}
