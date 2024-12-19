package echopraxia.spi;

import static echopraxia.spi.PresentationHintAttributes.withDisplayName;
import static org.assertj.core.api.Assertions.assertThat;

import echopraxia.api.*;
import echopraxia.api.Attributes;
import echopraxia.api.Field;
import echopraxia.api.Value;
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
