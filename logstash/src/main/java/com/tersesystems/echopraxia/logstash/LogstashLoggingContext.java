package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.support.DefaultLoggingContext;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/**
 * Logstash logging context implementation.
 *
 * <p>Note that this makes field evaluation lazy so that functions can pull things out of a thread
 * local (typically hard to do if when loggers are set up initially).
 */
public class LogstashLoggingContext implements DefaultLoggingContext {

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
  public @NotNull List<Field> getFields() {
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

    // This MUST be lazy, we can't get the fields until statement evaluation
    Supplier<List<Field>> joinedFields =
        joinFields(LogstashLoggingContext.this::getFields, context::getFields);
    Supplier<List<Marker>> joinedMarkers =
        joinMarkers(context::getMarkers, LogstashLoggingContext.this::getMarkers);
    return new LogstashLoggingContext(joinedFields, joinedMarkers);
  }

  private Supplier<List<Marker>> joinMarkers(
      Supplier<List<Marker>> markersSupplier, Supplier<List<Marker>> thisMarkersSupplier) {
    return () -> {
      final List<Marker> markers = markersSupplier.get();
      final List<Marker> thisMarkers = thisMarkersSupplier.get();
      if (markers.isEmpty()) {
        return thisMarkers;
      } else if (thisMarkers.isEmpty()) {
        return markers;
      } else {
        return Stream.concat(thisMarkers.stream(), markers.stream()).collect(Collectors.toList());
      }
    };
  }

  private Supplier<List<Field>> joinFields(
      Supplier<List<Field>> thisFieldsSupplier, Supplier<List<Field>> ctxFieldsSupplier) {
    return () -> {
      List<Field> thisFields = thisFieldsSupplier.get();
      List<Field> ctxFields = ctxFieldsSupplier.get();

      if (thisFields.isEmpty()) {
        return ctxFields;
      } else if (ctxFields.isEmpty()) {
        return thisFields;
      } else {
        // Stream.concat is actually faster than explicit ArrayList!
        // https://blog.soebes.de/blog/2020/03/31/performance-stream-concat/
        return Stream.concat(thisFields.stream(), ctxFields.stream()).collect(Collectors.toList());
      }
    };
  }

  // Convert markers explicitly.
  org.slf4j.Marker getMarker() {
    final List<Field> fields = getFields();
    final List<Marker> markers = getMarkers();

    // XXX there should be a way to cache this if we know it hasn't changed, since it
    // could be calculated repeatedly.
    if (fields.isEmpty() && markers.isEmpty()) {
      return null;
    }

    final List<Marker> markerList = new ArrayList<>();
    for (Field field : fields) {
      LogstashMarker append = Markers.append(field.name(), field.value());
      markerList.add(append);
    }
    markerList.addAll(markers);
    return Markers.aggregate(markerList);
  }
}
