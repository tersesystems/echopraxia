package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.*;
import java.util.Collections;
import java.util.List;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used by logstash-logback-encoder to turn a field into something that looks like a
 * StructuredArgument/Marker.
 */
public class FieldMarker extends ObjectAppendingMarker implements Field {

  private final Field field;

  public FieldMarker(Field field) {
    super(field.name(), field.value());
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

  @Override
  public @NotNull Attributes attributes() {
    return field.attributes();
  }

  @Override
  public String toStringSelf() {
    return field.toString();
  }

  public String toString() {
    return field.toString();
  }
}
