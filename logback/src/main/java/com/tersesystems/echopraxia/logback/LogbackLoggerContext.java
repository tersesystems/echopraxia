package com.tersesystems.echopraxia.logback;

import com.tersesystems.echopraxia.api.Field;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/** The logback context associated with the logger across multiple logging events. */
public interface LogbackLoggerContext {

  @NotNull
  List<Field> getLoggerFields();

  List<Marker> getMarkers();
}
