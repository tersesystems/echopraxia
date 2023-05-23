package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.FieldAttributes.ABBREVIATE_AFTER;
import static com.tersesystems.echopraxia.api.FieldAttributes.AS_CARDINAL;

import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class DefaultToStringFormatter implements ToStringFormatter {

  @Override
  @NotNull
  public String formatField(@NotNull Field field) {
    if (isValueOnly(field)) {
      return formatValue(field.value());
    } else {
      StringBuilder builder = new StringBuilder();
      formatName(builder, field.name());
      formatValue(builder, field.value(), field.attributes());
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

  private void formatValue(
      @NotNull StringBuilder b, @NotNull Value<?> v, @NotNull Attributes attributes) {
    if (v.type() == Value.Type.OBJECT) {
      formatObject(b, v.asObject());
    } else {
      // asCardinal takes priority over abbreviateAfter
      if (attributes.containsKey(AS_CARDINAL) && v.type() == Value.Type.ARRAY) {
        b.append("|").append(v.asArray().raw().size()).append("|");
      } else if (attributes.containsKey(AS_CARDINAL) && v.type() == Value.Type.STRING) {
        b.append("|").append(v.asString().raw().length()).append("|");
      } else if (attributes.containsKey(ABBREVIATE_AFTER)) {
        String abbreviated = abbreviateValue(v, attributes.get(ABBREVIATE_AFTER));
        b.append(abbreviated);
      } else {
        b.append(v.raw());
      }
    }
  }

  private String abbreviateValue(Value<?> v, Integer maxWidth) {
    switch (v.type()) {
      case STRING:
        String s = v.asString().raw();
        if (s.length() > maxWidth) {
          // handle unicode codepoints
          int correctedMaxWidth =
              (Character.isLowSurrogate(s.charAt(maxWidth))) && maxWidth > 0
                  ? maxWidth - 1
                  : maxWidth;
          return s.substring(0, correctedMaxWidth) + "...";
        } else {
          return s;
        }
      case ARRAY:
        List<Value<?>> elements = v.asArray().raw();
        if (elements.size() > maxWidth) {
          String limited =
              elements.stream()
                  .limit(maxWidth)
                  .map(Value::toString)
                  .collect(Collectors.joining(", "));
          return "[" + limited + "...]";
        } else {
          return elements.toString();
        }
      default:
        return v.raw().toString();
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
      formatValue(b, field.value(), field.attributes());
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
