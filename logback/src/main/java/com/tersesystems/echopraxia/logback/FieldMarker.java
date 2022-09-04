package com.tersesystems.echopraxia.logback;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** A marker that contains an echopraxia field. */
public class FieldMarker extends BaseMarker {

  private final FieldBuilderResult result;

  FieldMarker(@NotNull FieldBuilderResult field) {
    super(field.toString());
    this.result = field;
  }

  @NotNull
  public static FieldMarker apply(@NotNull FieldBuilderResult field) {
    return new FieldMarker(field);
  }

  public List<Field> getFields() {
    return result.fields();
  }
}
