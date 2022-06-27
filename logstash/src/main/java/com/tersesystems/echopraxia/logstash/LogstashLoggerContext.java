package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.logstash.logback.marker.Markers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

/** A logging context that stores fields belonging to the logger. */
public class LogstashLoggerContext {

  private static final LogstashLoggerContext EMPTY =
      new LogstashLoggerContext(Collections::emptyList, Collections::emptyList);

  private final Supplier<List<Field>> fieldsSupplier;
  private final Supplier<List<Marker>> markersSupplier;

  private final Supplier<Marker> markersResult;

  protected LogstashLoggerContext(Supplier<List<Field>> f, Supplier<List<Marker>> m) {
    this.fieldsSupplier = f;
    this.markersSupplier = m;
    this.markersResult =
        Utilities.memoize(
            () -> {
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

  public static LogstashLoggerContext create(List<Field> fields) {
    return new LogstashLoggerContext(() -> fields, Collections::emptyList);
  }

  public static LogstashLoggerContext create(Field field) {
    return new LogstashLoggerContext(
        () -> Collections.singletonList(field), Collections::emptyList);
  }

  public static LogstashLoggerContext empty() {
    return EMPTY;
  }

  public @NotNull List<Field> getLoggerFields() {
    return fieldsSupplier.get();
  }

  public @NotNull List<Marker> getMarkers() {
    return markersSupplier.get();
  }

  public LogstashLoggerContext withFields(Supplier<List<Field>> o) {
    // existing context should be concatenated before the new fields
    Supplier<List<Field>> joinedFields = joinFields(this::getLoggerFields, o);
    return new LogstashLoggerContext(joinedFields, this::getMarkers);
  }

  public LogstashLoggerContext withMarkers(Supplier<List<Marker>> o) {
    Supplier<List<Marker>> joinedMarkers = joinMarkers(this::getMarkers, o);
    return new LogstashLoggerContext(this::getLoggerFields, joinedMarkers);
  }

  /**
   * Joins the two contexts together, concatenating the lists in a supplier function.
   *
   * @param context the context to join
   * @return the new context containing fields and markers from both.
   */
  public LogstashLoggerContext and(LogstashLoggerContext context) {
    if (context == null) {
      return this;
    }

    // This MUST be lazy, we can't get the fields until statement evaluation
    Supplier<List<Field>> joinedFields =
        joinFields(this::getLoggerFields, context::getLoggerFields);
    Supplier<List<Marker>> joinedMarkers =
        joinMarkers(context::getMarkers, LogstashLoggerContext.this::getMarkers);
    return new LogstashLoggerContext(joinedFields, joinedMarkers);
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
      Supplier<List<Field>> first, Supplier<List<Field>> second) {
    return () -> {
      List<Field> firstFields = first.get();
      List<Field> secondFields = second.get();

      if (firstFields.isEmpty()) {
        return secondFields;
      } else if (secondFields.isEmpty()) {
        return firstFields;
      } else {
        // Stream.concat is actually faster than explicit ArrayList!
        // https://blog.soebes.de/blog/2020/03/31/performance-stream-concat/
        return Stream.concat(firstFields.stream(), secondFields.stream())
            .collect(Collectors.toList());
      }
    };
  }

  @Nullable
  org.slf4j.Marker resolveMarkers() {
    // Markers are always resolved on isEnabled, but contexts can also be
    // composed with each other, so we don't want every single context's marker,
    // only the final result.
    return markersResult.get();
  }
}
