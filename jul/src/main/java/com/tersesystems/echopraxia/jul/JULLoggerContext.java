package com.tersesystems.echopraxia.jul;

import static com.tersesystems.echopraxia.api.Utilities.joinFields;

import com.tersesystems.echopraxia.api.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class JULLoggerContext {
  protected final Supplier<List<Field>> fieldsSupplier;

  private static final JULLoggerContext EMPTY = new JULLoggerContext();

  public static JULLoggerContext empty() {
    return EMPTY;
  }

  JULLoggerContext() {
    this.fieldsSupplier = Collections::emptyList;
  }

  protected JULLoggerContext(Supplier<List<Field>> f) {
    this.fieldsSupplier = f;
  }

  public @NotNull List<Field> getLoggerFields() {
    return fieldsSupplier.get();
  }

  public JULLoggerContext withFields(Supplier<List<Field>> o) {
    Supplier<List<Field>> joinedFields = joinFields(o, this::getLoggerFields);
    return new JULLoggerContext(joinedFields);
  }
}
