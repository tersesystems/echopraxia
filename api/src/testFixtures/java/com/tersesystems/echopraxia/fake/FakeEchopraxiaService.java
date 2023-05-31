package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.*;
import org.jetbrains.annotations.NotNull;

public class FakeEchopraxiaService extends AbstractEchopraxiaService {

  public static class MyField extends DefaultField {

    protected MyField(String name, Value<?> value, Attributes attributes) {
      super(name, value, attributes);
    }
  }

  public FakeEchopraxiaService() {
    super();
    this.fieldCreator =
        new DefaultFieldCreator() {
          @Override
          public Field keyValue(String name, Value<?> value) {
            return new MyField(name, value, Attributes.empty());
          }

          @Override
          public Field value(String name, Value<?> value) {
            return new MyField(name, value, FieldAttributes.valueOnlyAttributes());
          }
        };
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return new FakeCoreLogger(fqcn);
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    return new FakeCoreLogger(fqcn);
  }
}
