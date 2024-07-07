# Changelog

## 3.2.1

* Fix a bug where `toStringValue` was not correctly applied to `Value.object` or `Value.array`.

## 3.2.0

* Add value attributes, `value.attributes()` etc.
* Make `abbreviateAfter`, `asCardinal` methods on `StringValue` and `ArrayValue`, deprecate the field methods.
* Add `withToStringValue` to `Value` so that `toStringFormat` doesn't have to be used (FieldVisitor is too complex).
* Add `(Field...)` method signatures to Logger so that you can log arguments without using field builder function.
* Add "no message" method signatures, so you don't need an explicit string template.

## 3.1.2

* Fix bug in ToStringFormat attribute where child fields were not processed correctly.

## 3.1.1

* Add a no-op EchopraxiaService provider, for use in tests.

## 3.1.0

* Add `withToStringFormat` to PresentationField.

## 3.0.2

* Make the `slf4j-api` dependency in `filewatch` and `jul` modules be "compileOnly"

## 3.0.1

* Move `LoggingContext` from `spi` back to the api package.

## 3.0.0

* Make Logback and logstash-logback-encoder dependencies be compile only for Logback 1.3/1.4 and LLE 7.4
* Remove lower bound for `*Logger<F extends FieldBuilder>`, now just `Logger<F>` so you can use your own builder.
* Move internal classes in `api` into `spi` package
* Move from very large README.md to documentation website
* Add presentation hints `valueOnly`, `abbreviateAfter`, `displayName`, `elide`, `cardinal`
* Add `PresentationHintsAware` and `PresentationField` so we can do `field.abbreviateAfter(5)`
* Add `FieldCreator` and service implementations so Field can be extended with extra methods
* Add `ToStringFormatter` and wire `field.toString` and `value.toString` to it
* Add `FieldVisitor` for changing field structure in JSON
* Add exception handler tied to `EchopraxiaService`
* Add `EchopraxiaServiceProvider` and `EchopraxiaService` for centralized management
* Change `equals` on fields so that fields are not equal if they have different attributes
* Make all fields (`fb.string`, `fb.number`, `fb.nullValue`) use `keyValue` by default.
* Add field attributes.

## 2.3.1

* Move logback converters to logback module by @wsargent in https://github.com/tersesystems/echopraxia/pull/242
* Add fallback logic for logstash by @wsargent in https://github.com/tersesystems/echopraxia/pull/241
* Add JUL options for context by @wsargent in https://github.com/tersesystems/echopraxia/pull/250
* Remove FieldBuilder as a dependency of DiffFieldBuilder by @wsargent in https://github.com/tersesystems/echopraxia/pull/251
* Open logstash context by @wsargent in https://github.com/tersesystems/echopraxia/pull/252

## 2.3.0

* Add `getCore()` method to `LoggingContext` in https://github.com/tersesystems/echopraxia/pull/229
* Add user defined functions to scripting to expose impure methods and context to Tweakflow in https://github.com/tersesystems/echopraxia/pull/227
* Path based logback custom converters in https://github.com/tersesystems/echopraxia/pull/223
* Add delegate core logger in https://github.com/tersesystems/echopraxia/pull/226
* Remove AsyncLogger from README (too confusing, not needed for most people)
* Move `LogbackLoggingContext` to Logback module

## 2.2.4

* Delay logback initialization.

## 2.2.3

* Allow filters to be instantiated from a context classloader.
* More Logback direct API support with `ConditionTurboFilter`

## 2.2.2

* Break out the underlying Logback classes from Logstash, so that "direct access" can be used as a fallback.
* Fix a bug in JSON exception rendering using Logstash `StructuredArgument` where exception was being rendered a full nested JSON object.

## 2.2.1

* Added Condition.booleanMatch/objectMatch/*match for better direct matching.
* Added a type safe Values.equals static method.
* Also opened up the Logstash / Log4J constructors, so that it's easier to wrap Logback Logger / ExtendedLogger directly into a core logger.

## 2.2.0

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



