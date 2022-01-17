package com.tersesystems.echopraxia.log4j.layout;

import com.tersesystems.echopraxia.Field;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolver;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;

/** Creates a resolver (but it only goes under the `fields` and flatten doesn't work) */
final class EchopraxiaFieldsResolver implements EventResolver {

  private static final EchopraxiaFieldsResolver INSTANCE = new EchopraxiaFieldsResolver();

  private static final EchopraxiaFieldSerializer serializer = new EchopraxiaFieldSerializer();

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

    JsonObjectBuilder builder = Json.createObjectBuilder();
    for (Field field : fields) {
      serializer.convertField(builder, field);
    }
    JsonObject jsonObject = builder.build();
    StringWriter stringWriter = new StringWriter();
    javax.json.JsonWriter javaxJsonWriter = Json.createWriter(stringWriter);
    javaxJsonWriter.write(jsonObject);
    jsonWriter.writeRawString(stringWriter.toString());
  }
}
