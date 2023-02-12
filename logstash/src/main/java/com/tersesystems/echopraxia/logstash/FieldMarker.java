package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import java.util.Collections;
import java.util.List;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.jetbrains.annotations.NotNull;

public class FieldMarker extends ObjectAppendingMarker implements Field {

  private final Field field;

  public FieldMarker(Field field) {
    super(field.name(), field.value());
    this.field = field;
  }

  public FieldMarker(Field field, String messageFormatPattern) {
    super(field.name(), field.value(), messageFormatPattern);
    this.field = field;
  }

  @Override
  public @NotNull String name() {
    return field.name();
  }

  @Override
  public @NotNull Value<?> value() {
    return field.value();
  }

  @Override
  public @NotNull List<Field> fields() {
    return Collections.singletonList(this);
  }
}
