package com.tersesystems.echopraxia.api;

import java.util.Objects;

/**
 * A typed attribute with a key and value.
 *
 * @param <A> the type of the attribute value.
 * @since 3.0
 */
public final class Attribute<A> {
  private final AttributeKey<A> key;
  private final A value;

  public Attribute(AttributeKey<A> key, A value) {
    this.key = key;
    this.value = value;
  }

  public AttributeKey<A> key() {
    return key;
  }

  public A value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Attribute<?> attribute = (Attribute<?>) o;
    return Objects.equals(key, attribute.key) && Objects.equals(value, attribute.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    return "Attribute{" + "key=" + key + ", value=" + value + '}';
  }
}
