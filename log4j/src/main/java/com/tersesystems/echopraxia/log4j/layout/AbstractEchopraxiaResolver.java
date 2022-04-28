package com.tersesystems.echopraxia.log4j.layout;

import com.tersesystems.echopraxia.api.Field;
import java.io.StringWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolver;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;

/** Creates a resolver (but it only goes under the `fields` and flatten doesn't work) */
abstract class AbstractEchopraxiaResolver implements EventResolver {

  private static final EchopraxiaFieldSerializer serializer = new EchopraxiaFieldSerializer();

  @Override
  public boolean isResolvable(LogEvent logEvent) {
    return logEvent.getMessage() instanceof EchopraxiaFieldsMessage;
  }

  @Override
  public void resolve(LogEvent logEvent, JsonWriter jsonWriter) {
    EchopraxiaFieldsMessage message = (EchopraxiaFieldsMessage) logEvent.getMessage();
    List<Field> fields = resolveFields(message);
    JsonObject jsonObject = convertToJson(fields);
    serializeJson(jsonWriter, jsonObject);
  }

  private JsonObject convertToJson(List<Field> fields) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    for (Field field : fields) {
      serializer.convertField(builder, field);
    }
    return builder.build();
  }

  private void serializeJson(JsonWriter jsonWriter, JsonObject jsonObject) {
    StringWriter stringWriter = new StringWriter();
    javax.json.JsonWriter javaxJsonWriter = Json.createWriter(stringWriter);
    javaxJsonWriter.write(jsonObject);
    jsonWriter.writeRawString(stringWriter.toString());
  }

  protected abstract List<Field> resolveFields(EchopraxiaFieldsMessage message);
}
