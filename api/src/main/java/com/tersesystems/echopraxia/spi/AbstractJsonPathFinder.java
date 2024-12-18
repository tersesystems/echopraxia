package com.tersesystems.echopraxia.spi;

import static com.tersesystems.echopraxia.spi.FieldConstants.*;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.tersesystems.echopraxia.model.Value;
import com.tersesystems.echopraxia.model.Value.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

/** An abstract path finder that implements the methods using Jayway JSONPath. */
public abstract class AbstractJsonPathFinder implements FindPathMethods {
  private static final String EXCEPTION_PATH = "$." + EXCEPTION;

  private static final JsonProvider jsonProvider = new EchopraxiaJsonProvider();
  private static final MappingProvider javaMappingProvider = new EchopraxiaMappingProvider();

  private static final Configuration configuration =
      Configuration.builder()
          .jsonProvider(jsonProvider)
          .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
          .options(Option.SUPPRESS_EXCEPTIONS)
          .mappingProvider(javaMappingProvider)
          .build();

  private final Supplier<DocumentContext> supplier =
      Utilities.memoize(() -> JsonPath.parse(this, configuration));

  /**
   * Finds an Optional of type T given a path and a desired class of T.
   *
   * @param jsonPath the json path to the object
   * @param desiredClass the desired class type
   * @return an optional containing the instance if exists and can be mapped, otherwise empty().
   * @param <T> the type
   */
  protected @NotNull <T> Optional<T> optionalFind(
      @NotNull @Language("JSONPath") String jsonPath, @NotNull Class<T> desiredClass) {
    final DocumentContext documentContext = getDocumentContext();
    final Object o = documentContext.read(jsonPath);

    // We asked for Foo, and it was Foo.  Return Foo.
    if (desiredClass.isInstance(o)) {
      return Optional.of(desiredClass.cast(o));
    }

    // Well, it's not that value.  Let's try mapping it.
    final T convertedObject = javaMappingProvider.map(o, desiredClass, configuration);
    if (desiredClass.isInstance(convertedObject)) {
      return Optional.of(convertedObject);
    }

    // Still nope.
    return Optional.empty();
  }

  @Override
  @NotNull
  public Optional<String> findString(@NotNull @Language("JSONPath") String jsonPath) {
    // Not all strings are mapped string values.
    // $.exception.message is a string but was never a string value,
    return optionalFind(jsonPath, String.class);
  }

  @Override
  @NotNull
  public Optional<Boolean> findBoolean(@NotNull @Language("JSONPath") String jsonPath) {
    return optionalFind(jsonPath, Boolean.class);
  }

  @Override
  @NotNull
  public Optional<Number> findNumber(@NotNull @Language("JSONPath") String jsonPath) {
    return optionalFind(jsonPath, Number.class);
  }

  public boolean findNull(@NotNull String jsonPath) {
    // $.exception.message where message == null is also a null
    // but was never a null value.
    Object o = getDocumentContext().read(jsonPath);
    return o == null || o instanceof Value.NullValue;
  }

  @Override
  @NotNull
  public Optional<Throwable> findThrowable(@NotNull @Language("JSONPath") String jsonPath) {
    // Pretty sure exceptions are always exception values.
    return optionalFind(jsonPath, ExceptionValue.class).map(ExceptionValue::raw);
  }

  @Override
  @NotNull
  public Optional<Throwable> findThrowable() {
    return findThrowable(EXCEPTION_PATH);
  }

  @SuppressWarnings("unchecked")
  @Override
  @NotNull
  public Optional<Map<String, ?>> findObject(@NotNull @Language("JSONPath") String jsonPath) {
    return optionalFind(jsonPath, Map.class).map(f -> (Map<String, ?>) f);
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull List<?> findList(@NotNull @Language("JSONPath") String jsonPath) {
    // finding a list has two different meanings in JSONPath
    // The first one is that you asked for a JSON array and it gives you
    // a json array.  Simple.
    // The second is when JSONPath does a deep scan or some kind of query
    // of the JSON document and returns matches.  This could have pretty
    // much anything in it, but may contain a List<ArrayValue>.
    // The third is when the exception is queried: $.exception.stackTrace
    // in particular.
    //
    // So we have to do some special case logic here beyond just optionalFind.
    final Object o = getDocumentContext().read(jsonPath);
    if (o instanceof ArrayValue || o instanceof List || o instanceof Object[]) {
      return javaMappingProvider.map(o, List.class, configuration);
    } else if (o != null) {
      // $.exception returns an individual exception rather than a list.
      Value<?> value = (Value<?>) o;
      return Collections.singletonList(value.raw());
    } else {
      return Collections.emptyList();
    }
  }

  private DocumentContext getDocumentContext() {
    return supplier.get();
  }
}
