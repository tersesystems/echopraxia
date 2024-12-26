
# Custom Logger

If you are using a particular set of field builders for your domain and want them available by default, it's easy to create your own logger with your own field builder, using the support classes and interfaces.  

Creating your own logger will also remove the type parameter from your code, so you don't have to type `Logger<PersonFieldBuilder>` everywhere, and allow you to create custom methods that leverage field builders.

If you want to make sure your logger is the only one available, you should import only the API:

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>simple</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:simple:<VERSION>" 
```

And then continuing on from the [custom field builder example](https://github.com/tersesystems/echopraxia-examples/blob/main/custom-field-builder/README.md), you can build a `PersonLogger`:

```

```

and a custom logger factory:

```java
public final class PersonLoggerFactory {

  private static final PersonFieldBuilder myFieldBuilder = PersonFieldBuilder.instance;

  // the class containing the error/warn/info/debug/trace methods
  private static final String FQCN = DefaultLoggerMethods.class.getName();

  public static PersonLogger getLogger(Class<?> clazz) {
    return getLogger(CoreLoggerFactory.getLogger(FQCN, clazz.getName()));
  }

  public static PersonLogger getLogger(String name) {
    return getLogger(CoreLoggerFactory.getLogger(FQCN, name));
  }

  public static PersonLogger getLogger() {
    return getLogger(CoreLoggerFactory.getLogger(FQCN, Caller.resolveClassName()));
  }

  public static PersonLogger getLogger(@NotNull CoreLogger core) {
    return new PersonLogger(core, myFieldBuilder, PersonLogger.class);
  }
}
```

and then you can log a person as a raw parameter:

```java
PersonLogger logger = PersonLoggerFactory.getLogger();
Person abe = ...
logger.info("Best person: {}", abe);
```

Generally loggers should be final, and any common functionality should be moved out to interfaces you can share.  This is because subclassing can have an impact on JVM optimizations, and can make returning specific types from `with*` methods more complicated. 
