package com.tersesystems.echopraxia.logstash;

import org.jetbrains.annotations.NotNull;

/**
 * A marker containing caller data. This can be used by a filter to set caller data on a logging
 * event prior to encoding.
 */
public class LogstashCallerMarker extends LogstashBaseMarker {
  private final String fqcn;
  private final Throwable callsite;
  private StackTraceElement[] callerData;

  public LogstashCallerMarker(@NotNull String fqcn, @NotNull Throwable callsite) {
    super("caller");
    this.fqcn = fqcn;
    this.callsite = callsite;
  }

  public Throwable getCallSite() {
    return callsite;
  }

  public String getFqcn() {
    return fqcn;
  }
}
