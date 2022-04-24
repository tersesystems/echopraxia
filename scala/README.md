# Scala API for Echopraxia

The Scala API for Echopraxia is a layer over the Java API that works smoothly with Scala types.

## Quick Start

Add the following to your `build.sbt` file:

```scala
libraryDependencies += "com.tersesystems.echopraxia" %% "scala-api" % "1.5.0-SNAPSHOT"
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

Using this method adds the following fields on every statement.  You can override this method in a custom logger to provide your own implementation.

```scala
trait DefaultLoggerMethods[FB <: FieldBuilder] extends LoggerMethods[FB] {
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

The Scala field builder has additional methods that take `ToValue`, `ToObjectValue`, and `ToArrayValue` type classes.

The field builder can imported with `import fb._` to provide a custom DSL that relies on tuples.  The built in type classes are already provided, and there are also type classes for `Option[V: ToValue]` and `Try[V: ToValue]` types.

```scala
import com.tersesystems.echopraxia.sapi._

class Example {
  val logger = LoggerFactory.getLogger

  def doStuff: Unit = {
    logger.info("{} {} {} {}", fb => {
      import fb._
      list(
        `object`("person" -> 
          Seq(
            value("number" -> 1),
            value("bool" -> true),
            array("ints" -> Seq(1, 2, 3)),
            keyValue("strName" -> "bar")
          )
        )
      )
    })
  }
}
```

## Custom Field Builder

You can create your own field builder and define type class instances.  For example, to map an `java.time.Instant` to a string, you would add

```scala
import java.time._

class CustomFieldBuilder extends FieldBuilder {
  import com.tersesystems.echopraxia.Field.Value

  implicit val instantToStringValue: ToValue[Instant] = ToValue(instantValue)

  def instant(name: String, i: Instant): Field = keyValue(name -> instantValue(i))

  def instant(tuple: (String, Instant)): Field = keyValue(tuple)

  private def instantValue(i: Instant): Value.StringValue = Value.string(i.toString)
}
```

And then you render an instant;

```scala
logger.info("time {}", fb.only(fb.instant("current", Instant.now)))
```

Or you can import the field builder implicit:

```scala
logger.info("time {}", fb => {
  import fb._
  keyValue("current" -> Instant.now)
})
```

You can also convert maps to an object value more generally:

```scala
trait MapFieldBuilder {

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
import com.tersesystems.echopraxia.core.{Caller, CoreLogger, CoreLoggerFactory}
import com.tersesystems.echopraxia.sapi.{Condition, FieldBuilder, ToObjectValue, ToValue}
import com.tersesystems.echopraxia.sapi.support._

import java.time.Instant
import scala.compat.java8.FunctionConverters._
import scala.collection.JavaConverters._

object CustomLoggerFactory {
  private val FQCN: String = classOf[DefaultLoggerMethods[_]].getName
  private val fieldBuilder: CustomFieldBuilder = new CustomFieldBuilder {}

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

  private type SELF = CustomLogger

  @inline
  private def newLogger(coreLogger: CoreLogger): SELF = new CustomLogger(coreLogger, fieldBuilder)

  @inline
  def withCondition(scalaCondition: Condition): SELF = newLogger(core.withCondition(scalaCondition.asJava))

  @inline
  def withFields(f: CustomFieldBuilder => java.util.List[Field]): SELF = {
    newLogger(core.withFields(f.asJava, fieldBuilder))
  }

  @inline
  def withThreadContext: SELF = {
    import com.tersesystems.echopraxia.support.Utilities
    newLogger(core.withThreadContext(Utilities.getThreadContextFunction(fieldBuilder)))
  }
}
```
