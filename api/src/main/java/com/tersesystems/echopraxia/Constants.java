package com.tersesystems.echopraxia;

import org.jetbrains.annotations.NotNull;

// Some package private constants
class Constants {

  private static final Field.Builder builderInstance = new Field.Builder() {};

  private Constants() {}

  static Field.Builder builder() {
    return builderInstance;
  }

  /** This is a field that prints out value to a message template if possible. */
  static final class DefaultValueField implements ValueField {
    private final String name;
    private final Value<?> value;

    public DefaultValueField(String name, Value<?> value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public @NotNull String name() {
      return name;
    }

    @Override
    public @NotNull Value<?> value() {
      return value;
    }

    public String toString() {
      return value.toString();
    }
  }

  /** This is a field that prints out key=value to a message template if possible. */
  static final class DefaultKeyValueField implements KeyValueField {
    private final String name;
    private final Value<?> value;

    public DefaultKeyValueField(String name, Value<?> value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public @NotNull String name() {
      return name;
    }

    @Override
    public @NotNull Value<?> value() {
      return value;
    }

    public String toString() {
      return name + "=" + value;
    }
  }
}
