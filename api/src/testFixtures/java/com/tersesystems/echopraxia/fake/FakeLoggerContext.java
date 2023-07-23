package com.tersesystems.echopraxia.fake;

import static com.tersesystems.echopraxia.spi.Utilities.joinFields;
import static com.tersesystems.echopraxia.spi.Utilities.memoize;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.spi.LoggerContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class FakeLoggerContext implements LoggerContext {
  private final Supplier<List<Field>> fieldsSupplier;

  public static FakeLoggerContext empty() {
    return new FakeLoggerContext(Collections::emptyList);
  }

  public FakeLoggerContext(Supplier<List<Field>> fieldsSupplier) {
    this.fieldsSupplier = memoize(fieldsSupplier);
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return fieldsSupplier.get();
  }

  public LoggerContext withFields(Supplier<List<Field>> extraFields) {
    return new FakeLoggerContext(joinFields(fieldsSupplier, extraFields));
  }
}
