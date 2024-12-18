package com.tersesystems.echopraxia.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * The ValueSerializer class plugs into the Jackson serializer system to serialize Value into JSON.
 */
public class ValueSerializer extends StdSerializer<Value> {

  static final ValueSerializer INSTANCE = new ValueSerializer();

  public ValueSerializer() {
    super(Value.class);
  }

  @Override
  public void serialize(Value value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    if (value == null || value.raw() == null) {
      gen.writeNull();
      return;
    }

    switch (value.type()) {
      case ARRAY:
        List<Value<?>> arrayValues = ((Value.ArrayValue) value).raw();
        gen.writeStartArray();
        for (Value<?> arrayValue : arrayValues) {
          gen.writeObject(arrayValue);
        }
        gen.writeEndArray();
        break;
      case OBJECT:
        List<Field> objFields = ((Value.ObjectValue) value).raw();
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
        Number n = ((Value.NumberValue) value).raw();
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
        boolean b = ((Value.BooleanValue) value).raw();
        gen.writeBoolean(b);
        break;
      case EXCEPTION:
        final Throwable throwable = ((Value.ExceptionValue) value).raw();
        gen.writeString(throwable.toString());
        break;
      case NULL:
        gen.writeNull();
        break;
    }
  }
}
