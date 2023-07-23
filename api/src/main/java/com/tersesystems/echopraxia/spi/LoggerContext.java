package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.api.Field;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface LoggerContext {

  @NotNull
  List<Field> getLoggerFields();
}
