package com.tersesystems.echopraxia.api;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.Nullable;

public class FieldConversion implements Function<Object, List<Field>> {
  @SuppressWarnings("unchecked")
  public List<Field> apply(@Nullable Object input) {
    if (input instanceof List) {
      return (List<Field>) input;
    } else if (input instanceof Field) {
      return Collections.singletonList((Field) input);
    } else if (input instanceof Iterable) {
      final Spliterator<Field> iterator = ((Iterable<Field>) input).spliterator();
      return StreamSupport.stream(iterator, false).collect(Collectors.toList());
    } else if (input instanceof Iterator) {
      final Iterator<Field> iterator = (Iterator<Field>) input;
      final Spliterator<Field> fieldSpliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
      return StreamSupport.stream(fieldSpliterator, false).collect(Collectors.toList());
    } else if (input instanceof Stream) {
      return ((Stream<Field>) input).collect(Collectors.toList());
    } else if (input instanceof Field[]) {
      return Arrays.asList((Field[]) input);
    } else {
      return Collections.emptyList();
    }
  }
}
