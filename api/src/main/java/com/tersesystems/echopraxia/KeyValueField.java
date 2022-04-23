package com.tersesystems.echopraxia;

import org.jetbrains.annotations.NotNull;

/**
 * Marker interface for key values.
 *
 * <p>Indicates that we want `key=value` in the message template.
 *
 * <p>This marker interface is used internally, but you typically won't need to use it directly. You
 * can call `Field.Builder.keyValue` to get a instance of a field with this.
 */
public interface KeyValueField extends Field {
  @NotNull
  static KeyValueField create(@NotNull String name, @NotNull Value<?> value) {
    return new Internals.DefaultKeyValueField(name, value);
  }
}
