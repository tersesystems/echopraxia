package com.tersesystems.echopraxia.model;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * A typed attribute key. Can be used with a display name argument for better visibility.
 *
 * @param <A> The type of the attribute.
 * @since 3.0
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
