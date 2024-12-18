package echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AbbreviateAfterTests {

  @Test
  void testStringWithAbbreviateAfter() {
    var array = Value.string("123456789");
    Attribute<Integer> afterTwo = PresentationHintAttributes.abbreviateAfter(2);
    var abbrValue = array.withAttributes(Attributes.create(afterTwo));
    assertThat(abbrValue).hasToString("12...");
  }

  @Test
  void testArrayWithAbbreviateAfter() {
    var array = Value.array("one", "two", "three");
    Attribute<Integer> afterTwo = PresentationHintAttributes.abbreviateAfter(2);
    var abbrValue = array.withAttributes(Attributes.create(afterTwo));
    assertThat(abbrValue).hasToString("[one, two...]");
  }
}
