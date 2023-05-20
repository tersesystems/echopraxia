package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A typed attribute key.  Can be used with a display name argument for better visibility.
 *
 * @param <A> The type of the attribute.
 */
public final class AttributeKey<A> {

  private final String displayName;

  private AttributeKey() {
    this.displayName = null;
  }

  private AttributeKey(@NotNull String displayName) {
    this.displayName = displayName;
  }

  @NotNull
  public Attribute<A> bindValue(A value) {
    return new Attribute<>(this, value);
  }

  @NotNull
  public static <A> AttributeKey<A> create() {
    return new AttributeKey<>();
  }

  @NotNull
  public static <A> AttributeKey<A> create(@NotNull String displayName) {
    return new AttributeKey<>(displayName);
  }

  @Override
  public String toString() {
    return Optional.ofNullable(displayName).orElse(super.toString());
  }
}
