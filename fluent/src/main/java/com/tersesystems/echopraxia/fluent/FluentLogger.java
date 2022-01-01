package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FluentLogger<FB extends Field.Builder> {

  private final CoreLogger core;
  private final FB builder;

  public FluentLogger(CoreLogger core, FB builder) {
    this.core = core;
    this.builder = builder;
  }

  public EntryBuilder atError() {
    return atLevel(Level.ERROR);
  }

  public EntryBuilder atWarn() {
    return atLevel(Level.WARN);
  }

  public EntryBuilder atInfo() {
    return atLevel(Level.INFO);
  }

  public EntryBuilder atDebug() {
    return atLevel(Level.DEBUG);
  }

  public EntryBuilder atTrace() {
    return atLevel(Level.TRACE);
  }

  public EntryBuilder atLevel(Level level) {
    return new EntryBuilder(level);
  }

  public class EntryBuilder {
    private final Level level;
    private String message;
    private final List<Function<FB, Field>> argumentFnList = new ArrayList<>();

    EntryBuilder(Level level) {
      this.level = level;
    }

    public EntryBuilder message(String message) {
      this.message = message;
      return this;
    }

    public EntryBuilder argument(Function<FB, Field> f) {
      this.argumentFnList.add(f);
      return this;
    }

    public EntryBuilder exception(Throwable t) {
      return argument(b -> b.exception(t));
    }

    public void log() {
      core.log(
          level,
          message,
          b -> argumentFnList.stream().map(f -> f.apply(b)).collect(Collectors.toList()),
          builder);
    }
  }
}
