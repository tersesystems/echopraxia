package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Marker;

public class LogstashLoggingContext implements LoggingContext {

  private final List<Marker> markers;
  private final List<Field> fields;

  LogstashLoggingContext(List<Field> fields, List<Marker> markers) {
    this.fields = fields;
    this.markers = markers;
  }

  public List<Field> getFields() {
    return fields;
  }

  public List<Marker> getMarkers() {
    return markers;
  }

  public LogstashLoggingContext and(LogstashLoggingContext context) {
    if (context != null) {
      List<Field> f =
          Stream.of(context.fields, this.fields)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
      List<Marker> m =
          Stream.of(context.markers, this.markers)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
      return new LogstashLoggingContext(f, m);
    } else {
      return this;
    }
  }
}
