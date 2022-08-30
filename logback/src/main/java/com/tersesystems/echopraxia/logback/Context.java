package com.tersesystems.echopraxia.logback;

import com.tersesystems.echopraxia.api.Field;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Marker;

public interface Context {

  Supplier<List<Field>> getLoggerFields();

  List<Marker> getMarkers();
}
