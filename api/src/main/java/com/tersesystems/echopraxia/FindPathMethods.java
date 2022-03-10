package com.tersesystems.echopraxia;

import com.jayway.jsonpath.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public interface FindPathMethods {

  @NotNull
  Optional<String> findString(@NotNull String jsonPath);

  @NotNull
  Optional<Boolean> findBoolean(@NotNull String jsonPath);

  @NotNull
  Optional<Number> findNumber(@NotNull String jsonPath);

  boolean findNull(@NotNull String jsonPath);

  @NotNull
  Optional<Throwable> findThrowable(@NotNull String jsonPath);

  @NotNull
  Optional<Throwable> findThrowable();

  @NotNull
  <T> Optional<Map<String, T>> findObject(@NotNull String jsonPath);

  @NotNull
  <T> Optional<Map<String, T>> findObject(@NotNull String jsonPath, Predicate... predicates);

  @NotNull
  <T> List<T> findList(@NotNull String jsonPath);

  @NotNull
  <T> List<T> findList(@NotNull String jsonPath, Predicate... predicates);
}
