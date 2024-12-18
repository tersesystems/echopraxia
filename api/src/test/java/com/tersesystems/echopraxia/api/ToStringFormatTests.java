package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.model.Value.array;
import static com.tersesystems.echopraxia.model.Value.string;
import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.model.DefaultField;
import com.tersesystems.echopraxia.model.Field;
import com.tersesystems.echopraxia.model.PresentationField;
import com.tersesystems.echopraxia.model.Value;
import java.time.Duration;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class ToStringFormatTests {

  @Test
  public void testSimpleFormat() {
    MyFieldBuilder fb = MyFieldBuilder.instance();
    Duration duration = Duration.ofDays(1);
    Field field = fb.duration("duration", duration);
    assertThat(field.toString()).isEqualTo("duration=1 day");
    assertThat(field.value().asString().raw()).isEqualTo("PT24H");
  }

  @Test
  public void testNestedSimpleFormat() {
    MyFieldBuilder fb = MyFieldBuilder.instance();

    Field nameField = fb.string("name", "event name");
    Field durationField = fb.duration("duration", Duration.ofDays(1));
    Field eventField = fb.keyValue("event", Value.object(nameField, durationField));

    var s = eventField.toString();
    assertThat(s).isEqualTo("event={name=event name, duration=1 day}");
  }

  @Test
  public void testArraySimpleFormat() {
    MyFieldBuilder fb = MyFieldBuilder.instance();

    Duration[] durationsArray = {Duration.ofDays(1), Duration.ofDays(2)};
    var visitor =
        new SimpleFieldVisitor() {
          @Override
          public @NotNull ArrayVisitor visitArray() {
            return new SimpleArrayVisitor() {
              @Override
              public @NotNull Field done() {
                return new DefaultField(
                    name, array(f -> string(formatDuration(f)), durationsArray), attributes);
              }
            };
          }
        };

    var durationsField =
        fb.array("durations", array(f -> string(f.toString()), durationsArray))
            .withToStringFormat(visitor);
    var s = durationsField.toString();
    assertThat(s).isEqualTo("durations=[1 day, 2 days]");
  }

  static class MyFieldBuilder implements PresentationFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder();
    }

    public PresentationField duration(String name, Duration duration) {
      return string(name, duration.toString())
          .withToStringFormat(
              new SimpleFieldVisitor() {
                @Override
                public @NotNull Field visitString(@NotNull Value<String> stringValue) {
                  return string(name, formatDuration(duration));
                }
              });
    }
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
