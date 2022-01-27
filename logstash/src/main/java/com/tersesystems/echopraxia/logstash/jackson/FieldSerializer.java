package com.tersesystems.echopraxia.logstash.jackson;

import static com.tersesystems.echopraxia.Field.Value;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tersesystems.echopraxia.Field;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/** The FieldSerializer class plugs into Jackson to serialize Field to JSON. */
public class FieldSerializer extends StdSerializer<Field> {

  public static final FieldSerializer INSTANCE = new FieldSerializer();

  public FieldSerializer() {
    this(Field.class);
  }

  protected FieldSerializer(Class<Field> t) {
    super(t);
  }

  @Override
  public void serialize(Field field, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    Value<?> value = field.value();
    switch (value.type()) {
      case ARRAY:
        List<Value<?>> arrayValues = ((Value.ArrayValue) value).raw();
        jgen.writeArrayFieldStart(field.name());
        for (Value<?> av : arrayValues) {
          jgen.writeObject(av);
        }
        jgen.writeEndArray();
        break;
      case OBJECT:
        List<Field> objFields = ((Value.ObjectValue) value).raw();
        jgen.writeObjectFieldStart(field.name());
        for (Field objField : objFields) {
          jgen.writeObject(objField);
        }
        jgen.writeEndObject();
        break;
      case STRING:
        jgen.writeStringField(field.name(), value.raw().toString());
        break;
      case NUMBER:
        Number n = ((Value.NumberValue) value).raw();
        if (n instanceof Byte) {
          jgen.writeNumberField(field.name(), n.byteValue());
        } else if (n instanceof Short) {
          jgen.writeNumberField(field.name(), n.shortValue());
        } else if (n instanceof Integer) {
          jgen.writeNumberField(field.name(), n.intValue());
        } else if (n instanceof Long) {
          jgen.writeNumberField(field.name(), n.longValue());
        } else if (n instanceof Double) {
          jgen.writeNumberField(field.name(), n.doubleValue());
        } else if (n instanceof BigInteger) {
          jgen.writeNumberField(field.name(), (BigInteger) n);
        } else if (n instanceof BigDecimal) {
          jgen.writeNumberField(field.name(), (BigDecimal) n);
        }
        break;
      case BOOLEAN:
        boolean b = ((Value.BooleanValue) value).raw();
        jgen.writeBooleanField(field.name(), b);
        break;
      case EXCEPTION:
        break;
      case NULL:
        jgen.writeNullField(field.name());
        break;
    }
  }
}
