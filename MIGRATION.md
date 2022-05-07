## Migration Notes

## 2.0.0

### API changes

The API package now begins with the package name `com.tersesystems.echopraxia.api` and does not contain the `Logger` or `LoggerFactory` classes.  All the classes in `core` and `support` have been moved to `api`.

There is a new `FieldBuilderResult` interface that is responsible for getting fields from a field builder. The `Field.BuilderFunction` interface which extended `Function<FB, List<Field>>` has been replaced with plain `Function<FB, FieldBuilderResult>`.  The `Field` interface now extends `FieldBuilderResult`.  The upshot of this is that both `fb.list` and `field` return a `FieldBuilderResult` and there is no more need for `fb.only`.  A `FieldBuilderWithOnly` interface is available for backwards compatibility.

The `Field.Value` classes and subclasses have been moved to a top level class `Value`, i.e. `Value.string("foo")` rather than `Field.Value.string("foo")`.

There is a `FieldConstants` class that uses a resource bundle to load in hardcoded field constants, such as `exception` and `stackTrace`.

The core logger no longer depends on `FB extends FieldBuilder`, so it is now possible to create custom loggers that don't expose `fb.keyValue` or `fb.string`.

There is a new `Utilities.threadContext()` method which is a cleaner way to manage thread context in loggers.

### Logger Changes

Logger has been broken out into a different maven package.  It is still in the same location `com.tersesystems.echopraxia.Logger`.

The `withExecutor` method that returned `AsyncLogger` has been removed.

## Async Logger Changes

The async logger has been broken out into a different maven package.   It is still in the same location `com.tersesystems.echopraxia.async.AsyncLogger`.

### Fluent Logger Changes

No major changes. The `argument` method now takes `Function<FB, FieldBuilderResult>` and so will work with both lists and single fields.

### Logstash Changes

No major changes.  Minor optimizations internally to reduce allocation, enable more inlining, and pre-size lists.

### Log4J Changes

The `Log4JCoreLogger` logging checks were using `logger.isEnabled(log4jLevel, marker, message, e)` which required resolution of the field builder function even on logging levels that were disabled, just in case some filters might evaluate on message or exception.  This is an unjustifiable overhead, and now Log4J checks the same way as Logstash with `logger.isEnabled(log4jLevel, marker)` using only the level and marker as parameters -- only if that check is passed are arguments evaluated.

### Scripting Changes

No major changes.  Tweakflow script evaluation has been optimized and run through benchmarking to minimize object allocation and maximize JVM inlining.



