package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Marker;

/**
 * Logstash logging context implementation.
 *
 * <p>Note that this makes field evaluation lazy so that functions can pull things out of a thread
 * local (typically hard to do if when loggers are set up initially).
 */
public class LogstashLoggingContext implements LoggingContext {

  private static final LogstashLoggingContext EMPTY =
      new LogstashLoggingContext(Collections::emptyList, Collections::emptyList);

  protected final Supplier<List<Field>> fieldsSupplier;
  protected final Supplier<List<Marker>> markersSupplier;

  protected LogstashLoggingContext(Supplier<List<Field>> f, Supplier<List<Marker>> m) {
    this.fieldsSupplier = f;
    this.markersSupplier = m;
  }

  public static LogstashLoggingContext empty() {
    return EMPTY;
  }

  @Override
  public List<Field> getFields() {
    return fieldsSupplier.get();
  }

  public List<Marker> getMarkers() {
    return markersSupplier.get();
  }

  /**
   * Joins the two contexts together, concatenating the lists in a supplier function.
   *
   * @param context the context to join
   * @return the new context containing fields and markers from both.
   */
  public LogstashLoggingContext and(LogstashLoggingContext context) {
    if (context == null) {
      return this;
    }

    final List<Field> thisFields = LogstashLoggingContext.this.getFields();
    final List<Field> ctxFields = context.getFields();
    Supplier<List<Field>> joinedFields = joinFields(thisFields, ctxFields);

    final List<Marker> markers = context.getMarkers();
    final List<Marker> thisMarkers = LogstashLoggingContext.this.getMarkers();
    Supplier<List<Marker>> joinedMarkers = joinMarkers(markers, thisMarkers);
    return new LogstashLoggingContext(joinedFields, joinedMarkers);
  }

  private Supplier<List<Marker>> joinMarkers(List<Marker> markers, List<Marker> thisMarkers) {
    if (markers.isEmpty()) {
      return () -> thisMarkers;
    } else if (thisMarkers.isEmpty()) {
      return () -> markers;
    } else {
      return () ->
          Stream.concat(thisMarkers.stream(), markers.stream()).collect(Collectors.toList());
    }
  }

  private Supplier<List<Field>> joinFields(List<Field> thisFields, List<Field> ctxFields) {
    Supplier<List<Field>> joinedFields;
    if (thisFields.isEmpty()) {
      joinedFields = () -> ctxFields;
    } else if (ctxFields.isEmpty()) {
      joinedFields = () -> thisFields;
    } else {
      joinedFields =
          () -> Stream.concat(thisFields.stream(), ctxFields.stream()).collect(Collectors.toList());
    }
    return joinedFields;
  }
}
