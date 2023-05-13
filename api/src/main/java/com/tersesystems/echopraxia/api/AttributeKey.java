package com.tersesystems.echopraxia.api;

import java.util.Optional;

public final class AttributeKey<A> {

  private final String displayName;

  private AttributeKey() {
    this.displayName = null;
  }

  private AttributeKey(String displayName) {
    this.displayName = displayName;
  }

  public Attribute<A> bindValue(A value) {
    return new Attribute<>(this, value);
  }

  public static <A> AttributeKey<A> create() {
    return new AttributeKey<>();
  }

  public static <A> AttributeKey<A> create(String displayName) {
    return new AttributeKey<>(displayName);
  }

  @Override
  public String toString() {
    return Optional.ofNullable(displayName).orElse(super.toString());
  }
}
