package com.tersesystems.echopraxia.support;

import com.tersesystems.echopraxia.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/** Utilities for classes implementing a logger. */
public final class Utilities {

  @NotNull
  public static <T extends Field.Builder> T getNewInstance(@NotNull Class<T> newBuilderClass) {
    try {
      return newBuilderClass.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  @NotNull
  public static <FB extends Field.Builder>
      Function<Supplier<Map<String, String>>, Supplier<List<Field>>> getThreadContextFunction(
          @NotNull FB fieldBuilder) {
    return mapSupplier ->
        () -> {
          List<Field> list = new ArrayList<>();
          for (Map.Entry<String, String> e : mapSupplier.get().entrySet()) {
            Field string = fieldBuilder.string(e.getKey(), e.getValue());
            list.add(string);
          }
          return list;
        };
  }
}
