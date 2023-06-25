package com.tersesystems.echopraxia.spi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class Filters {

  private final List<CoreLoggerFilter> filterList;

  private static final String PROPERTIES_FILE = "echopraxia.properties";

  public Filters(@NotNull ClassLoader[] classLoaders) {
    InputStream inputStream = CoreLoggerFactory.class.getResourceAsStream("/" + PROPERTIES_FILE);

    if (inputStream != null) {
      try {
        Properties props = new Properties();
        props.load(inputStream);
        filterList = getFilters(props, classLoaders);
      } catch (IOException e) {
        throw new ServiceConfigurationError(e.getMessage(), e);
      }
    } else {
      filterList = Collections.emptyList();
    }
  }

  public Filters(List<CoreLoggerFilter> filterList) {
    this.filterList = filterList;
  }

  @NotNull
  private List<CoreLoggerFilter> getFilters(
      @NotNull Properties props, @NotNull ClassLoader[] classLoaders) {
    List<String> result = new LinkedList<>();
    String value;
    for (int i = 0; (value = props.getProperty("filter." + i)) != null; i++) {
      result.add(value);
    }
    Stream<String> stream = result.stream();
    return stream
        .map(className -> this.getFilterInstance(classLoaders, className))
        .collect(Collectors.toList());
  }

  Class<?> loadClass(@NotNull ClassLoader[] classLoaders, @NotNull String className)
      throws ClassNotFoundException {
    List<ClassNotFoundException> exceptions = new ArrayList<>();
    for (int i = 0; i < classLoaders.length; i++) {
      try {
        @NotNull ClassLoader classLoader = classLoaders[i];
        return Class.forName(className, true, classLoader);
      } catch (ClassNotFoundException e) {
        exceptions.add(e);
      }
    }

    // Look up the context class loader if necessary.
    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    if (contextClassLoader != null) {
      try {
        return Class.forName(className, true, contextClassLoader);
      } catch (ClassNotFoundException e) {
        exceptions.add(e);
      }
    }
    @NotNull
    String msg =
        "No class found "
            + className
            + " using classLoaders "
            + Arrays.asList(classLoaders)
            + ", exceptions = "
            + exceptions;
    throw new ClassNotFoundException(msg);
  }

  @NotNull
  private CoreLoggerFilter getFilterInstance(
      @NotNull ClassLoader[] classLoaders, @NotNull String className) {
    try {
      Class<?> aClass = loadClass(classLoaders, className);
      if (!CoreLoggerFilter.class.isAssignableFrom(aClass)) {
        String msg = "Class " + className + " does not implement CoreLoggerFilter";
        throw new ServiceConfigurationError(msg);
      }
      //noinspection unchecked
      Class<CoreLoggerFilter> filterClass = (Class<CoreLoggerFilter>) aClass;
      Constructor<CoreLoggerFilter> declaredConstructor = filterClass.getDeclaredConstructor();
      return declaredConstructor.newInstance();
    } catch (NoSuchMethodException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException
        | ClassNotFoundException e) {
      throw new ServiceConfigurationError(
          "Cannot create an instance from " + className + ": " + e.getMessage(), e);
    }
  }

  @NotNull
  public CoreLogger apply(@NotNull CoreLogger core) {
    CoreLogger c = core;
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, filterListSize = filterList.size(); i < filterListSize; i++) {
      CoreLoggerFilter next = filterList.get(i);
      c = next.apply(c);
    }
    return c;
  }
}
