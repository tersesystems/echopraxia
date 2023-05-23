package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DefaultFieldFormatter implements Formatter {

  @Override
  @NotNull
  public String formatField(@NotNull Field field) {
    if (isValueOnly(field)) {
      return formatValue(field.value());
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(field.name()).append("=");
      formatToBuffer(builder, field.value());
      return builder.toString();
    }
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

    final StringBuilder b = new StringBuilder();
    formatToBuffer(b, value);
    return b.toString();
  }

  private void formatToBuffer(StringBuilder b, Value<?> v) {
    final Object raw = v.raw();
    if (raw == null) { // if null value or a raw value was set to null, keep going.
      b.append("null");
    } else if (v.type() == Value.Type.OBJECT) {
      // render an object with curly braces to distinguish from array.
      final List<Field> fieldList = ((Value.ObjectValue) v).raw();
      b.append("{");
      for (int i = 0; i < fieldList.size(); i++) {
        Field field = fieldList.get(i);
        boolean valueOnly = isValueOnly(field);
        if (!valueOnly) {
          b.append(field.name()).append("=");
        }
        formatToBuffer(b, field.value());
        if (i < fieldList.size() - 1) {
          b.append(", ");
        }
      }
      b.append("}");
    } else {
      b.append(raw);
    }
  }

  private boolean isValueOnly(Field field) {
    return field.attributes().getOptional(FieldAttributes.VALUE_ONLY).orElse(false);
  }
}
