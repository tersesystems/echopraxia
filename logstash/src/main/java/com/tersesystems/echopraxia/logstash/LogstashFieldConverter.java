package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

public interface LogstashFieldConverter {

  default StructuredArgument convertArgument(Field field) {
    final String name = field.name();
    final Value<?> value = field.value();
    if (value.type() == Value.Type.EXCEPTION) {
      Value.ExceptionValue throwable = (Value.ExceptionValue) value;
      return StructuredArguments.keyValue(name, throwable.raw().toString());
    } else {
      return field instanceof Field.ValueField
          ? StructuredArguments.value(name, value)
          : StructuredArguments.keyValue(name, value);
    }
  }

  default LogstashMarker convertMarker(Field field) {
    final String name = field.name();
    final Value<?> value = field.value();
    if (value.type() == Value.Type.EXCEPTION) {
      Value.ExceptionValue throwable = (Value.ExceptionValue) value;
      return Markers.append(name, throwable.raw().toString());
    } else {
      return Markers.append(name, value);
    }
  }
}
