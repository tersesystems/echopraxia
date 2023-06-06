package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * A field transformer that uses a FieldVisitor.
 *
 * @since 3.0
 */
public class VisitorFieldTransformer implements FieldTransformer {

  private final FieldVisitor visitor;

  public VisitorFieldTransformer(FieldVisitor visitor) {
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
