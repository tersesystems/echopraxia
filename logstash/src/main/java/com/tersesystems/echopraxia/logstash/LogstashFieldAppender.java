package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldConverter;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.logback.DirectFieldMarker;
import com.tersesystems.echopraxia.logback.TransformingAppender;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

/**
 * An appender that converts the logging event from containing Field into containing
 * StructuredArgument so that it can be rendered as JSON.
 *
 * <p>This should only really be used if you are working with SLF4J directly, as it's a hack.
 */
public class LogstashFieldAppender extends TransformingAppender<ILoggingEvent> {

  private FieldConverter fieldConverter = FieldConverter.identity();

  public FieldConverter getFieldConverter() {
    return fieldConverter;
  }

  public void setFieldConverter(FieldConverter fieldConverter) {
    this.fieldConverter = fieldConverter;
  }

  @Override
  protected ILoggingEvent decorateEvent(ILoggingEvent eventObject) {
    // Run through the markers and convert FieldMarker to LogstashMarker
    final Marker marker = eventObject.getMarker();
    if (marker != null) {
      List<Marker> markers = new ArrayList<>();
      if (marker instanceof DirectFieldMarker) {
        final List<Field> fields = ((DirectFieldMarker) marker).getFields();
        for (Field field : fields) {
          Field loggerField = fieldConverter.convertLoggerField(field);
          markers.add(new FieldMarker(loggerField));
        }
      }
      final Iterator<Marker> iterator = marker.iterator();
      for (Marker m; iterator.hasNext(); ) {
        m = iterator.next();
        if (m instanceof DirectFieldMarker) {
          final List<Field> fields = ((DirectFieldMarker) m).getFields();
          for (Field field : fields) {
            Field loggerField = fieldConverter.convertLoggerField(field);
            markers.add(new FieldMarker(loggerField));
          }
        }
      }
      // Add the original marker to the end of the list...
      marker.add(Markers.aggregate(markers));
    }

    // what comes in is fields, what comes out is structured arguments
    final Object[] argumentArray = eventObject.getArgumentArray();
    if (argumentArray != null) {
      for (int i = 0; i < argumentArray.length; i++) {
        final Object arg = argumentArray[i];
        if (arg instanceof Field) {
          Field field = (Field) arg;
          Field converted = fieldConverter.convertArgumentField(field);
          argumentArray[i] = new FieldMarker(converted);

          // swap out the throwable if one is found
          if (eventObject.getThrowableProxy() == null) {
            final Throwable throwable = extractThrowable(field);
            if (throwable != null) {
              ((LoggingEvent) eventObject).setThrowableProxy(new ThrowableProxy(throwable));
            }
          }
        }
      }
    }
    return eventObject;
  }

  protected Throwable extractThrowable(Field field) {
    Value<?> value = field.value();
    if (value.type() == Value.Type.EXCEPTION) {
      Value.ExceptionValue throwable = (Value.ExceptionValue) value;
      return throwable.raw();
    } else {
      return null;
    }
  }
}
