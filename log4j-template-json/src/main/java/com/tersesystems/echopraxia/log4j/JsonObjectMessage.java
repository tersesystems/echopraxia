package com.tersesystems.echopraxia.log4j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.json.*;
import org.apache.logging.log4j.message.Message;

// JSONLayout only lets you stick arbitrary JSON inside the "message" field.
//
// JSONTemplateLayout only lets you stick arbitrary JSON inside the parameters field.
// it also looks like "destringifying" only works at one level, so strings get escaped if
// you have objects.
//
// As far as I can tell, MapMessage and StructuredDataMessage don't let you render a message
// directly, and...
//
// SO... fine.  If we want to render a structured message, we're going to have to do it directly.
public class JsonObjectMessage implements Message {
  private final String message;
  private final JsonObject jsonObject;
  private final Throwable throwable;

  public JsonObjectMessage(String message, JsonObject jsonObject, Throwable t) {
    this.message = message;
    this.jsonObject = jsonObject;
    this.throwable = t;
  }

  @Override
  public String getFormattedMessage() {
    // Need to format the message with any arguments?  Use StringSubstitutor?
    return formatJSON();
  }

  public String formatJSON() {
    // XXX We can add to the json object as need be...
    final JsonObjectBuilder builder = Json.createObjectBuilder(jsonObject);
    builder.add("message", message);

    Map<String, Object> map = new HashMap<>();
    // map.put(JsonGenerator.PRETTY_PRINTING, true);
    JsonWriterFactory writerFactory = Json.createWriterFactory(map);
    final StringWriter stringWriter = new StringWriter();
    final JsonWriter writer = writerFactory.createWriter(stringWriter);
    writer.write(builder.build());
    writer.close();
    return stringWriter.toString();
  }

  @Override
  public String getFormat() {
    return message; // this is UNFORMATTED message.
  }

  @Override
  public Object[] getParameters() {
    // final String s = formatJSON();
    return new Object[] {jsonObject};
  }

  @Override
  public Throwable getThrowable() {
    return throwable;
  }

  public JsonObject getJsonObject() {
    return jsonObject;
  }
}
