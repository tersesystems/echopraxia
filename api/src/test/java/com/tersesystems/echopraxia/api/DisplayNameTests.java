package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.PresentationHints.withDisplayName;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DisplayNameTests {

  @Test
  public void testDisplayName() {
    Value<?> value = Value.string("derp");
    Field field =
        Field.keyValue("longArray", value).withAttribute(withDisplayName("My Display Name"));
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
