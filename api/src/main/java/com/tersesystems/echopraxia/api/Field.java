package com.tersesystems.echopraxia.api;

import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * The Field interface. This is a core part of structured data, and consists of a name and a Value,
 * where a value corresponds roughly to the JSON infoset: string, number, boolean, null, array, and
 * object.
 *
 * <p>The field builder interface and custom field builders go a long way to building up more
 * complex structures, please see documentation for how to use them.
 */
public interface Field {

  String EXCEPTION = "exception";

  /**
   * The field name.
   *
   * @return the field name.
   */
  @NotNull
  String name();

  /**
   * The field value.
   *
   * @return the field value.
   */
  @NotNull
  Value<?> value();

  /**
   * The BuilderFunction interface. This is used when logging arguments, so that a field builder can
   * return a list of fields.
   *
   * @param <FB> the field builder type.
   */
  @FunctionalInterface
  interface BuilderFunction<FB> extends Function<FB, List<Field>> {}
}
