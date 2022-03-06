package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.support.DefaultLoggingContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class FakeLoggingContext implements DefaultLoggingContext {

  private static final FakeLoggingContext EMPTY = new FakeLoggingContext(Collections::emptyList);

  protected final Supplier<List<Field>> fieldsSupplier;

  protected FakeLoggingContext(Supplier<List<Field>> f) {
    this.fieldsSupplier = f;
  }

  public static FakeLoggingContext single(Field field) {
    return new FakeLoggingContext(() -> Collections.singletonList(field));
  }

  public static FakeLoggingContext of(Field... fields) {
    return new FakeLoggingContext(() -> Arrays.asList(fields));
  }

  public static FakeLoggingContext empty() {
    return EMPTY;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fieldsSupplier.get();
  }

  public FakeLoggingContext and(FakeLoggingContext context) {
    if (context == null) {
      return this;
    }

    // This MUST be lazy, we can't get the fields until statement evaluation
    Supplier<List<Field>> joinedFields =
        joinFields(FakeLoggingContext.this::getFields, context::getFields);
    return new FakeLoggingContext(joinedFields);
  }

  private Supplier<List<Field>> joinFields(
      Supplier<List<Field>> thisFieldsSupplier, Supplier<List<Field>> ctxFieldsSupplier) {
    return () -> {
      List<Field> thisFields = thisFieldsSupplier.get();
      List<Field> ctxFields = ctxFieldsSupplier.get();

      if (thisFields.isEmpty()) {
        return ctxFields;
      } else if (ctxFields.isEmpty()) {
        return thisFields;
      } else {
        // Stream.concat is actually faster than explicit ArrayList!
        // https://blog.soebes.de/blog/2020/03/31/performance-stream-concat/
        return Stream.concat(thisFields.stream(), ctxFields.stream()).collect(Collectors.toList());
      }
    };
  }
}
