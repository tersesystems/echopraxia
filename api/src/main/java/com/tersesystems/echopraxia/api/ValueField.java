package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * Marker interface for key values.
 *
 * <p>Indicates that the plain value should be rendered in message template.
 *
 * <p>This marker interface is used internally.
 */
public interface ValueField extends Field {

  @NotNull
  static ValueField create(@NotNull String name, @NotNull Field.Value<?> value) {
    return new Internals.DefaultValueField(name, value);
  }
}
