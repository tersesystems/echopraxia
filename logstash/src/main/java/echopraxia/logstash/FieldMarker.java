package echopraxia.logstash;

import echopraxia.api.*;
import echopraxia.api.Attributes;
import echopraxia.api.Field;
import echopraxia.api.Value;
import java.util.Collection;
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

  @Override
  public @NotNull <A> Field withAttribute(@NotNull Attribute<A> attr) {
    return new FieldMarker(field.withAttribute(attr));
  }

  @Override
  public @NotNull Field withAttributes(@NotNull Attributes attrs) {
    return new FieldMarker(field.withAttributes(attrs));
  }

  @Override
  public @NotNull <A> Field withoutAttribute(@NotNull AttributeKey<A> key) {
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
  public @NotNull Field asValueOnly() {
    return new FieldMarker(field.asValueOnly());
  }

  @Override
  public @NotNull Field asElided() {
    return new FieldMarker(field.asElided());
  }

  @Override
  public @NotNull Field withDisplayName(@NotNull String displayName) {
    return new FieldMarker(field.withDisplayName(displayName));
  }

  @Override
  public @NotNull Field withStructuredFormat(@NotNull FieldVisitor fieldVisitor) {
    return new FieldMarker(field.withStructuredFormat(fieldVisitor));
  }

  @Override
  public @NotNull Field withToStringFormat(@NotNull FieldVisitor fieldVisitor) {
    return new FieldMarker(field.withToStringFormat(fieldVisitor));
  }
}
