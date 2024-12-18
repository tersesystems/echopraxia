package com.tersesystems.echopraxia.noop;

import static com.tersesystems.echopraxia.spi.Utilities.joinFields;
import static com.tersesystems.echopraxia.spi.Utilities.memoize;

import com.tersesystems.echopraxia.model.Field;
import com.tersesystems.echopraxia.spi.LoggerContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class NoopLoggerContext implements LoggerContext {
  private final Supplier<List<Field>> fieldsSupplier;

  public static NoopLoggerContext empty() {
    return new NoopLoggerContext(Collections::emptyList);
  }

  public NoopLoggerContext(Supplier<List<Field>> fieldsSupplier) {
    this.fieldsSupplier = memoize(fieldsSupplier);
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return fieldsSupplier.get();
  }

  public LoggerContext withFields(Supplier<List<Field>> extraFields) {
    return new NoopLoggerContext(joinFields(fieldsSupplier, extraFields));
  }
}
