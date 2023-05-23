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
  public <A> @NotNull Field withAttribute(@NotNull Attribute<A> attr) {
    return new FieldMarker(field.withAttribute(attr));
  }

  @Override
  public @NotNull Field withAttributes(@NotNull Attributes attrs) {
    return new FieldMarker(field.withAttributes(attrs));
  }

  @Override
  public <A> @NotNull Field withoutAttribute(@NotNull AttributeKey<A> key) {
    return new FieldMarker(field.withoutAttribute(key));
  }

  @Override
  public @NotNull Field withoutAttributes(@NotNull Collection<AttributeKey<?>> keys) {
    return new FieldMarker(field.withoutAttributes(keys));
  }

  @Override
  public @NotNull Field clearAttributes() {
    return new FieldMarker(field.clearAttributes());
  }

  @Override
  public String toStringSelf() {
    return CoreLoggerFactory.getFieldFormatter().formatField(this);
  }
}
