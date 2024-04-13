package com.tersesystems.echopraxia.api;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.tersesystems.echopraxia.spi.PresentationHintAttributes.withToStringValue;
import static org.assertj.core.api.Assertions.assertThat;

public class ToStringValueTests {

    @Test
    public void testString() {
        Duration duration = Duration.ofDays(1);
        var value = Value.string(duration.toString()); // PT24H in line and JSON
        Attribute<String> stringAttribute = withToStringValue(formatDuration(duration));
        var durationWithToString = value.withAttributes(Attributes.create(stringAttribute)); // 1 day in toString()
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
