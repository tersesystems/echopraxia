package echopraxia.logstash;

import echopraxia.api.*;
import echopraxia.api.Attributes;
import echopraxia.api.Field;
import echopraxia.api.ToStringFormatter;
import echopraxia.api.Value;
import java.util.Collection;
import java.util.List;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.jetbrains.annotations.NotNull;

/**
 * This marker produces different text output than JSON output, and is used for arguments that are
 * rendered in the message template.
 *
 * <p>The attributes of the original field are always used.
 */
public class MappedFieldMarker extends ObjectAppendingMarker implements Field {

  private final Field textField;
  private final Field structuredField;

  public MappedFieldMarker(Field originalField, Field structuredField) {
    super(structuredField.name(), structuredField.value());
    this.textField = originalField;
    this.structuredField = structuredField;
  }

  public Field getTextField() {
    return textField;
  }

  public Field getStructuredField() {
    return structuredField;
  }

  @Override
  public @NotNull String name() {
    return structuredField.name();
  }

  @Override
  public @NotNull Value<?> value() {
    return structuredField.value();
  }

  @Override
  public @NotNull List<Field> fields() {
    return structuredField.fields();
  }

  @Override
  public @NotNull Attributes attributes() {
    return textField.attributes();
  }

  @Override
  public String toStringSelf() {
    return ToStringFormatter.getInstance().formatField(textField);
  }

  @Override
  public @NotNull <A> Field withAttribute(@NotNull Attribute<A> attr) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field withAttributes(@NotNull Attributes attrs) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull <A> Field withoutAttribute(@NotNull AttributeKey<A> key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field withoutAttributes(@NotNull Collection<AttributeKey<?>> keys) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field clearAttributes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field asValueOnly() {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field asElided() {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field withDisplayName(@NotNull String displayName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field withStructuredFormat(@NotNull FieldVisitor fieldVisitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NotNull Field withToStringFormat(@NotNull FieldVisitor fieldVisitor) {
    throw new UnsupportedOperationException();
  }
}
