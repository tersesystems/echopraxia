package com.tersesystems.echopraxia.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ExtendedField implements Field {

  private final String name;
  private final Value<?> value;
  private final Attributes attributes;

  public ExtendedField(String name, Value<?> value, Attributes attributes) {
    this.name = Field.requireName(name);
    this.value = Field.requireValue(value);
    this.attributes = attributes;
  }

  public ExtendedField abbreviateAfter(int after) {
    return this.withAttribute(FieldAttributes.abbreviateAfter(after));
  }

  public ExtendedField asCardinal() {
    return this.withAttribute(FieldAttributes.asCardinal());
  }

  public ExtendedField withDisplayName(String displayName) {
    return this.withAttribute(FieldAttributes.displayName(displayName));
  }

  protected @NotNull ExtendedField newAttributes(@NotNull Attributes attrs) {
    return new ExtendedField(name, value, attrs);
  }

  @Override
  public <A> @NotNull ExtendedField withAttribute(@NotNull Attribute<A> attr) {
    return newAttributes(attributes.plus(attr));
  }

  @Override
  public @NotNull ExtendedField withAttributes(@NotNull Attributes attrs) {
    return newAttributes(attributes.plusAll(attrs));
  }

  @Override
  public <A> @NotNull ExtendedField withoutAttribute(@NotNull AttributeKey<A> key) {
    return newAttributes(attributes.minus(key));
  }

  @Override
  public @NotNull ExtendedField withoutAttributes(@NotNull Collection<AttributeKey<?>> keys) {
    return newAttributes(attributes.minusAll(keys));
  }

  @Override
  public @NotNull ExtendedField clearAttributes() {
    return newAttributes(Attributes.empty());
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
    ExtendedField that = (ExtendedField) o;
    return Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(attributes, that.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, attributes);
  }

  public String toString() {
    return EchopraxiaService.getInstance().getToStringFormatter().formatField(this);
  }
}
