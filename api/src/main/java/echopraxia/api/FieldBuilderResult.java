package echopraxia.api;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is the result end of the FieldBuilder -> FieldBuilderResult function used for
 * arguments in loggers. It abstracts away the "list of fields" vs "single field" result issue we'd
 * have otherwise.
 */
public interface FieldBuilderResult {

  @NotNull
  List<Field> fields();

  @Contract(pure = true)
  static @NotNull FieldBuilderResult empty() {
    return Collections::emptyList;
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult flatten(Supplier<FieldBuilderResult> supplier) {
    return () -> supplier.get().fields();
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult only(@NotNull Field field) {
    return () -> Collections.singletonList(field);
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(@NotNull List<Field> list) {
    return () -> list;
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(@NotNull Field[] array) {
    return () -> Arrays.asList(array);
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(FieldBuilderResult[] results) {
    return () ->
        Arrays.stream(results).flatMap(f -> f.fields().stream()).collect(Collectors.toList());
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(@NotNull Iterable<Field> iterable) {
    return list(iterable.spliterator());
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(@NotNull Spliterator<Field> fieldSpliterator) {
    return list(StreamSupport.stream(fieldSpliterator, false));
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(@NotNull Iterator<Field> iterator) {
    return list(Spliterators.spliteratorUnknownSize(iterator, 0));
  }

  @Contract(pure = true)
  static @NotNull FieldBuilderResult list(@NotNull Stream<Field> stream) {
    return () -> stream.collect(Collectors.toList());
  }
}
