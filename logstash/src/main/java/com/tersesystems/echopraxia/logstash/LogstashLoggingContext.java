package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
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

  protected final Supplier<List<Field>> fieldsSupplier;
  protected final Supplier<List<Marker>> markersSupplier;

  protected LogstashLoggingContext(Supplier<List<Field>> f, Supplier<List<Marker>> m) {
    this.fieldsSupplier = f;
    this.markersSupplier = m;
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
    if (context != null) {
      Supplier<List<Field>> joinedFields =
          () -> {
            final List<Field> listOne = LogstashLoggingContext.this.getFields();
            final List<Field> listTwo = context.getFields();
            return Stream.concat(listOne.stream(), listTwo.stream()).collect(Collectors.toList());
          };
      Supplier<List<Marker>> joinedMarkers =
          () -> {
            final List<Marker> listOne = LogstashLoggingContext.this.getMarkers();
            final List<Marker> listTwo = context.getMarkers();
            return Stream.concat(listOne.stream(), listTwo.stream()).collect(Collectors.toList());
          };
      return new LogstashLoggingContext(joinedFields, joinedMarkers);
    } else {
      return this;
    }
  }
}
