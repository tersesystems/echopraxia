package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * The Field interface. This is a core part of structured data, and consists of a name and a Value,
 * where a value corresponds roughly to the JSON infoset: string, number, boolean, null, array, and
 * object.
 *
 * <p>The field builder interface and custom field builders go a long way to building up more
 * complex structures, please see documentation for how to use them.
 */
public interface Field extends FieldBuilderResult {

  String EXCEPTION = "exception";

  /**
   * The field name.
   *
   * @return the field name.
   */
  @NotNull
  String name();

  /**
   * The field value.
   *
   * @return the field value.
   */
  @NotNull
  Value<?> value();

  @NotNull
  static Field.ValueField value(@NotNull String name, @NotNull Value<?> value) {
    return new Internals.DefaultValueField(name, value);
  }

  @NotNull
  static Field.KeyValueField keyValue(@NotNull String name, @NotNull Value<?> value) {
    return new Internals.DefaultKeyValueField(name, value);
  }

  /**
   * Marker interface for key values.
   *
   * <p>Indicates that the plain value should be rendered in message template.
   *
   * <p>This marker interface is used internally.
   */
  interface ValueField extends Field {}

  /**
   * Marker interface for key values.
   *
   * <p>Indicates that we want `key=value` in the message template.
   *
   * <p>This marker interface is used internally, but you typically won't need to use it directly.
   * You can call `Field.Builder.keyValue` to get a instance of a field with this.
   */
  interface KeyValueField extends Field {}
}
