package com.tersesystems.echopraxia.core;

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
  public static CoreLogger getLogger(String fqcn, @NotNull Class<?> clazz) {
    CoreLogger core = LazyHolder.INSTANCE.getLogger(fqcn, clazz);
    return processFilters(core);
  }

  @NotNull
  public static CoreLogger getLogger(String fqcn, @NotNull String name) {
    CoreLogger core = LazyHolder.INSTANCE.getLogger(fqcn, name);
    return processFilters(core);
  }

  private static CoreLogger processFilters(CoreLogger core) {
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
    private final Properties props;
    private final List<CoreLoggerFilter> filterList;

    public Filters(ClassLoader classLoader) {
      InputStream inputStream =
          CoreLoggerFactory.class.getResourceAsStream("/echopraxia.properties");
      props = new Properties();
      try {
        props.load(inputStream);
        filterList = getFilters(classLoader);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private List<String> getFilterClass() {
      List<String> result = new LinkedList<>();
      String value;
      for (int i = 0; (value = props.getProperty("filter." + i)) != null; i++) {
        result.add(value);
      }
      return result;
    }

    private List<CoreLoggerFilter> getFilters(ClassLoader classLoader) {
      Stream<String> stream = getFilterClass().stream();
      return stream
          .map(className -> this.getFilterInstance(classLoader, className))
          .collect(Collectors.toList());
    }

    private CoreLoggerFilter getFilterInstance(ClassLoader classLoader, String className) {
      try {
        Class<?> aClass = classLoader.loadClass(className);
        if (!CoreLoggerFilter.class.isAssignableFrom(aClass)) {
          String msg = "Class " + className + " does not implement CoreLoggerFilter";
          throw new RuntimeException(msg);
        }
        Class<CoreLoggerFilter> filterClass = (Class<CoreLoggerFilter>) aClass;
        Constructor<CoreLoggerFilter> declaredConstructor = filterClass.getDeclaredConstructor();
        CoreLoggerFilter filter = declaredConstructor.newInstance();
        return filter;
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    public CoreLogger apply(CoreLogger core) {
      CoreLoggerFilter filter = filterList.get(0);
      return filter.apply(() -> core).get();
    }
  }
}
