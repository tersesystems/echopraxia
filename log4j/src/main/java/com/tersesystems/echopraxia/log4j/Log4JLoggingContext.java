package com.tersesystems.echopraxia.log4j;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.EchopraxiaJsonProvider;
import com.tersesystems.echopraxia.api.EchopraxiaMappingProvider;
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

  private static final Configuration configuration =
      Configuration.builder()
          .jsonProvider(new EchopraxiaJsonProvider())
          .mappingProvider(new EchopraxiaMappingProvider())
          .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
          .build();

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
