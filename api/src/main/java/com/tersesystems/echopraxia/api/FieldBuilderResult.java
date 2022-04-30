package com.tersesystems.echopraxia.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
