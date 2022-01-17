package com.tersesystems.echopraxia;

/**
 * The KeyValueField class.
 *
 * <p>This is a field that prints out key=value to a message template.
 */
public final class KeyValueField implements Field {

  private static final Formatter formatter =
      field -> {
        String name = field.name();
        return name + "=" + field.value();
      };

  private final String name;
  private final Value<?> value;

  // package private builder
  static final Field.Builder keyValueFieldBuilder =
      new Field.Builder() {
        @Override
        public Field field(String name, Value<?> value) {
          return new KeyValueField(name, value);
        }
      };

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
    return formatter.fieldToString(this);
  }
}
