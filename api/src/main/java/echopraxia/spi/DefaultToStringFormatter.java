package echopraxia.spi;

import static echopraxia.spi.PresentationHintAttributes.*;

import echopraxia.api.*;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultToStringFormatter implements ToStringFormatter {

  private static final DefaultToStringFormatter INSTANCE = new DefaultToStringFormatter();

  public static DefaultToStringFormatter getInstance() {
    return INSTANCE;
  }

  @Override
  @NotNull
  public String formatField(@NotNull Field field) {
    Attributes attributes = field.attributes();
    if (isElided(attributes)) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    if (!isValueOnly(attributes)) {
      formatName(builder, field.name(), attributes);
    }

    if (isToStringFormat(attributes)) {
      FieldVisitor fieldVisitor = getToStringFormat(attributes);
      assert fieldVisitor != null;
      field = fieldVisitor.visit(field);
    }

    Attributes valueAttributes =
        collectValueAttributes(field.attributes(), field.value().attributes());
    formatValue(builder, field.value(), valueAttributes);
    return builder.toString();
  }

  // if the field has elided, tostringvalue, abbreviateafter, or ascardinal,
  // it should apply to the value instead.
  Attributes collectValueAttributes(Attributes fa, Attributes valueAttributes) {
    if (fa.containsKey(ABBREVIATE_AFTER)) {
      return valueAttributes.plus(abbreviateAfter(fa.get(ABBREVIATE_AFTER)));
    } else if (fa.containsKey(AS_CARDINAL)) {
      return valueAttributes.plus(asCardinal());
    } else if (fa.containsKey(TOSTRING_VALUE)) {
      return valueAttributes.plus(withToStringValue(fa.get(TOSTRING_VALUE)));
    } else {
      return valueAttributes;
    }
  }

  @NotNull
  @Override
  public String formatValue(@NotNull Value<?> value) {
    Attributes attributes = value.attributes();
    if (isElided(attributes)) {
      return "";
    }

    if (isToStringValue(attributes)) {
      return getToStringValue(attributes);
    }

    if (value.type() == Value.Type.OBJECT) {
      StringBuilder b = new StringBuilder();
      formatObject(b, value.asObject());
      return b.toString();
    } else if (attributes == Attributes.empty()) {
      // ArrayList renders elements with [] in toString, so we can just render toString!
      return String.valueOf(value.raw());
    } else {
      StringBuilder b = new StringBuilder();
      formatValue(b, value, attributes);
      return b.toString();
    }
  }

  private void formatValue(
      @NotNull StringBuilder b, @NotNull Value<?> v, @NotNull Attributes attributes) {
    if (isElided(attributes)) {
      return;
    }

    if (isToStringValue(attributes)) {
      String toStringValue = getToStringValue(attributes);
      b.append(toStringValue);
      return;
    }

    if (v.type() == Value.Type.OBJECT) {
      formatObject(b, v.asObject());
    } else {
      // asCardinal takes priority over abbreviateAfter
      if (isAsCardinal(attributes) && v.type() == Value.Type.ARRAY) {
        b.append("|").append(v.asArray().raw().size()).append("|");
      } else if (isAsCardinal(attributes) && v.type() == Value.Type.STRING) {
        b.append("|").append(v.asString().raw().length()).append("|");
      } else if (isAbbreviateAfter(attributes)) {
        String abbreviated = abbreviateValue(v, getAbbreviateAfter(attributes));
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
    boolean displayComma = false;
    for (int i = 0; i < fieldList.size(); i++) {
      Field field = fieldList.get(i);
      Attributes attributes = field.attributes();
      if (!isElided(attributes)) {
        if (displayComma) {
          b.append(", ");
        }
        if (!isValueOnly(attributes)) {
          formatName(b, field.name(), attributes);
        }

        if (isToStringFormat(attributes)) {
          FieldVisitor fieldVisitor = getToStringFormat(attributes);
          assert fieldVisitor != null;
          field = fieldVisitor.visit(field);
        }
        formatValue(b, field.value(), field.value().attributes());
        displayComma = i < fieldList.size();
      }
    }
    b.append("}");
  }

  private void formatName(
      @NotNull StringBuilder builder, @NotNull String name, @NotNull Attributes attributes) {
    if (isDisplayName(attributes)) {
      String displayName = getDisplayName(attributes);
      builder.append("\"").append(displayName).append("\"");
    } else {
      builder.append(name);
    }
    builder.append("=");
  }

  private static boolean isToStringFormat(Attributes attributes) {
    return attributes.containsKey(TOSTRING_FORMAT);
  }

  private static @Nullable FieldVisitor getToStringFormat(Attributes attributes) {
    return attributes.get(TOSTRING_FORMAT);
  }

  private static boolean isDisplayName(@NotNull Attributes attributes) {
    return attributes.containsKey(PresentationHintAttributes.DISPLAY_NAME);
  }

  private static @Nullable String getDisplayName(@NotNull Attributes attributes) {
    return attributes.get(PresentationHintAttributes.DISPLAY_NAME);
  }

  private boolean isAbbreviateAfter(@NotNull Attributes attributes) {
    return attributes.containsKey(ABBREVIATE_AFTER);
  }

  private static @Nullable Integer getAbbreviateAfter(@NotNull Attributes attributes) {
    return attributes.get(ABBREVIATE_AFTER);
  }

  private boolean isAsCardinal(@NotNull Attributes attributes) {
    return attributes.containsKey(AS_CARDINAL);
  }

  private boolean isValueOnly(Attributes attributes) {
    return attributes.containsKey(PresentationHintAttributes.VALUE_ONLY);
  }

  private boolean isElided(Attributes attributes) {
    return attributes.containsKey(PresentationHintAttributes.ELIDE);
  }

  private boolean isToStringValue(Attributes attributes) {
    return attributes.containsKey(TOSTRING_VALUE);
  }

  private String getToStringValue(Attributes attributes) {
    return attributes.get(TOSTRING_VALUE);
  }
}
