package com.tersesystems.echopraxia.log4j.layout;

import com.tersesystems.echopraxia.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

class EchopraxiaFieldSerializer {

  void convertField(JsonObjectBuilder builder, Field f) {
    switch (f.value().type()) {
      case ARRAY:
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        //noinspection unchecked
        final List<Field.Value<?>> arrayValues = (List<Field.Value<?>>) f.value().raw();
        for (Field.Value<?> value : arrayValues) {
          addValue(value, arrayBuilder);
        }
        builder.add(f.name(), arrayBuilder);
        break;
      case OBJECT:
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        //noinspection unchecked
        final List<Field> fields = (List<Field>) f.value().raw();
        addObject(fields, objectBuilder);
        builder.add(f.name(), objectBuilder);
        break;
      case STRING:
        builder.add(f.name(), (String) f.value().raw());
        break;
      case NUMBER:
        final Object raw = f.value().raw();
        if (raw instanceof Integer) {
          builder.add(f.name(), (Integer) raw);
        } else if (raw instanceof Long) {
          builder.add(f.name(), (Long) raw);
        } else if (raw instanceof Double) {
          builder.add(f.name(), (Double) raw);
        } else if (raw instanceof BigDecimal) {
          builder.add(f.name(), (BigDecimal) raw);
        } else if (raw instanceof BigInteger) {
          builder.add(f.name(), (BigInteger) raw);
        }
        break;
      case BOOLEAN:
        builder.add(f.name(), (Boolean) f.value().raw());
        break;
      case EXCEPTION:
        // do nothing for right now...
        break;
      case NULL:
        builder.addNull(f.name());
        break;
    }
  }

  private void addValue(Field.Value<?> value, JsonArrayBuilder arrayBuilder) {
    switch (value.type()) {
      case ARRAY:
        JsonArrayBuilder newArrayBuilder = Json.createArrayBuilder();
        addValue(value, newArrayBuilder);
        arrayBuilder.add(newArrayBuilder);
        break;
      case OBJECT:
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        //noinspection unchecked
        List<Field> valueFields = (List<Field>) value.raw();
        addObject(valueFields, objectBuilder);
        arrayBuilder.add(objectBuilder);
        break;
      case STRING:
        arrayBuilder.add((String) value.raw());
        break;
      case NUMBER:
        final Object raw = value.raw();
        if (raw instanceof Integer) {
          arrayBuilder.add((Integer) raw);
        } else if (raw instanceof Long) {
          arrayBuilder.add((Long) raw);
        } else if (raw instanceof Double) {
          arrayBuilder.add((Double) raw);
        } else if (raw instanceof BigDecimal) {
          arrayBuilder.add((BigDecimal) raw);
        } else if (raw instanceof BigInteger) {
          arrayBuilder.add((BigInteger) raw);
        }
        break;
      case BOOLEAN:
        arrayBuilder.add((Boolean) value.raw());
        break;
      case EXCEPTION:
        // Do nothing for now...
        break;
      case NULL:
        arrayBuilder.add(JsonValue.NULL);
        break;
    }
  }

  private void addObject(List<Field> values, JsonObjectBuilder builder) {
    for (Field f : values) {
      convertField(builder, f);
    }
  }
}
