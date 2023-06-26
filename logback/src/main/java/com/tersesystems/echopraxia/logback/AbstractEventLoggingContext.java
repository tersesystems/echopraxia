package com.tersesystems.echopraxia.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.spi.AbstractJsonPathFinder;
import com.tersesystems.echopraxia.api.LoggingContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

public abstract class AbstractEventLoggingContext extends AbstractJsonPathFinder
    implements LoggingContext {

  protected List<Field> fieldArguments(@NotNull ILoggingEvent event) {
    final Object[] argumentArray = event.getArgumentArray();
    if (argumentArray == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(argumentArray).flatMap(this::toField).collect(Collectors.toList());
  }

  protected List<Field> fieldMarkers(@NotNull ILoggingEvent event) {
    Marker m = event.getMarker();
    if (m == null) {
      return Collections.emptyList();
    }
    return markerStream(m).flatMap(this::toField).collect(Collectors.toList());
  }

  protected Stream<Marker> markerStream(@NotNull Marker m) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(m.iterator(), Spliterator.ORDERED), false);
  }

  protected Stream<Field> toField(Object arg) {
    return arg instanceof Field ? Stream.of((Field) arg) : Stream.empty();
  }

  public @NotNull Optional<Object> find(String path) {
    if (path == null) {
      return Optional.empty();
    }
    return optionalFind(path, Object.class);
  }
}
