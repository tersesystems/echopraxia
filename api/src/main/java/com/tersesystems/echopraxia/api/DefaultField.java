package com.tersesystems.echopraxia.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class DefaultField implements Field {

  private final String name;
  private final Value<?> value;
  private final Attributes attributes;

  public DefaultField(String name, Value<?> value, Attributes attributes) {
    this.name = Field.requireName(name);
    this.value = Field.requireValue(value);
    this.attributes = attributes;
  }

  @Override
  public <A> @NotNull Field withAttribute(@NotNull Attribute<A> attr) {
    return new DefaultField(name, value, attributes.plus(attr));
  }

  @Override
  public @NotNull Field withAttributes(@NotNull Attributes attrs) {
    return new DefaultField(name, value, attributes.plusAll(attrs));
  }

  @Override
  public <A> @NotNull Field withoutAttribute(@NotNull AttributeKey<A> key) {
    return new DefaultField(name, value, attributes.minus(key));
  }

  @Override
  public @NotNull Field withoutAttributes(@NotNull Collection<AttributeKey<?>> keys) {
    return new DefaultField(name, value, attributes.minusAll(keys));
  }

  @Override
  public @NotNull Field clearAttributes() {
    return new DefaultField(name, value, Attributes.empty());
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
    if (o == null || getClass() != o.getClass()) return false;
    DefaultField that = (DefaultField) o;
    return Objects.equals(name, that.name)
        && Objects.equals(value, that.value)
        && Objects.equals(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, attributes);
  }

  public String toString() {
    return EchopraxiaService.getInstance().getToStringFormatter().formatField(this);
  }
}
