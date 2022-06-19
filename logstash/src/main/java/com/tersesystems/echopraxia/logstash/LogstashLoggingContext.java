package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

/**
 * Logstash logging context implementation.
 *
 * <p>Note that this makes field evaluation lazy so that functions can pull things out of a thread
 * local (typically hard to do if when loggers are set up initially).
 */
public class LogstashLoggingContext extends AbstractLoggingContext {

  private static final LogstashLoggingContext EMPTY =
      new LogstashLoggingContext(Collections::emptyList, Collections::emptyList);

  private final Supplier<List<Field>> fieldsSupplier;
  private final Supplier<List<Marker>> markersSupplier;

  private final Supplier<Marker> markersResult;

  protected LogstashLoggingContext(Supplier<List<Field>> f, Supplier<List<Marker>> m) {
    this.fieldsSupplier = f;
    this.markersSupplier = m;
    this.markersResult = Utilities.memoize(() -> {
      List<Marker> markers = getMarkers();
      if (markers.isEmpty()) {
        return null;
      } else if (markers.size() == 1) {
        return markers.get(0);
      } else {
        return Markers.aggregate(markers);
      }
    });
  }

  public static LogstashLoggingContext create(List<Field> fields) {
    return new LogstashLoggingContext(() -> fields, Collections::emptyList);
  }

  public static LogstashLoggingContext create(Field field) {
    return new LogstashLoggingContext(
        () -> Collections.singletonList(field), Collections::emptyList);
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

  public LogstashLoggingContext withFields(Supplier<List<Field>> o) {
    // existing context should be concatenated before the new fields
    Supplier<List<Field>> joinedFields = joinFields(this::getFields, o);
    return new LogstashLoggingContext(joinedFields, this::getMarkers);
  }

  public LogstashLoggingContext withMarkers(Supplier<List<Marker>> o) {
    Supplier<List<Marker>> joinedMarkers = joinMarkers(this::getMarkers, o);
    return new LogstashLoggingContext(this::getFields, joinedMarkers);
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
    Supplier<List<Field>> joinedFields = joinFields(this::getFields, context::getFields);
    Supplier<List<Marker>> joinedMarkers =
        joinMarkers(context::getMarkers, LogstashLoggingContext.this::getMarkers);
    return new LogstashLoggingContext(joinedFields, joinedMarkers);
  }

  static Supplier<List<Marker>> joinMarkers(
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

  static Supplier<List<Field>> joinFields(
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

  @Nullable
  org.slf4j.Marker resolveMarkers() {
    return markersResult.get();
  }

  // Convert markers explicitly.
  @Nullable
  org.slf4j.Marker resolveFieldsAndMarkers() {
    final List<Field> fields = getFields();
    if (fields.isEmpty()) {
      return markersResult.get();
    } else {
      final Marker marker = markersResult.get();
      final List<Marker> markerList = new ArrayList<>(fields.size() + 1);
      for (Field field : fields) {
        LogstashMarker append = Markers.append(field.name(), field.value());
        markerList.add(append);
      }
      if (marker != null) {
        markerList.add(marker);
      }
      return Markers.aggregate(markerList);
    }
  }
}
