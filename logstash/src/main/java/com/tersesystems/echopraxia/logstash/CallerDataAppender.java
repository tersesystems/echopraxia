package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import java.util.Iterator;
import org.slf4j.Marker;

/** An appender that sets the caller data on the event from a marker if it exists. */
public class CallerDataAppender extends TransformingAppender<ILoggingEvent> {
  @Override
  protected ILoggingEvent decorateEvent(ILoggingEvent eventObject) {
    return setCallerData(eventObject);
  }

  protected ILoggingEvent setCallerData(ILoggingEvent event) {
    final Marker marker = event.getMarker();
    if (marker != null) {
      final Iterator<Marker> iterator = marker.iterator();
      while (iterator.hasNext()) {
        final Marker next = iterator.next();
        if (next instanceof LogstashCallerMarker) {
          LogstashCallerMarker callerMarker = (LogstashCallerMarker) next;
          final LoggingEvent internalEvent = (LoggingEvent) event;
          final String fqcn = callerMarker.getFqcn();
          final Throwable callSite = callerMarker.getCallSite();
          final StackTraceElement[] callerData = extractCallerData(fqcn, callSite);
          internalEvent.setCallerData(callerData);
        }
      }
    }
    return event;
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
