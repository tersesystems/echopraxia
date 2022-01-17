package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Marker;

public class Log4JLoggingContext implements LoggingContext {

  protected final Supplier<List<Field>> fieldsSupplier;
  protected final Supplier<List<Marker>> markersSupplier;

  protected Log4JLoggingContext(Supplier<List<Field>> f, Supplier<List<Marker>> m) {
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
  public Log4JLoggingContext and(Log4JLoggingContext context) {
    if (context != null) {
      Supplier<List<Field>> joinedFields =
          () -> {
            final List<Field> listOne = Log4JLoggingContext.this.getFields();
            final List<Field> listTwo = context.getFields();
            return Stream.concat(listOne.stream(), listTwo.stream()).collect(Collectors.toList());
          };
      Supplier<List<Marker>> joinedMarkers =
          () -> {
            final List<Marker> listOne = Log4JLoggingContext.this.getMarkers();
            final List<Marker> listTwo = context.getMarkers();
            return Stream.concat(listOne.stream(), listTwo.stream()).collect(Collectors.toList());
          };
      return new Log4JLoggingContext(joinedFields, joinedMarkers);
    } else {
      return this;
    }
  }
}
