package com.tersesystems.echopraxia.logstash;

import static net.logstash.logback.argument.StructuredArguments.DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN;
import static net.logstash.logback.argument.StructuredArguments.VALUE_ONLY_MESSAGE_FORMAT_PATTERN;

import com.tersesystems.echopraxia.api.Field;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.LogstashMarker;

interface FieldToArgumentConverter {

  default StructuredArgument convertArgument(Field field) {
    String format =
        field instanceof Field.ValueField
            ? VALUE_ONLY_MESSAGE_FORMAT_PATTERN
            : DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN;
    return new FieldMarker(field, format);
  }
}

interface FieldToMarkerConverter {
  default LogstashMarker convertMarker(Field field) {
    return new FieldMarker(field);
  }
}
