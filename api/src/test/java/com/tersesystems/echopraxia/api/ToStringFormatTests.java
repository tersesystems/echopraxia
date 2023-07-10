package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
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

  static class MyFieldBuilder implements PresentationFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder();
    }

    public PresentationField duration(String name, Duration duration) {
      return string(name, duration.toString())
          .asValueOnly()
          .withToStringFormat(
              new SimpleFieldVisitor() {
                @Override
                public @NotNull Field visitString(@NotNull Value<String> stringValue) {
                  return string(name, duration.toDays() + " day");
                }
              });
    }
  }
}
