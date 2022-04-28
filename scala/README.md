# Scala API for Echopraxia

The Scala API for Echopraxia is a layer over the Java API that works smoothly with Scala types.

## Quick Start

Add the following to your `build.sbt` file:

```scala
libraryDependencies += "com.tersesystems.echopraxia" %% "scala-logger" % "2.0.0-SNAPSHOT"
```

To import the Scala API, add the following:

```scala
import com.tersesystems.echopraxia.sapi._

class Example {
  val logger = LoggerFactory.getLogger
  
  def doStuff: Unit = {
    logger.info("do some stuff")
  }
}
```

## Source Code

The Scala API can integrate source code metadata into logging statements.

```scala
libraryDependencies += "com.tersesystems.echopraxia" %% "scala-sourcecode" % "1.5.0-SNAPSHOT"
```
 
The API is the same, but you must import `sapi.sourcecode._`:

```scala
import com.tersesystems.echopraxia.sapi.sourcecode._

class Example {
  val logger = LoggerFactory.getLogger
  
  def doStuff: Unit = {
    logger.info("do some stuff")
  }
}
```

Using this method adds the following fields on every statement:

```scala
trait DefaultLoggerMethods[FB] extends LoggerMethods[FB] {
  this: DefaultMethodsSupport[FB] =>

  protected def sourceInfoFields(fb: FB)(implicit
      line: sourcecode.Line,
      file: sourcecode.File,
      enc: sourcecode.Enclosing
  ): util.List[Field] = {
    fb.onlyObject(
      "sourcecode",
      fb.string("file", file.value),
      fb.number("line", line.value),
      fb.string("enclosing", enc.value)
    )
  }
  
  // ...
}
```

## Field Builder

A field is defined as a `name` and a `value`, where the value can one of the types defined in `Field.Value`.  Defining a value like `StringValue` or `BooleanValue` can be tedious, and so the Scala field builder has methods that take `ToValue`, `ToObjectValue`, and `ToArrayValue` type classes.

The field builder can be imported with `import fb._` to provide a custom DSL that relies on tuples.  The built-in type classes are already provided for strings, numbers, and booleans, and there are also type classes for `Option[V: ToValue]` and `Try[V: ToValue]` types.

There are two basic methods, `fb.value` and `fb.keyValue`, which render fields defined in a parameterized message template with "hello {}" as `hello value` and `hello key=value` in a line oriented layout, respectively.  The more specific methods, `fb.string`, `fb.number`, `fb.bool`, `fb.array`, and `fb.obj` have more specific value requirements.  Only `fb.array` and `fb.obj` use `key=value` format, and the other methods use the `value` format.

```scala
import com.tersesystems.echopraxia.sapi._

class Example {
  val logger = LoggerFactory.getLogger

  def doStuff: Unit = {
    logger.info("{} {} {} {}", fb => fb.list {
      import fb._
      obj("person" -> 
        Seq(
          value("number" -> 1),
          value("bool" -> true),
          array("ints" -> Seq(1, 2, 3)),
          keyValue("strName" -> "bar")
        )
      )
    })
  }
}
```

Arrays will take a `Seq` of values, including object values.  Object values take a sequence of fields as arguments, and are best defined using the `Field.Value.object`. For example, the first element in the [path example from Json-Path](https://github.com/json-path/JsonPath#path-examples) can be represented as:

```scala
logger.info("{}", fb => {
  fb.onlyObj("store" ->
    fb.array("book" -> Seq(
      Field.Value.`object`(
        fb.string("category", "reference"),
        fb.string("author", "Nigel Rees"),
        fb.string("title", "Sayings of the Century"),
        fb.number("price", 8.95)
      )
    ))
  )
})
```

## Custom Field Builder

You can create your own field builder and define type class instances.  For example, to map an `java.time.Instant` to a string, you would add the following:

```scala
import java.time._

final class CustomFieldBuilder extends FieldBuilder {
  import java.time.Instant

  implicit val instantToStringValue: ToValue[Instant] = ToValue(i => Field.Value.string(i.toString))

  def instant(name: String, instant: Instant): Field = keyValue(name, instant)
  def instant(tuple: (String, Instant)): Field = instant(tuple._1, tuple._2)
}
```

And then you render an instant;

```scala
logger.info("time {}", fb.only(fb.instant("current", Instant.now)))
```

Or you can import the field builder implicit:

```scala
logger.info("time {}", fb => fb.only {
  import fb._
  keyValue("current" -> Instant.now)
})
```

You can also convert maps to an object value more generally:

```scala
class MapFieldBuilder extends FieldBuilder {

  implicit def mapToObjectValue[V: ToValue]: ToObjectValue[Map[String, V]] = new ToObjectValue[Map[String, V]] {
    override def toObjectValue(t: Map[String, V]): Value.ObjectValue = {
      val fields: Seq[Field] = t.map {
        case (k, v) =>
          keyValue(k, v.toString)
      }.toSeq
      Field.Value.`object`(fields.asJava)
    }
  }
}
```

## Custom Logger

You can create a custom logger which has your own methods and field builders by leveraging the `sapi.support` package.

```scala
import com.tersesystems.echopraxia.Field
import com.tersesystems.echopraxia.core._
import com.tersesystems.echopraxia.sapi._
import com.tersesystems.echopraxia.sapi.support._

object CustomLoggerFactory {
  private val FQCN: String = classOf[DefaultLoggerMethods[_]].getName
  private val fieldBuilder: CustomFieldBuilder = new CustomFieldBuilder

  def getLogger(name: String): CustomLogger = {
    val core = CoreLoggerFactory.getLogger(FQCN, name)
    new CustomLogger(core, fieldBuilder)
  }

  def getLogger(clazz: Class[_]): CustomLogger = {
    val core = CoreLoggerFactory.getLogger(FQCN, clazz.getName)
    new CustomLogger(core, fieldBuilder)
  }

  def getLogger: CustomLogger = {
    val core = CoreLoggerFactory.getLogger(FQCN, Caller.resolveClassName)
    new CustomLogger(core, fieldBuilder)
  }
}

final class CustomLogger(core: CoreLogger, fieldBuilder: CustomFieldBuilder)
  extends AbstractLoggerSupport(core, fieldBuilder) with DefaultLoggerMethods[CustomFieldBuilder] {

  @inline
  private def newLogger(coreLogger: CoreLogger): CustomLogger = new CustomLogger(coreLogger, fieldBuilder)

  @inline
  def withCondition(scalaCondition: Condition): CustomLogger = newLogger(core.withCondition(scalaCondition.asJava))

  @inline
  def withFields(f: CustomFieldBuilder => java.util.List[Field]): CustomLogger = {
    import scala.compat.java8.FunctionConverters._
    newLogger(core.withFields(f.asJava, fieldBuilder))
  }

  @inline
  def withThreadContext: CustomLogger = {
    import com.tersesystems.echopraxia.support.Utilities
    newLogger(core.withThreadContext(Utilities.threadContext()))
  }
}
```
