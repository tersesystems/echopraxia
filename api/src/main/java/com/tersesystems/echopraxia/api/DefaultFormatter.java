package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DefaultFormatter implements Formatter {

  @Override
  @NotNull
  public String formatField(@NotNull Field field) {
    if (isValueOnly(field)) {
      return formatValue(field.value());
    } else {
      StringBuilder builder = new StringBuilder();
      formatName(builder, field.name());
      formatValue(builder, field.value());
      return builder.toString();
    }
  }

  @NotNull
  @Override
  public String formatValue(@NotNull Value<?> value) {
    if (value.type() == Value.Type.OBJECT) {
      StringBuilder b = new StringBuilder();
      formatObject(b, value.asObject());
      return b.toString();
    }
    // ArrayList renders elements with [] in toString, so we can just render toString!
    return String.valueOf(value.raw());
  }

  private void formatValue(@NotNull StringBuilder b, @NotNull Value<?> v) {
    if (v.type() == Value.Type.OBJECT) {
      formatObject(b, v.asObject());
    } else {
      b.append(v.raw());
    }
  }

  private void formatObject(@NotNull StringBuilder b, @NotNull Value.ObjectValue v) {
    // render an object with curly braces to distinguish from array.
    final List<Field> fieldList = v.raw();
    b.append("{");
    for (int i = 0; i < fieldList.size(); i++) {
      Field field = fieldList.get(i);
      if (!isValueOnly(field)) {
        formatName(b, field.name());
      }
      formatValue(b, field.value());
      if (i < fieldList.size() - 1) {
        b.append(", ");
      }
    }
    b.append("}");
  }

  private void formatName(@NotNull StringBuilder builder, @NotNull String name) {
    builder.append(name).append("=");
  }

  private boolean isValueOnly(Field field) {
    return field.attributes().getOptional(FieldAttributes.VALUE_ONLY).orElse(false);
  }
}
