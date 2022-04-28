package com.tersesystems.echopraxia.api;

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
  public static Function<Supplier<Map<String, String>>, Supplier<List<Field>>>
      getThreadContextFunction(@NotNull Function<Map<String, String>, List<Field>> f) {
    return mapSupplier -> () -> f.apply(mapSupplier.get());
  }

  public static <T> Supplier<T> memoize(Supplier<T> supplier) {
    return new MemoizingSupplier<>(supplier);
  }

  public static List<Field> buildThreadContext(Map<String, String> contextMap) {
    List<Field> list = new ArrayList<>();
    for (Map.Entry<String, String> e : contextMap.entrySet()) {
      Field field = KeyValueField.create(e.getKey(), Field.Value.string(e.getValue()));
      list.add(field);
    }
    return list;
  }

  public static Function<Supplier<Map<String, String>>, Supplier<List<Field>>> threadContext() {
    return getThreadContextFunction(Utilities::buildThreadContext);
  }

  @NotNull
  static class MemoizingSupplier<T> implements Supplier<T> {
    final Supplier<T> delegate;
    transient volatile boolean initialized;
    transient T value;

    MemoizingSupplier(Supplier<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T get() {
      if (!initialized) {
        synchronized (this) {
          if (!initialized) {
            T t = delegate.get();
            value = t;
            initialized = true;
            return t;
          }
        }
      }
      return value;
    }
  }
}
