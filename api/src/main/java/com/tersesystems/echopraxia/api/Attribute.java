package com.tersesystems.echopraxia.api;

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
}
