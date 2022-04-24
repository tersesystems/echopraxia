package com.tersesystems.echopraxia;

import com.jayway.jsonpath.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/** Methods for finding values and fields using JSON path syntax. */
public interface FindPathMethods {

  @NotNull
  <T extends Field.Value<?>> Optional<T> findValue(
      @NotNull String jsonPath, @NotNull Class<T> valueClass, Predicate... predicates);

  @NotNull
  <T extends Field.Value<?>> Optional<T> findValue(
      @NotNull String jsonPath, @NotNull Class<T> valueClass);

  /**
   * Finds a string value from the JSON path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return an optional string if found, otherwise empty().
   */
  @NotNull
  Optional<String> findString(@NotNull String jsonPath);

  /**
   * Finds a boolean value from the JSON path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return an optional boolean if found, otherwise empty().
   */
  @NotNull
  Optional<Boolean> findBoolean(@NotNull String jsonPath);

  /**
   * Finds a number value from the JSON path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return an optional number if found, otherwise empty().
   */
  @NotNull
  Optional<Number> findNumber(@NotNull String jsonPath);

  /**
   * Finds a null value from the JSON path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return true if null found, false otherwise.
   */
  boolean findNull(@NotNull String jsonPath);

  /**
   * Finds a throwable value from the JSON path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return optional throwable if found, empty() otherwise.
   */
  @NotNull
  Optional<Throwable> findThrowable(@NotNull String jsonPath);

  /**
   * Finds a throwable value using the default path
   *
   * @return optional throwable if found, empty() otherwise.
   */
  @NotNull
  Optional<Throwable> findThrowable();

  /**
   * Finds an object value using a json path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return optional map if found, empty() otherwise.
   * @param <T> the type of value in the map.
   */
  @NotNull
  <T> Optional<Map<String, T>> findObject(@NotNull String jsonPath);

  /**
   * Finds an object value using a json path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @param predicates the predicates to use.
   * @return optional map if found, empty() otherwise.
   * @param <T> the type of value in the map.
   */
  @NotNull
  <T> Optional<Map<String, T>> findObject(@NotNull String jsonPath, Predicate... predicates);

  /**
   * Finds a list using a json path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @return list containing elements, may be empty if nothing found.
   * @param <T> the type of value in the list.
   */
  @NotNull
  <T> List<T> findList(@NotNull String jsonPath);

  /**
   * Finds a list using a json path.
   *
   * @param jsonPath a JSON path to evaluate.
   * @param predicates the predicates to use.
   * @return optional map if found, empty() otherwise.
   * @param <T> the type of value in the list.
   */
  @NotNull
  <T> List<T> findList(@NotNull String jsonPath, Predicate... predicates);
}
