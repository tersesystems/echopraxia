package com.tersesystems.echopraxia;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.fake.FakeLoggingContext;
import java.util.*;
import org.junit.jupiter.api.Test;

public class ContextTests {

  @Test
  public void testContainsKeyMatch() {
    final Field.Builder builder = Field.Builder.instance();
    final FakeLoggingContext context = FakeLoggingContext.single(builder.string("foo", "bar"));

    assertThat(context.containsKey("foo")).isTrue();
  }

  @Test
  public void testContainsKeyNoMatch() {
    final Field.Builder builder = Field.Builder.instance();
    final FakeLoggingContext context = FakeLoggingContext.single(builder.string("notfoo", "bar"));

    assertThat(context.containsKey("foo")).isFalse();
  }

  @Test
  public void testIsEmpty() {
    final Field.Builder builder = Field.Builder.instance();
    final FakeLoggingContext context = FakeLoggingContext.empty();

    assertThat(context.isEmpty()).isTrue();
  }

  @Test
  public void testIsNotEmpty() {
    final Field.Builder builder = Field.Builder.instance();
    final FakeLoggingContext context = FakeLoggingContext.single(builder.string("foo", "bar"));

    assertThat(context.isEmpty()).isFalse();
  }

  @Test
  public void testGet() {
    final Field.Builder builder = Field.Builder.instance();
    final Field fooField = builder.string("foo", "value1");
    final Field barField = builder.string("bar", "value2");
    final FakeLoggingContext context = FakeLoggingContext.of(fooField, barField);

    assertThat(context.get("foo")).isEqualTo(fooField.value());
  }

  @Test
  public void testKeys() {
    final Field.Builder builder = Field.Builder.instance();
    final Field fooField = builder.string("foo", "value1");
    final Field barField = builder.string("bar", "value2");
    final FakeLoggingContext context = FakeLoggingContext.of(fooField, barField);

    final LinkedHashSet<Object> set = new LinkedHashSet<>();
    set.add("foo");
    set.add("bar");
    assertThat(context.keySet()).isEqualTo(set);
  }

  @Test
  public void testValues() {
    final Field.Builder builder = Field.Builder.instance();
    final Field fooField = builder.string("foo", "value1");
    final Field barField = builder.string("bar", "value2");
    final FakeLoggingContext context = FakeLoggingContext.of(fooField, barField);

    final List<Field.Value<?>> values = new LinkedList<>();
    values.add(fooField.value());
    values.add(barField.value());
    assertThat(context.values()).isEqualTo(values);
  }
}
