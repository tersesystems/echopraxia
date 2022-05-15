package com.tersesystems.echopraxia.api;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;

public interface FieldBuilderResult {

  @NotNull
  List<Field> fields();

  static @NotNull FieldBuilderResult empty() {
    return Collections::emptyList;
  }

  static @NotNull FieldBuilderResult only(@NotNull Field field) {
    return () -> Collections.singletonList(field);
  }

  static @NotNull FieldBuilderResult list(@NotNull List<Field> list) {
    return () -> new CopyOnWriteArrayList<>(list);
  }

  static @NotNull FieldBuilderResult list(@NotNull Field[] array) {
    return () -> new CopyOnWriteArrayList<>(array);
  }

  static @NotNull FieldBuilderResult list(@NotNull Iterable<Field> iterable) {
    return list(iterable.spliterator());
  }

  static @NotNull FieldBuilderResult list(@NotNull Spliterator<Field> fieldSpliterator) {
    return list(StreamSupport.stream(fieldSpliterator, false));
  }

  static @NotNull FieldBuilderResult list(@NotNull Iterator<Field> iterator) {
    return list(Spliterators.spliteratorUnknownSize(iterator, 0));
  }

  static @NotNull FieldBuilderResult list(@NotNull Stream<Field> stream) {
    return list(stream.collect(Collectors.toList()));
  }
}
