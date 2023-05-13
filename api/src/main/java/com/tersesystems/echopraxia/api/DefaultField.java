package com.tersesystems.echopraxia.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import org.jetbrains.annotations.NotNull;

public class DefaultField implements Field {

  public static final String ECHOPRAXIA_UNKNOWN = "echopraxia-unknown-";

  private static final int DEFAULT_STRING_BUILDER_SIZE = 255;

  public static final LongAdder unknownFieldAdder = new LongAdder();

  // Cut down on allocation pressure by reusing stringbuilder
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<>();

  protected final String name;
  protected final Value<?> value;
  protected final Attributes attributes;

  DefaultField(String name, Value<?> value, Attributes attributes) {
    this.name = requireName(name);
    this.value = requireValue(value);
    this.attributes = attributes;
  }

  @Override
  public <A> Field withAttribute(Attribute<A> attr) {
    return new DefaultField(name, value, attributes.plus(attr));
  }

  @Override
  public Field withAttributes(Attributes attrs) {
    return new DefaultField(name, value, attributes.plusAll(attrs));
  }

  @Override
  public <A> Field withoutAttribute(AttributeKey<A> key) {
    return new DefaultField(name, value, attributes.minus(key));
  }

  @Override
  public Field withoutAttributes(Collection<AttributeKey<?>> keys) {
    return new DefaultField(name, value, attributes.minusAll(keys));
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull Value<?> value() {
    return value;
  }

  @Override
  public @NotNull Attributes attributes() {
    return attributes;
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

  public String toString() {
    Boolean valueOnly = isValueOnly();
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
      builder.append(name).append("=");
    }
    ValueFormatter.formatToBuffer(builder, value);
    return builder.toString();
  }

  public void formatToBuffer(StringBuilder b) {
    Boolean valueOnly = isValueOnly();
    if (!valueOnly) {
      b.append(name).append("=");
    }
    ValueFormatter.formatToBuffer(b, value);
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
