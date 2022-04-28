package com.tersesystems.echopraxia.log4j.layout;

import static com.tersesystems.echopraxia.api.Field.Value;

import com.tersesystems.echopraxia.api.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

class EchopraxiaFieldSerializer {

  void convertField(JsonObjectBuilder builder, Field f) {
    final String name = f.name();
    final Value<?> v = f.value();

    // if raw() is null and it's not NullValue, then chalk up an error and keep going.
    if (v.type() == Value.ValueType.NULL || v.raw() == null) {
      builder.addNull(name);
      return;
    }

    switch (v.type()) {
      case ARRAY:
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        //noinspection unchecked
        final List<Value<?>> arrayValues = (List<Value<?>>) v.raw();
        for (Value<?> value : arrayValues) {
          addValue(value, arrayBuilder);
        }
        builder.add(name, arrayBuilder);
        break;
      case OBJECT:
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        //noinspection unchecked
        final List<Field> fields = (List<Field>) v.raw();
        addObject(fields, objectBuilder);
        builder.add(name, objectBuilder);
        break;
      case STRING:
        builder.add(name, (String) v.raw());
        break;
      case NUMBER:
        final Object raw = v.raw();
        if (raw instanceof Integer) {
          builder.add(name, (Integer) raw);
        } else if (raw instanceof Long) {
          builder.add(name, (Long) raw);
        } else if (raw instanceof Double) {
          builder.add(name, (Double) raw);
        } else if (raw instanceof BigDecimal) {
          builder.add(name, (BigDecimal) raw);
        } else if (raw instanceof BigInteger) {
          builder.add(name, (BigInteger) raw);
        }
        break;
      case BOOLEAN:
        builder.add(name, (Boolean) v.raw());
        break;
      case EXCEPTION:
        // do nothing for right now...
        break;
      case NULL:
        builder.addNull(name); // should never be reached, but okay :-)
        break;
    }
  }

  private void addValue(Value<?> value, JsonArrayBuilder arrayBuilder) {
    final Object rawValue = value.raw();
    switch (value.type()) {
      case ARRAY:
        JsonArrayBuilder newArrayBuilder = Json.createArrayBuilder();
        addValue(value, newArrayBuilder);
        arrayBuilder.add(newArrayBuilder);
        break;
      case OBJECT:
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        //noinspection unchecked
        List<Field> valueFields = (List<Field>) rawValue;
        addObject(valueFields, objectBuilder);
        arrayBuilder.add(objectBuilder);
        break;
      case STRING:
        if (rawValue == null) {
          arrayBuilder.add(JsonValue.NULL);
        } else {
          arrayBuilder.add((String) rawValue);
        }
        break;
      case NUMBER:
        if (rawValue instanceof Integer) {
          arrayBuilder.add((Integer) rawValue);
        } else if (rawValue instanceof Long) {
          arrayBuilder.add((Long) rawValue);
        } else if (rawValue instanceof Double) {
          arrayBuilder.add((Double) rawValue);
        } else if (rawValue instanceof BigDecimal) {
          arrayBuilder.add((BigDecimal) rawValue);
        } else if (rawValue instanceof BigInteger) {
          arrayBuilder.add((BigInteger) rawValue);
        } else {
          arrayBuilder.add(JsonValue.NULL);
        }
        break;
      case BOOLEAN:
        if (rawValue == null) {
          arrayBuilder.add(JsonValue.NULL);
        } else {
          arrayBuilder.add((Boolean) rawValue);
        }
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
