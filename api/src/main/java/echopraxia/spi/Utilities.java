package echopraxia.spi;

import echopraxia.api.*;
import echopraxia.api.Field;
import echopraxia.api.Value;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/** Utilities for classes implementing a logger. */
public final class Utilities {

  @NotNull
  public static Function<Supplier<Map<String, String>>, Supplier<List<Field>>>
      getThreadContextFunction(@NotNull Function<Map<String, String>, List<Field>> f) {
    return mapSupplier -> () -> f.apply(mapSupplier.get());
  }

  public static <T> @NotNull Supplier<T> memoize(@NotNull Supplier<T> supplier) {
    return new MemoizingSupplier<>(supplier);
  }

  public @NotNull static List<Field> buildThreadContext(Map<String, String> contextMap) {
    if (contextMap == null || contextMap.isEmpty()) {
      return Collections.emptyList();
    }
    List<Field> list = new ArrayList<>();
    for (Map.Entry<String, String> e : contextMap.entrySet()) {
      Field field = Field.keyValue(e.getKey(), Value.string(e.getValue()));
      list.add(field);
    }
    return list;
  }

  public static Supplier<List<Field>> joinFields(
      Supplier<List<Field>> first, Supplier<List<Field>> second) {
    return () -> {
      List<Field> firstFields = first.get();
      List<Field> secondFields = second.get();

      if (firstFields.isEmpty()) {
        return secondFields;
      } else if (secondFields.isEmpty()) {
        return firstFields;
      } else {
        // Stream.concat is actually faster than explicit ArrayList!
        // https://blog.soebes.de/blog/2020/03/31/performance-stream-concat/
        return Stream.concat(firstFields.stream(), secondFields.stream())
            .collect(Collectors.toList());
      }
    };
  }

  public @NotNull static Function<Supplier<Map<String, String>>, Supplier<List<Field>>>
      threadContext() {
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
