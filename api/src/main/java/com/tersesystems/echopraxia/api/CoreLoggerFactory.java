package com.tersesystems.echopraxia.api;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * The core logger factory.
 *
 * <p>This is internal, and is intended for service provider implementations.
 */
public class CoreLoggerFactory {

  private static final Filters filters = new Filters(ClassLoader.getSystemClassLoader());

  @NotNull
  public static CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    CoreLogger core = LazyHolder.INSTANCE.getLogger(fqcn, clazz);
    return processFilters(core);
  }

  @NotNull
  public static CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    CoreLogger core = LazyHolder.INSTANCE.getLogger(fqcn, name);
    return processFilters(core);
  }

  @NotNull
  private static CoreLogger processFilters(@NotNull CoreLogger core) {
    return filters.apply(core);
  }

  private static class LazyHolder {
    private static CoreLoggerProvider init() {
      ServiceLoader<CoreLoggerProvider> loader = ServiceLoader.load(CoreLoggerProvider.class);
      Iterator<CoreLoggerProvider> iterator = loader.iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        String msg = "No CoreLoggerProvider implementation found in classpath!";
        throw new ServiceConfigurationError(msg);
      }
    }

    static final CoreLoggerProvider INSTANCE = init();
  }

  private static class Filters {

    private final List<CoreLoggerFilter> filterList;

    private static final String PROPERTIES_FILE = "echopraxia.properties";

    public Filters(@NotNull ClassLoader classLoader) {
      InputStream inputStream = CoreLoggerFactory.class.getResourceAsStream("/" + PROPERTIES_FILE);

      if (inputStream != null) {
        try {
          Properties props = new Properties();
          props.load(inputStream);
          filterList = getFilters(props, classLoader);
        } catch (IOException e) {
          throw new ServiceConfigurationError("", e);
        }
      } else {
        filterList = Collections.emptyList();
      }
    }

    @NotNull
    private List<CoreLoggerFilter> getFilters(
        @NotNull Properties props, @NotNull ClassLoader classLoader) {
      List<String> result = new LinkedList<>();
      String value;
      for (int i = 0; (value = props.getProperty("filter." + i)) != null; i++) {
        result.add(value);
      }
      Stream<String> stream = result.stream();
      return stream
          .map(className -> this.getFilterInstance(classLoader, className))
          .collect(Collectors.toList());
    }

    @NotNull
    private CoreLoggerFilter getFilterInstance(
        @NotNull ClassLoader classLoader, @NotNull String className) {
      try {
        Class<?> aClass = classLoader.loadClass(className);
        if (!CoreLoggerFilter.class.isAssignableFrom(aClass)) {
          String msg = "Class " + className + " does not implement CoreLoggerFilter";
          throw new ServiceConfigurationError(msg);
        }
        //noinspection unchecked
        Class<CoreLoggerFilter> filterClass = (Class<CoreLoggerFilter>) aClass;
        Constructor<CoreLoggerFilter> declaredConstructor = filterClass.getDeclaredConstructor();
        return declaredConstructor.newInstance();
      } catch (ClassNotFoundException
          | NoSuchMethodException
          | InvocationTargetException
          | InstantiationException
          | IllegalAccessException e) {
        throw new ServiceConfigurationError("Cannot create an instance from " + className, e);
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
}
