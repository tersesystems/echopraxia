# Context

You can also add fields directly to the logger using `logger.withFields` for contextual logging:

```java
var loggerWithFoo = basicLogger.withFields(fb.string("foo", "bar"));

// will log "foo": "bar" field in a JSON appender.
loggerWithFoo.info("JSON field will log automatically") 
```

This works very well for HTTP session and request data such as correlation ids.

One thing to be aware of that the popular idiom of using `public static final Logger logger` can be limiting in cases where you want to include context data.  For example, if you have a number of objects with their own internal state, it may be more appropriate to create a logger field on the object.

```java
public class PlayerData {

  // the date is scoped to an instance of this player
  private Date lastAccessedDate = new Date();

  // logger is not static because lastAccessedDate is an instance variable
  private final Logger logger =
      LoggerFactory.getLogger()
          .withFields(fb.date("last_accessed_date", lastAccessedDate));

}
```

Because values may be tied to state or variables that can change between method calls, the function call made by `withFields` is [call by name](https://en.wikipedia.org/wiki/Evaluation_strategy#Call_by_name) i.e. the function is called on every logging statement for evaluation against any conditions and the logging statement will contain whatever the current value of `lastAccessedDate` at evaluation.

It's important to note that context evaluation only happens after enabled checks.  For example, `isInfoEnabled` will not trigger context evaluation by itself.  However, any implementation specific markers attached to the context will be passed in for the enabled check, as both Logback and Log4J incorporate marker checks in `isEnabled`.

```java
// will not evaluate context fields
// will evaluate any impl-specific markers (Logback or Log4J)
boolean enabled = logger.isInfoEnabled();
```

The way that context works in conjunction with conditions is more involved, and is covered in the conditions section.

## Thread Context

You can also resolve any fields in Mapped Diagnostic Context (MDC) into fields, using `logger.withThreadContext()`.  This method provides a pre-built function that calls `fb.string` for each entry in the map.

Because MDC is thread local, if you pass the logger between threads or use asynchronous processing i.e. `CompletionStage/CompletableFuture`, you may have inconsistent results.

```java
org.slf4j.MDC.put("mdckey", "mdcvalue");
myLogger.withThreadContext().info("This statement has MDC values in context");
```

This method is call by name, and so will provide the MDC state as fields at the time the logging statement is evaluated.

## Thread Safety

Thread safety is something to be aware of when using context fields.  While fields are thread-safe and using a context is far more convenient than using MDC, you do still have to be aware when you are accessing non-thread safe state.

For example, `SimpleDateFormat` is infamously not thread-safe, and so the following code is not safe to use in a multi-threaded context:

```java
private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");

// UNSAFE EXAMPLE
private static final Logger logger =
        LoggerFactory.getLogger()
        .withFields(fb.string("unsafe_date", df.format(new Date())));
```