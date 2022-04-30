package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface FieldBuilderResult {
  List<Field> fields();

  static FieldBuilderResult empty() {
    return new FieldBuilderResult() {
      @Override
      public List<Field> fields() {
        return Collections.emptyList();
      }
    };
  }

  static FieldBuilderResult only(Field field) {
    return new FieldBuilderResult() {
      @Override
      public List<Field> fields() {
        return Collections.singletonList(field);
      }
    };
  }

  static FieldBuilderResult list(List<Field> list) {
    return new FieldBuilderResult() {
      @Override
      public List<Field> fields() {
        return list;
      }
    };
  }

  static FieldBuilderResult list(Field[] array) {
    return new FieldBuilderResult() {
      @Override
      public List<Field> fields() {
        return Arrays.asList(array);
      }
    };
  }

  static FieldBuilderResult list(Iterable<Field> iterable) {
      return list(iterable.spliterator());
  }

  static FieldBuilderResult list(Spliterator<Field> fieldSpliterator) {
    return list(StreamSupport.stream(fieldSpliterator, false));
  }

  static FieldBuilderResult list(Iterator<Field> iterator) {
    final Spliterator<Field> fieldSpliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
    return list(fieldSpliterator);
  }

  static FieldBuilderResult list(Stream<Field> stream) {
    return list(stream.collect(Collectors.toList()));
  }

}
