package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;

public class Log4JLoggingContext extends AbstractLoggingContext {
  protected final Supplier<List<Field>> fieldsSupplier;
  protected final Marker marker;

  Log4JLoggingContext() {
    this.fieldsSupplier = Collections::emptyList;
    this.marker = null;
  }

  protected Log4JLoggingContext(Supplier<List<Field>> f, Marker m) {
    this.fieldsSupplier = Utilities.memoize(f);
    this.marker = m;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fieldsSupplier.get();
  }

  public Marker getMarker() {
    return marker;
  }

  public Log4JLoggingContext withFields(Supplier<List<Field>> o) {
    Supplier<List<Field>> joinedFields = joinFields(o, this::getFields);
    return new Log4JLoggingContext(joinedFields, this.getMarker());
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

  /**
   * Joins the two contexts together, concatenating the lists in a supplier function.
   *
   * @param context the context to join
   * @return the new context containing fields and markers from both.
   */
  public Log4JLoggingContext and(Log4JLoggingContext context) {
    if (context != null) {
      Supplier<List<Field>> joinedFields = joinFields(context::getFields, this::getFields);
      Marker m = context.getMarker() != null ? context.getMarker() : this.marker;
      return new Log4JLoggingContext(joinedFields, m);
    } else {
      return this;
    }
  }
}
