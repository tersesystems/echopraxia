package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.Attributes;
import com.tersesystems.echopraxia.api.Field;
import org.jetbrains.annotations.NotNull;

/**
 * This marker produces different text output than JSON output, and is used for arguments that are
 * rendered in the message template.
 *
 * <p>The attributes of the original field are always used.
 */
public class MappedFieldMarker extends FieldMarker {

  private final Field originalField;

  public MappedFieldMarker(Field originalField, Field structuredField) {
    super(structuredField);
    this.originalField = originalField;
  }

  public Field getOriginalField() {
    return originalField;
  }

  @Override
  public @NotNull Attributes attributes() {
    return originalField.attributes();
  }

  @Override
  public String toStringSelf() {
    final String fieldValueString = originalField.value().toString();
    return isValueOnly() ? fieldValueString : originalField.name() + "=" + fieldValueString;
  }
}
