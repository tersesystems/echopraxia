package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.model.Attribute;
import com.tersesystems.echopraxia.model.Attributes;
import com.tersesystems.echopraxia.model.PresentationHintAttributes;
import com.tersesystems.echopraxia.model.Value;
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
