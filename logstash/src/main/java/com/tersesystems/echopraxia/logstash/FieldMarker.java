package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.*;
import java.util.Collection;
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
  public <A> Field withAttribute(Attribute<A> attr) {
    return new FieldMarker(field.withAttribute(attr));
  }

  @Override
  public Field withAttributes(Attributes attrs) {
    return new FieldMarker(field.withAttributes(attrs));
  }

  @Override
  public <A> Field withoutAttribute(AttributeKey<A> key) {
    return new FieldMarker(field.withoutAttribute(key));
  }

  @Override
  public Field withoutAttributes(Collection<AttributeKey<?>> keys) {
    return new FieldMarker(field.withoutAttributes(keys));
  }

  @Override
  public Field clearAttributes() {
    return new FieldMarker(field.clearAttributes());
  }

  @Override
  public String toStringSelf() {
    final String fieldValueString = field.value().toString();
    return isValueOnly() ? fieldValueString : field.name() + "=" + fieldValueString;
  }
}
