package echopraxia.logging.fake;

import static echopraxia.logging.spi.Utilities.joinFields;
import static echopraxia.logging.spi.Utilities.memoize;

import echopraxia.api.Field;
import echopraxia.logging.spi.LoggerContext;
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
