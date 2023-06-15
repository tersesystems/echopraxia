package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.spi.EchopraxiaService;
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
    return EchopraxiaService.getInstance().getToStringFormatter().formatField(textField);
  }
}
