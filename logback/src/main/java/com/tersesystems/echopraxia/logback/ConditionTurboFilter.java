package com.tersesystems.echopraxia.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.echopraxia.api.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/**
 * A filter that looks for a ConditionMarker, and evaluates the condition based on a logging context
 * constructed from the logging event.
 *
 * <p>If the condition passes, returns NEUTRAL, otherwise DENY.
 */
public class ConditionTurboFilter extends TurboFilter {

  @Override
  public FilterReply decide(
      Marker marker,
      Logger logger,
      ch.qos.logback.classic.Level level,
      String s,
      Object[] arguments,
      Throwable throwable) {
    if (marker == null) {
      return FilterReply.NEUTRAL;
    }

    final Level echoLevel = level(level);
    LoggingContext loggingContext = null;

    // Check if it's the only marker (passed in by hand?)
    if (marker instanceof ConditionMarker) {
      loggingContext = loggingContext(marker, arguments);
      Condition condition = ((ConditionMarker) marker).getCondition();
      if (!condition.test(echoLevel, loggingContext)) {
        return FilterReply.DENY;
      }
    }

    // Check through any children
    final Iterator<Marker> iterator = marker.iterator();
    while (iterator.hasNext()) {
      Marker m = iterator.next();
      if (m instanceof ConditionMarker) {
        Condition condition = ((ConditionMarker) m).getCondition();
        if (loggingContext == null) {
          loggingContext = loggingContext(marker, arguments);
        }
        if (!condition.test(echoLevel, loggingContext)) {
          return FilterReply.DENY;
        }
      }
    }

    // Nothing failed, return neutral
    return FilterReply.NEUTRAL;
  }

  private Level level(ch.qos.logback.classic.Level level) {
    return Level.valueOf(level.levelStr);
  }

  private LoggingContext loggingContext(Marker marker, Object[] arguments) {
    Iterable<Marker> iterable = marker::iterator;
    List<Marker> markers =
        StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    FilterMarkerContext markerContext = new FilterMarkerContext(markers);
    return new LogbackLoggingContext(markerContext, () -> getFields(arguments));
  }

  @NotNull
  private List<Field> getFields(Object[] argumentArray) {
    List<Field> fields;
    if (argumentArray == null || argumentArray.length == 0) {
      fields = Collections.emptyList();
    } else {
      fields = new ArrayList<>();
      for (Object arg : argumentArray) {
        if (arg instanceof Field) {
          fields.add((Field) arg);
        }
        if (arg instanceof FieldBuilderResult) {
          FieldBuilderResult result = (FieldBuilderResult) arg;
          fields.addAll(result.fields());
        }
      }
    }
    return fields;
  }

  static class FilterMarkerContext implements LogbackLoggerContext {
    private final List<Marker> markers;

    public FilterMarkerContext(List<Marker> markers) {
      this.markers = markers;
    }

    @Override
    public @NotNull List<Field> getLoggerFields() {
      return Collections.emptyList();
    }

    @Override
    public List<Marker> getMarkers() {
      return markers;
    }
  }
}
