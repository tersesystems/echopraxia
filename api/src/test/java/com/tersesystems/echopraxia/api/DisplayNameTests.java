package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.FieldAttributes.displayName;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DisplayNameTests {

  @Test
  public void testDisplayName() {
    Value<?> value = Value.string("derp");
    Field field = Field.keyValue("longArray", value).withAttribute(displayName("My Display Name"));
    assertThat(field.toString()).isEqualTo("\"My Display Name\"=derp");
  }
}
