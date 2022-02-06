package com.tersesystems.echopraxia;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import org.jetbrains.annotations.NotNull;

// Some package private constants
class Constants {

  public static final String ECHOPRAXIA_UNKNOWN = "echopraxia-unknown-";

  private static final int DEFAULT_STRING_BUILDER_SIZE = 255;

  public static final LongAdder unknownFieldAdder = new LongAdder();

  private static final Field.Builder builderInstance = new Field.Builder() {};

  // Cut down on allocation pressure by reusing stringbuilder
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<>();

  private Constants() {}

  static Field.Builder builder() {
    return builderInstance;
  }

  // keep this package private for now
  interface FormatToBuffer {
    void formatToBuffer(StringBuilder b);
  }

  /** This is a field that prints out value to a message template if possible. */
  static final class DefaultValueField implements ValueField, FormatToBuffer {
    private final String name;
    private final Value<?> value;

    public DefaultValueField(String name, Value<?> value) {
      this.name = requireName(name);
      this.value = requireValue(value);
    }

    @Override
    public @NotNull String name() {
      return name;
    }

    @Override
    public @NotNull Value<?> value() {
      return value;
    }

    public String toString() {
      final StringBuilder builder = getThreadLocalStringBuilder();
      formatToBuffer(builder);
      return builder.toString();
    }

    public void formatToBuffer(StringBuilder b) {
      // Render value only here
      ValueFormatter.formatToBuffer(b, value);
    }
  }

  /** This is a field that prints out key=value to a message template if possible. */
  static final class DefaultKeyValueField implements KeyValueField, FormatToBuffer {
    private final String name;
    private final Value<?> value;

    public DefaultKeyValueField(String name, Value<?> value) {
      this.name = requireName(name);
      this.value = requireValue(value);
    }

    @Override
    public @NotNull String name() {
      return name;
    }

    @Override
    public @NotNull Value<?> value() {
      return value;
    }

    public String toString() {
      final StringBuilder builder = getThreadLocalStringBuilder();
      // Render key=value here
      formatToBuffer(builder);
      return builder.toString();
    }

    public void formatToBuffer(StringBuilder b) {
      b.append(name).append("=");
      ValueFormatter.formatToBuffer(b, value);
    }
  }

  // construct a field name so that json is happy and keep going.
  private static String requireName(String name) {
    if (name != null) {
      return name;
    }
    unknownFieldAdder.increment();
    return ECHOPRAXIA_UNKNOWN + unknownFieldAdder.longValue();
  }

  private static Field.Value<?> requireValue(Field.Value<?> value) {
    if (value != null) {
      return value;
    }
    return Field.Value.nullValue();
  }

  private static StringBuilder getThreadLocalStringBuilder() {
    StringBuilder buffer = threadLocalStringBuilder.get();
    if (buffer == null) {
      buffer = new StringBuilder(DEFAULT_STRING_BUILDER_SIZE);
      threadLocalStringBuilder.set(buffer);
    }
    buffer.setLength(0);
    return buffer;
  }

  static class ValueFormatter {

    static void formatToBuffer(StringBuilder b, Field.Value<?> v) {
      final Object raw = v.raw();
      if (raw == null) { // if null value or a raw value was set to null, keep going.
        b.append("null");
      } else if (v.type() == Field.Value.ValueType.OBJECT) {
        // render an object with curly braces to distinguish from array.
        final List<Field> fieldList = ((Field.Value.ObjectValue) v).raw();
        b.append("{");
        for (int i = 0; i < fieldList.size(); i++) {
          Field field = fieldList.get(i);
          ((FormatToBuffer) field).formatToBuffer(b);
          if (i < fieldList.size() - 1) {
            b.append(", ");
          }
        }
        b.append("}");
      } else {
        b.append(raw);
      }
    }
  }
}
