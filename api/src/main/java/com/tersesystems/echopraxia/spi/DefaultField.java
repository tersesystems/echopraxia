package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.api.PresentationHintsAware;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * The default field implementation.
 *
 * @since 3.0
 */
public class DefaultField
    implements Field, PresentationHintsAware<DefaultField>, AttributesAware<DefaultField> {

  protected final String name;
  protected final Value<?> value;
  protected final Attributes attributes;

  protected DefaultField(
      @NotNull String name, @NotNull Value<?> value, @NotNull Attributes attributes) {
    this.name = Field.requireName(name);
    this.value = Field.requireValue(value);
    this.attributes = attributes;
  }

  @Override
  public @NotNull DefaultField asValueOnly() {
    return this.withAttribute(PresentationHints.asValueOnly());
  }

  @Override
  public @NotNull DefaultField abbreviateAfter(int after) {
    return this.withAttribute(PresentationHints.abbreviateAfter(after));
  }

  @Override
  public @NotNull DefaultField asCardinal() {
    return this.withAttribute(PresentationHints.asCardinal());
  }

  @Override
  public @NotNull DefaultField asElided() {
    return this.withAttribute(PresentationHints.asElided());
  }

  @Override
  public @NotNull DefaultField withDisplayName(@NotNull String displayName) {
    return this.withAttribute(PresentationHints.withDisplayName(displayName));
  }

  @Override
  public @NotNull DefaultField withStructuredFormat(@NotNull FieldVisitor fieldVisitor) {
    return this.withAttribute(PresentationHints.withStructuredFormat(fieldVisitor));
  }

  @Override
  public <A> @NotNull DefaultField withAttribute(@NotNull Attribute<A> attr) {
    return newAttributes(attributes.plus(attr));
  }

  @Override
  public @NotNull DefaultField withAttributes(@NotNull Attributes attrs) {
    return newAttributes(attributes.plusAll(attrs));
  }

  @Override
  public <A> @NotNull DefaultField withoutAttribute(@NotNull AttributeKey<A> key) {
    return newAttributes(attributes.minus(key));
  }

  @Override
  public @NotNull DefaultField withoutAttributes(@NotNull Collection<AttributeKey<?>> keys) {
    return newAttributes(attributes.minusAll(keys));
  }

  @Override
  public @NotNull DefaultField clearAttributes() {
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

  private @NotNull DefaultField newAttributes(@NotNull Attributes attrs) {
    return new DefaultField(name, value, attrs);
  }
}
