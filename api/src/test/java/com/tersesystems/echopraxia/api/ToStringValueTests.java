package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.spi.PresentationHintAttributes.withToStringValue;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ToStringValueTests {

  @Test
  public void testString() {
    Duration duration = Duration.ofDays(1);
    var value = Value.string(duration.toString()); // PT24H in line and JSON
    Attribute<String> stringAttribute = withToStringValue(formatDuration(duration));
    var durationWithToString =
        value.withAttributes(Attributes.create(stringAttribute)); // 1 day in toString()
    assertThat(durationWithToString).hasToString("1 day");
  }

  @Test
  public void testArray() {
    var one = Value.string("one");
    var two = Value.string("two").withAttributes(Attributes.create(withToStringValue("dos")));
    var three = Value.string("three");
    var array = Value.array(one, two, three);
    assertThat(array).hasToString("[one, dos, three]");
  }

  @Test
  public void testObjectWithToStringValue() {
    var frame = new IllegalStateException().getStackTrace()[0];
    var stackObject =
        Value.object(
                Field.keyValue("line_number", Value.number(frame.getLineNumber())),
                Field.keyValue("method_name", Value.string(frame.getMethodName())),
                Field.keyValue("method_name", Value.string(frame.getFileName())))
            .withToStringValue(frame.getFileName());

    assertThat(stackObject).hasToString("ToStringValueTests.java");
  }

  @Test
  public void testArrayWithToStringValue() {
    var stackObject = Value.array("1", "2", "3").withToStringValue("herp derp");

    assertThat(stackObject).hasToString("herp derp");
  }

  private static String formatDuration(Duration duration) {
    List<String> parts = new ArrayList<>();
    long days = duration.toDaysPart();
    if (days > 0) {
      parts.add(plural(days, "day"));
    }
    return String.join(", ", parts);
  }

  private static String plural(long num, String unit) {
    return num + " " + unit + (num == 1 ? "" : "s");
  }
}
