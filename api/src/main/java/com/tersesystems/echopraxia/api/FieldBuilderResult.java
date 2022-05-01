package com.tersesystems.echopraxia.api;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface FieldBuilderResult {
  List<Field> fields();

  static FieldBuilderResult empty() {
    return Collections::emptyList;
  }

  static FieldBuilderResult only(Field field) {
    return () -> Collections.singletonList(field);
  }

  static FieldBuilderResult list(List<Field> list) {
    return () -> list;
  }

  static FieldBuilderResult list(Field[] array) {
    return () -> Arrays.asList(array);
  }

  static FieldBuilderResult list(Iterable<Field> iterable) {
    return list(iterable.spliterator());
  }

  static FieldBuilderResult list(Spliterator<Field> fieldSpliterator) {
    return list(StreamSupport.stream(fieldSpliterator, false));
  }

  static FieldBuilderResult list(Iterator<Field> iterator) {
    return list(Spliterators.spliteratorUnknownSize(iterator, 0));
  }

  static FieldBuilderResult list(Stream<Field> stream) {
    return list(stream.collect(Collectors.toList()));
  }
}
