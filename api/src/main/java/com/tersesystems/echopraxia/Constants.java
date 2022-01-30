package com.tersesystems.echopraxia;

import java.util.concurrent.atomic.LongAdder;
import org.jetbrains.annotations.NotNull;

// Some package private constants
class Constants {

  public static final String ECHOPRAXIA_UNKNOWN = "echopraxia-unknown-";

  public static final LongAdder unknownFieldAdder = new LongAdder();

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
      this.name = requireName(name);
      this.value = requireValue(value);
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
      this.name = requireName(name);
      this.value = requireValue(value);
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

  // construct a field name so that json is happy and keep going.
  private static String requireName(String name) {
    if (name != null) {
      return name;
    }
    unknownFieldAdder.increment();
    return ECHOPRAXIA_UNKNOWN + unknownFieldAdder.longValue();
  }

  private static Field.Value<?> requireValue(Field.Value<?> value) {
    if (value != null) {
      return value;
    }
    return Field.Value.nullValue();
  }
}
