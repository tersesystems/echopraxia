package com.tersesystems.echopraxia.logstash;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/** A logstash specific interface that returns a context with markers, for use in conditions. */
public interface MarkerLoggingContext {
  @NotNull
  List<Marker> getMarkers();
}
