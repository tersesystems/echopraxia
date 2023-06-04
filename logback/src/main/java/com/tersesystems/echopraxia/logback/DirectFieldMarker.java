package com.tersesystems.echopraxia.logback;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** A marker that contains an echopraxia field, used as part of the direct API. */
public class DirectFieldMarker extends BaseMarker {

  private final FieldBuilderResult result;

  DirectFieldMarker(@NotNull FieldBuilderResult field) {
    super(field.toString());
    this.result = field;
  }

  @NotNull
  public static DirectFieldMarker apply(@NotNull FieldBuilderResult field) {
    return new DirectFieldMarker(field);
  }

  public List<Field> getFields() {
    return result.fields();
  }
}
