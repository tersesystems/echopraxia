package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/**
 * This filter looks for a LogstashCallerMarker in the logging event.
 *
 * <p>If the marker is found, then it calls event.setCallerData(marker.getCallerData()).
 *
 * <p>This allows for an async logger to set the location data correctly, so it does not appear to
 * come from the executor thread.
 *
 * <p>Always returns FilterReply.NEUTRAL.
 */
public class LogstashCallerDataFilter extends Filter<ILoggingEvent> {

  @Override
  public FilterReply decide(@NotNull ILoggingEvent event) {
    final Marker marker = event.getMarker();
    if (marker != null) {
      final Iterator<Marker> iterator = marker.iterator();
      while (iterator.hasNext()) {
        final Marker next = iterator.next();
        if (next instanceof LogstashCallerMarker) {
          LogstashCallerMarker callerMarker = (LogstashCallerMarker) next;
          final LoggingEvent internalEvent = (LoggingEvent) event;
          if (shouldWriteCallerData(internalEvent)) {
            final String fqcn = callerMarker.getFqcn();
            final Throwable callSite = callerMarker.getCallSite();
            final StackTraceElement[] callerData = extractCallerData(fqcn, callSite);
            internalEvent.setCallerData(callerData);
          }
          return FilterReply.NEUTRAL;
        }
      }
    }
    return FilterReply.NEUTRAL;
  }

  // Filter is always set directly on the appender so...
  protected boolean shouldWriteCallerData(LoggingEvent internalEvent) {
    return true;
  }

  protected StackTraceElement[] extractCallerData(String fqcn, Throwable callsite) {
    LoggerContext loggerContext = (LoggerContext) getContext();
    return CallerData.extract(
        callsite,
        fqcn,
        loggerContext.getMaxCallerDataDepth(),
        loggerContext.getFrameworkPackages());
  }
}
