package com.tersesystems.echopraxia.logback;

import com.tersesystems.echopraxia.api.Field;
import org.jetbrains.annotations.NotNull;

/** A marker that contains an echopraxia field. */
public class FieldMarker extends BaseMarker {

  private final Field field;

  FieldMarker(@NotNull Field field) {
    super(field.toString());
    this.field = field;
  }

  @NotNull
  public static FieldMarker apply(@NotNull Field field) {
    return new FieldMarker(field);
  }

  public Field getField() {
    return field;
  }
}
