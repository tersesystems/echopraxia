
## Custom Logger Factories

If you are using a particular set of field builders for your domain and want them available by default, it's easy to create your own logger with your own field builder, using the support classes and interfaces.  

Creating your own logger will also remove the type parameter from your code, so you don't have to type `Logger<?>` everywhere, and allow you to create custom methods that leverage field builders.

If you want to make sure your logger is the only one available, you should import only the API:

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>api</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:api:<VERSION>" 
```

And then continuing on from the [custom field builder example](https://github.com/tersesystems/echopraxia-examples/blob/main/custom-field-builder/README.md), you can build a `PersonLogger`:

```java
import com.tersesystems.echopraxia.api.*;

public final class PersonLogger extends AbstractLoggerSupport<PersonLogger, PersonFieldBuilder>
  implements DefaultLoggerMethods<PersonFieldBuilder> {
  private static final String FQCN = PersonLogger.class.getName();

  protected PersonLogger(
    @NotNull CoreLogger core, @NotNull PersonFieldBuilder fieldBuilder, Class<?> selfType) {
    super(core, fieldBuilder, selfType);
  }

  public void info(@Nullable String message, Person person) {
    // when using custom methods, you must specify the caller as the class it's defined in.
    this.core().withFQCN(FQCN).log(Level.INFO, message,
      fb -> fb.person("person", person), fieldBuilder);
  }

  @Override
  protected @NotNull PersonLogger newLogger(CoreLogger core) {
    return new PersonLogger(core, fieldBuilder(), PersonLogger.class);
  }

  @Override
  protected @NotNull PersonLogger neverLogger() {
    return new PersonLogger(
      core.withCondition(Condition.never()), fieldBuilder(), PersonLogger.class);
  }
}
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
