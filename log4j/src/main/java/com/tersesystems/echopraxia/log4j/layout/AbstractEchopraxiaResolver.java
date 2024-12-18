package com.tersesystems.echopraxia.log4j.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tersesystems.echopraxia.model.Field;
import com.tersesystems.echopraxia.model.Value;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolver;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;

/** Creates a resolver (but it only goes under the `fields` and flatten doesn't work) */
abstract class AbstractEchopraxiaResolver implements EventResolver {

  private static final ObjectMapper mapper = (new ObjectMapper()).findAndRegisterModules();

  @Override
  public boolean isResolvable(LogEvent logEvent) {
    return logEvent.getMessage() instanceof EchopraxiaFieldsMessage;
  }

  @Override
  public void resolve(LogEvent logEvent, JsonWriter jsonWriter) {
    EchopraxiaFieldsMessage message = (EchopraxiaFieldsMessage) logEvent.getMessage();
    List<Field> fields = resolveFields(message);
    StringWriter stringWriter = new StringWriter();
    try {
      Value<?> objectValue = Value.object(fields);
      mapper.writeValue(stringWriter, objectValue);
      jsonWriter.writeRawString(stringWriter.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract List<Field> resolveFields(EchopraxiaFieldsMessage message);
}
