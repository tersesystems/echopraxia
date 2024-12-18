package com.tersesystems.echopraxia.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tersesystems.echopraxia.model.Field;
import com.tersesystems.echopraxia.model.Value;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ValueDeserializer extends StdDeserializer<Value<?>> {
  static final ValueDeserializer INSTANCE = new ValueDeserializer();

  public ValueDeserializer() {
    super(Value.class);
  }

  @Override
  public Value<?> deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    final JsonNode jsonNode = p.readValueAs(JsonNode.class);
    return processNode(jsonNode);
  }

  private Value<?> processNode(JsonNode jsonNode) {
    switch (jsonNode.getNodeType()) {
      case ARRAY:
        return processArrayValue(jsonNode);
      case BINARY:
        return Value.string(jsonNode.textValue());
      case BOOLEAN:
        return Value.bool(jsonNode.booleanValue());
      case MISSING:
        return Value.nullValue();
      case NULL:
        return Value.nullValue();
      case NUMBER:
        switch (jsonNode.numberType()) {
          case INT:
            return Value.number(jsonNode.asInt());
          case LONG:
            return Value.number(jsonNode.asLong());
          case BIG_INTEGER:
            return Value.number((BigInteger) jsonNode.numberValue());
          case FLOAT:
            return Value.number(jsonNode.floatValue());
          case DOUBLE:
            return Value.number(jsonNode.asDouble());
          case BIG_DECIMAL:
            return Value.number((BigDecimal) jsonNode.numberValue());
        }
      case OBJECT:
        return processObjectValue(jsonNode);
      case POJO:
        throw new IllegalStateException("No POJO accepted for " + jsonNode);
      case STRING:
        return Value.string(jsonNode.textValue());
      default:
        throw new IllegalStateException("Unknown node type " + jsonNode.getNodeType());
    }
  }

  private Value<?> processObjectValue(JsonNode jsonNode) {
    ObjectNode node = (ObjectNode) jsonNode;
    List<Field> fields = new ArrayList<>();
    for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
      Map.Entry<String, JsonNode> entry = it.next();
      Value<?> value = processNode(entry.getValue());
      Field field = Field.keyValue(entry.getKey(), value);
      fields.add(field);
    }
    return Value.object(fields);
  }

  private Value<?> processArrayValue(JsonNode jsonNode) {
    final List<Value<?>> values = new ArrayList<>();
    for (JsonNode arrayItem : jsonNode) {
      Value<?> value = processNode(arrayItem);
      values.add(value);
    }
    return Value.array(values);
  }
}
