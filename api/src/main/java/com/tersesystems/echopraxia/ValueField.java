package com.tersesystems.echopraxia;

/**
 * The ValueField class.
 *
 * <p>This is a field that prints out `value` to a message template if possible.
 */
public final class ValueField implements Field {

  private final String name;
  private final Value<?> value;

  public ValueField(String name, Value<?> value) {
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
    return value.toString();
  }
}
