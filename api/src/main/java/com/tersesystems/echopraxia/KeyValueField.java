package com.tersesystems.echopraxia;

/**
 * The KeyValueField class.
 *
 * <p>This is a field that prints out key=value to a message template if possible.
 */
public final class KeyValueField implements Field {

  private final String name;
  private final Value<?> value;

  public KeyValueField(String name, Value<?> value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Value<?> value() {
    return value;
  }

  public String toString() {
    return name + "=" + value;
  }
}
