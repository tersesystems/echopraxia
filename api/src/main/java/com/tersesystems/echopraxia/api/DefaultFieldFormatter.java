package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DefaultFieldFormatter implements FieldFormatter {

  private static final int DEFAULT_STRING_BUILDER_SIZE = 255;

  // Cut down on allocation pressure by reusing stringbuilder
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<>();

  boolean isValueOnly(Field field) {
    return field.attributes().getOptional(FieldAttributes.VALUE_ONLY).orElse(false);
  }

  @Override
  @NotNull
  public String formatField(@NotNull Field field) {
    Boolean valueOnly = isValueOnly(field);
    Value<?> value = field.value();
    if (valueOnly) {
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
    }

    final StringBuilder builder = getThreadLocalStringBuilder();
    if (!valueOnly) {
      builder.append(field.name()).append("=");
    }
    formatToBuffer(builder, value);
    return builder.toString();
  }

  @Override
  public String formatValue(Value<?> value) {
    final Object raw = value.raw();
    final Value.Type type = value.type();
    if (raw == null || type == Value.Type.NULL) {
      // if null value or a raw value was set to null, keep going.
      return "null";
    }

    if (type == Value.Type.STRING) {
      return ((String) raw);
    }

    if (type == Value.Type.BOOLEAN) {
      return Boolean.toString((Boolean) raw);
    }

    if (type == Value.Type.NUMBER) {
      return raw.toString();
    }

    StringBuilder b = new StringBuilder();
    formatToBuffer(b, value);
    return b.toString();
  }

  void formatToBuffer(StringBuilder b, Value<?> v) {
    final Object raw = v.raw();
    if (raw == null) { // if null value or a raw value was set to null, keep going.
      b.append("null");
    } else if (v.type() == Value.Type.OBJECT) {
      // render an object with curly braces to distinguish from array.
      final List<Field> fieldList = ((Value.ObjectValue) v).raw();
      b.append("{");
      for (int i = 0; i < fieldList.size(); i++) {
        DefaultField field = (DefaultField) fieldList.get(i);
        formatToBuffer(b, field);
        if (i < fieldList.size() - 1) {
          b.append(", ");
        }
      }
      b.append("}");
    } else {
      b.append(raw);
    }
  }

  void formatToBuffer(StringBuilder b, Field field) {
    Boolean valueOnly = isValueOnly(field);
    if (!valueOnly) {
      b.append(field.name()).append("=");
    }
    formatToBuffer(b, field.value());
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
}
