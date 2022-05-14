package com.tersesystems.echopraxia.api;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import org.jetbrains.annotations.NotNull;

/** Internals class, super not public. */
class Internals {

  public static final String ECHOPRAXIA_UNKNOWN = "echopraxia-unknown-";

  private static final int DEFAULT_STRING_BUILDER_SIZE = 255;

  public static final LongAdder unknownFieldAdder = new LongAdder();

  // Cut down on allocation pressure by reusing stringbuilder
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<>();

  private Internals() {}

  // keep this package private for now
  interface FormatToBuffer {
    void formatToBuffer(StringBuilder b);
  }

  /** This is a field that prints out value to a message template if possible. */
  static final class DefaultValueField implements Field.ValueField, FormatToBuffer {
    private final String name;
    private final Value<?> value;

    DefaultValueField(String name, Value<?> value) {
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
      final Object raw = value.raw();
      final Value.Type type = value.type();
      if (raw == null || type == Value.Type.NULL) {
        return "null";
      }
      if (type == Value.Type.STRING) {
        return ((String) raw);
      }

      if (type == Value.Type.BOOLEAN) {
        return ((Boolean) raw) ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
      }

      if (type == Value.Type.NUMBER) {
        return raw.toString();
      }

      final StringBuilder builder = getThreadLocalStringBuilder();
      formatToBuffer(builder);
      return builder.toString();
    }

    public void formatToBuffer(StringBuilder b) {
      // Render value only here
      ValueFormatter.formatToBuffer(b, value);
    }

    @Override
    @NotNull
    public List<Field> fields() {
      return Collections.singletonList(this);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Field)) return false;

      // key/value fields are comparable against value fields.
      Field that = (Field) o;

      if (!Objects.equals(name, that.name())) return false;
      return Objects.equals(value, that.value());
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
    }
  }

  /** This is a field that prints out key=value to a message template if possible. */
  static final class DefaultKeyValueField implements Field.KeyValueField, FormatToBuffer {
    private final String name;
    private final Value<?> value;

    DefaultKeyValueField(String name, Value<?> value) {
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

    @Override
    @NotNull
    public List<Field> fields() {
      return Collections.singletonList(this);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Field)) return false;

      // key/value fields are comparable against value fields.
      Field that = (Field) o;

      if (!Objects.equals(name, that.name())) return false;
      return Objects.equals(value, that.value());
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
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

  private static Value<?> requireValue(Value<?> value) {
    if (value != null) {
      return value;
    }
    return Value.nullValue();
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

    static void formatToBuffer(StringBuilder b, Value<?> v) {
      final Object raw = v.raw();
      if (raw == null) { // if null value or a raw value was set to null, keep going.
        b.append("null");
      } else if (v.type() == Value.Type.OBJECT) {
        // render an object with curly braces to distinguish from array.
        final List<Field> fieldList = ((Value.ObjectValue) v).raw();
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
