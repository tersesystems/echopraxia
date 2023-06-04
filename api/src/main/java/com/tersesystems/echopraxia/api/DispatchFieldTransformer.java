package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A field transformer that dispatches the field to a FieldVisitor.
 *
 * @since 3.0
 */
public class DispatchFieldTransformer implements FieldTransformer {

  private final FieldVisitor visitor;

  public DispatchFieldTransformer(FieldVisitor visitor) {
    this.visitor = visitor;
  }

  @Override
  public @NotNull Field tranformArgumentField(@NotNull Field field) {
    return visitor.visit(field);
  }

  @Override
  public @NotNull Field transformLoggerField(@NotNull Field field) {
    return visitor.visit(field);
  }
}
