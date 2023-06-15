package com.tersesystems.echopraxia.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldVisitor;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.spi.PresentationHints;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/** The FieldSerializer class plugs into Jackson to serialize Field to JSON. */
public class FieldSerializer extends StdSerializer<Field> {

  static final FieldSerializer INSTANCE = new FieldSerializer();

  public FieldSerializer() {
    this(Field.class);
  }

  protected FieldSerializer(Class<Field> t) {
    super(t);
  }

  @Override
  public void serialize(Field input, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {

    Field field;
    Optional<FieldVisitor> fieldVisitorOpt =
        input.attributes().getOptional(PresentationHints.STRUCTURED_FORMAT);
    if (fieldVisitorOpt.isPresent()) {
      FieldVisitor fieldVisitor = fieldVisitorOpt.get();
      field = fieldVisitor.visit(input);
    } else {
      field = input;
    }

    final String name = field.name();
    final Value<?> value = field.value();
    // short circuit if a null value's been passed in, so we can keep logging.
    if (value.raw() == null) {
      jgen.writeNullField(name);
      return;
    }
    switch (value.type()) {
      case ARRAY:
        List<Value<?>> arrayValues = ((Value.ArrayValue) value).raw();
        jgen.writeArrayFieldStart(name);
        for (Value<?> av : arrayValues) {
          if (av != null) {
            jgen.writeObject(av);
          } else {
            // fallback for when passed a null value directly (grrr)
            jgen.writeNull();
          }
        }
        jgen.writeEndArray();
        break;
      case OBJECT:
        List<Field> objFields = ((Value.ObjectValue) value).raw();
        jgen.writeObjectFieldStart(name);
        for (Field objField : objFields) {
          jgen.writeObject(objField);
        }
        jgen.writeEndObject();
        break;
      case STRING:
        jgen.writeStringField(name, value.raw().toString());
        break;
      case NUMBER:
        Number n = ((Value.NumberValue) value).raw();
        if (n instanceof Byte) {
          jgen.writeNumberField(name, n.byteValue());
        } else if (n instanceof Short) {
          jgen.writeNumberField(name, n.shortValue());
        } else if (n instanceof Integer) {
          jgen.writeNumberField(name, n.intValue());
        } else if (n instanceof Long) {
          jgen.writeNumberField(name, n.longValue());
        } else if (n instanceof Double) {
          jgen.writeNumberField(name, n.doubleValue());
        } else if (n instanceof BigInteger) {
          jgen.writeNumberField(name, (BigInteger) n);
        } else if (n instanceof BigDecimal) {
          jgen.writeNumberField(name, (BigDecimal) n);
        }
        break;
      case BOOLEAN:
        boolean b = ((Value.BooleanValue) value).raw();
        jgen.writeBooleanField(name, b);
        break;
      case EXCEPTION:
        break;
      case NULL:
        jgen.writeNullField(name);
        break;
    }
  }
}
