package com.tersesystems.echopraxia.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

abstract class AbstractPathConverter extends ClassicConverter {

  protected String jsonPath;

  public void start() {
    String optStr = this.getFirstOption();
    if (optStr != null) {
      this.jsonPath = optStr;
      super.start();
    }

    if (this.jsonPath == null) {
      throw new IllegalStateException("JSON path is not specified");
    }
  }

  @Override
  public String convert(ILoggingEvent event) {
    AbstractEventLoggingContext ctx = getLoggingContext(event);
    try {
      if (ctx.getFields().isEmpty()) {
        return "";
      } else {
        final Optional<Object> optObject = ctx.find(jsonPath);
        return optObject.map(o -> o.toString()).orElse("");
      }
    } catch (Exception e) {
      addError("Cannot convert path " + jsonPath, e);
      return "";
    }
  }

  @NotNull
  protected abstract AbstractEventLoggingContext getLoggingContext(ILoggingEvent event);
}
