package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;

public class Log4JLoggerContext {
  protected final Supplier<List<Field>> fieldsSupplier;
  protected final Marker marker;

  Log4JLoggerContext() {
    this.fieldsSupplier = Collections::emptyList;
    this.marker = null;
  }

  protected Log4JLoggerContext(Supplier<List<Field>> f, Marker m) {
    this.fieldsSupplier = f;
    this.marker = m;
  }

  public @NotNull List<Field> getLoggerFields() {
    return fieldsSupplier.get();
  }

  public Marker getMarker() {
    return marker;
  }

  public Log4JLoggerContext withFields(Supplier<List<Field>> o) {
    Supplier<List<Field>> joinedFields = joinFields(o, this::getLoggerFields);
    return new Log4JLoggerContext(joinedFields, this.getMarker());
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

  /**
   * Joins the two contexts together, concatenating the lists in a supplier function.
   *
   * @param context the context to join
   * @return the new context containing fields and markers from both.
   */
  public Log4JLoggerContext and(Log4JLoggerContext context) {
    if (context != null) {
      Supplier<List<Field>> joinedFields =
          joinFields(context::getLoggerFields, this::getLoggerFields);
      Marker m = context.getMarker() != null ? context.getMarker() : this.marker;
      return new Log4JLoggerContext(joinedFields, m);
    } else {
      return this;
    }
  }
}
