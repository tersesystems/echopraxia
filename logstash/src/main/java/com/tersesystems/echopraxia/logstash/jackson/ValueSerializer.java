package com.tersesystems.echopraxia.logstash.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tersesystems.echopraxia.Field;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * The ValueSerializer class plugs into the Jackson serializer system to serialize Field.Value into
 * JSON.
 */
public class ValueSerializer extends StdSerializer<Field.Value> {

  public static final ValueSerializer INSTANCE = new ValueSerializer();

  public ValueSerializer() {
    super(Field.Value.class);
  }

  @Override
  public void serialize(Field.Value value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    switch (value.type()) {
      case ARRAY:
        List<Field.Value<?>> arrayValues = ((Field.Value.ArrayValue) value).raw();
        gen.writeStartArray();
        for (Field.Value<?> arrayValue : arrayValues) {
          gen.writeObject(arrayValue);
        }
        gen.writeEndArray();
        break;
      case OBJECT:
        List<Field> objFields = ((Field.Value.ObjectValue) value).raw();
        gen.writeStartObject();
        for (Field objField : objFields) {
          gen.writeObject(objField);
        }
        gen.writeEndObject();
        break;
      case STRING:
        gen.writeString(value.raw().toString());
        break;
      case NUMBER:
        Number n = ((Field.Value.NumberValue) value).raw();
        if (n instanceof Byte) {
          gen.writeNumber(n.byteValue());
        } else if (n instanceof Short) {
          gen.writeNumber(n.shortValue());
        } else if (n instanceof Integer) {
          gen.writeNumber(n.intValue());
        } else if (n instanceof Long) {
          gen.writeNumber(n.longValue());
        } else if (n instanceof Double) {
          gen.writeNumber(n.doubleValue());
        } else if (n instanceof BigInteger) {
          gen.writeNumber((BigInteger) n);
        } else if (n instanceof BigDecimal) {
          gen.writeNumber((BigDecimal) n);
        }
        break;
      case BOOLEAN:
        boolean b = ((Field.Value.BooleanValue) value).raw();
        gen.writeBoolean(b);
        break;
      case EXCEPTION:
        // gen.writeString(value.raw().toString());
        break;
      case NULL:
        gen.writeNull();
        break;
    }
  }
}
