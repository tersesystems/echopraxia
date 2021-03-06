## Changelog

## 2.1.1

Break out Jackson serde module and make both Logstash and Log4J2 implementations depend on it.

* Break out jackson module as a distinct dependency.
* Upgrade to Log4J2 2.18.0

## 2.1.0

Changes to `CoreLogger` API to allow for more flexible loggers in [echopraxia-plusscala](https://github.com/tersesystems/echopraxia-plusscala).  Some optimizations.

* Add `extraFields` parameter to `CoreLogger` methods. [#201](https://github.com/tersesystems/echopraxia/pull/201)
* Document "call-by-name" semantics on `logger.withFields`. [#188](https://github.com/tersesystems/echopraxia/pull/188)
* Expose `getArgumentFields()` and `getLoggerFields()` methods on `LoggingContext`. [#197](https://github.com/tersesystems/echopraxia/pull/197)
* Upgrade logstash-logback-encoder to 7.2 [#203](https://github.com/tersesystems/echopraxia/pull/203)
* Add `coreLogger.logHandle` for loggers that may log multiple times internally when called. [#202](https://github.com/tersesystems/echopraxia/pull/202)
* Fix a bug where `withFields` was being memoized and evaluated once. [#187](https://github.com/tersesystems/echopraxia/pull/187)

## 2.0.1

Bug fixes and some enhancements around number values.

* `ctx.findList` returns a list with a single element if an element matches, i.e. `ctx.findList("$.exception")` returns a list containing a single `Throwable`.
* Add object equality methods for `Value` and `Field` instances.
* Add `Comparable` interface for `NumberValue` and specialize types so that `NumberValue<Integer>` and `NumberValue<Byte>` are not comparable.
* Set null numbers to return `0` rather than `null` to better reflect java number behavior.
* Remove generic `Numeric` methods, use specific numbers in methods. 
* Add cache for number values corresponding to the java.lang number caches.

## 2.0.0

### API changes

The API package now begins with the package name `com.tersesystems.echopraxia.api` and does not contain the `Logger` or `LoggerFactory` classes.  All the classes in `core` and `support` have been moved to `api`.

There is a new `FieldBuilderResult` interface that is responsible for getting fields from a field builder. The `Field.BuilderFunction` interface which extended `Function<FB, List<Field>>` has been replaced with plain `Function<FB, FieldBuilderResult>`.  The `Field` interface now extends `FieldBuilderResult`.  The upshot of this is that both `fb.list` and `field` return a `FieldBuilderResult` and there is no more need for `fb.only`.  A `FieldBuilderWithOnly` interface is available for backwards compatibility.  `FieldBuilderResult.list` will take most aggregate forms; `Stream`, `Iterator`, etc.

The `Field.Builder` interface is now `FieldBuilder`.

The `Field.Value` interface and subclasses have been moved to a top level class `Value`, i.e. `Value.string("foo")` rather than `Field.Value.string("foo")`.

There is a `FieldConstants` class that uses a resource bundle to load in hardcoded field constants, such as `exception` and `stackTrace`.  Use `FieldConstants.EXCEPTION` to reference.

The core logger no longer depends on `FB extends FieldBuilder`, so it is now possible to create custom loggers that don't expose `fb.keyValue` or `fb.string`.

There is a new `Utilities.threadContext()` method which is a cleaner way to manage thread context in custom loggers.

Jayway specific predicates removed from `LoggingContext` API.

### Logger Changes

Logger has been broken out into a different maven package.  It is still in the same location `com.tersesystems.echopraxia.Logger`.

The `withExecutor` method that returned `AsyncLogger` has been removed.

The `withFieldBuilder(Foo.class)` method has been removed, please use `withFieldBuilder(new Foo)` instead.

## Async Logger Changes

The async logger has been broken out into a different maven package.   It is still in the same location `com.tersesystems.echopraxia.async.AsyncLogger`.

The `withFieldBuilder(Foo.class)` method has been removed, please use `withFieldBuilder(new Foo)` instead.

### Fluent Logger Changes

The `argument` method now takes `Function<FB, FieldBuilderResult>` and so will work with both lists and single fields.

The `withFieldBuilder(Foo.class)` method has been removed, please use `withFieldBuilder(new Foo)` instead.

### Semantic Logger Changes

The `withFieldBuilder(Foo.class)` method has been removed, please use `withFieldBuilder(new Foo)` instead.

### Logstash Changes

No major changes.  Minor optimizations internally to reduce allocation, enable more inlining, and pre-size lists.

### Log4J Changes

The `Log4JCoreLogger` logging checks were using `logger.isEnabled(log4jLevel, marker, message, e)` which required resolution of the field builder function even on logging levels that were disabled, just in case some filters might evaluate on message or exception.  This is an unjustifiable overhead, and now Log4J checks the same way as Logstash with `logger.isEnabled(log4jLevel, marker)` using only the level and marker as parameters -- only if that check is passed are arguments evaluated.

### Scripting Changes

No major changes.  Tweakflow script evaluation has been optimized and run through benchmarking to minimize object allocation and maximize JVM inlining.



