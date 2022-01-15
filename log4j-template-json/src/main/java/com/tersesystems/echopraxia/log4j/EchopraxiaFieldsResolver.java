package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Field;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolver;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Creates a resolver (but it only goes under the `fields` and flatten doesn't work)
 */
final class EchopraxiaFieldsResolver implements EventResolver {

  private static final EchopraxiaFieldsResolver INSTANCE = new EchopraxiaFieldsResolver();

  static EchopraxiaFieldsResolver getInstance() {
    return INSTANCE;
  }

  static String getName() {
    return "echopraxiaFields";
  }

  @Override
  public boolean isResolvable(LogEvent logEvent) {
    return logEvent.getMessage() instanceof EchopraxiaFieldsMessage;
  }

  @Override
  public void resolve(LogEvent logEvent, JsonWriter jsonWriter) {
    EchopraxiaFieldsMessage message = (EchopraxiaFieldsMessage) logEvent.getMessage();
    Field[] fields = message.getFields();
    jsonWriter.writeObjectStart();
    for (Field field : fields) {
      convertField(jsonWriter, field);
    }
    jsonWriter.writeObjectEnd();
  }
  
   private void convertField(JsonWriter builder, Field f) {
     builder.writeObjectKey(f.name());
     switch (f.value().type()) {
       case ARRAY:
         builder.writeArrayStart();
          //         //noinspection unchecked
          //         final List<Field.Value<?>> arrayValues = (List<Field.Value<?>>) f.value().raw();
          //         for (Field.Value<?> value : arrayValues) {
          //           addValue(value, arrayBuilder);
          //         }
          //         builder.add(f.name(), arrayBuilder);
         builder.writeArrayEnd();
         break;
       case OBJECT:
         builder.writeObjectStart();
          //         final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
          //         //noinspection unchecked
          //         final List<Field> fields = (List<Field>) f.value().raw();
          //         addObject(fields, objectBuilder);
          //         builder.add(f.name(), objectBuilder);
         builder.writeObjectEnd();
         break;
       case STRING:
         builder.writeString((String) f.value().raw());
         break;
       case NUMBER:
         final Object raw = f.value().raw();
         if (raw instanceof Integer) {
           builder.writeNumber((Integer) raw);
         } else if (raw instanceof Long) {
           builder.writeNumber((Long) raw);
         } else if (raw instanceof Double) {
           builder.writeNumber((Double) raw);
         } else if (raw instanceof BigDecimal) {
           builder.writeNumber((BigDecimal) raw);
         } else if (raw instanceof BigInteger) {
           builder.writeNumber((BigInteger) raw);
         }
         break;
       case BOOLEAN:
         builder.writeBoolean((Boolean) f.value().raw());
         break;
       case EXCEPTION:
         // do nothing for right now...
         break;
       case NULL:
         builder.writeNull();
         break;
     }
   }
  
}
