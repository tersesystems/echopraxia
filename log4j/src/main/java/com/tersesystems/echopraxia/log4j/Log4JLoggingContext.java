package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.support.DefaultLoggingContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;

public class Log4JLoggingContext implements DefaultLoggingContext {

  protected final Supplier<List<Field>> fieldsSupplier;
  protected final Marker marker;

  Log4JLoggingContext() {
    this.fieldsSupplier = Collections::emptyList;
    this.marker = null;
  }

  protected Log4JLoggingContext(Supplier<List<Field>> f, Marker m) {
    this.fieldsSupplier = f;
    this.marker = m;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fieldsSupplier.get();
  }

  public Marker getMarker() {
    return marker;
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
      Marker m = context.getMarker() != null ? context.getMarker() : this.marker;
      return new Log4JLoggingContext(joinedFields, m);
    } else {
      return this;
    }
  }
}
