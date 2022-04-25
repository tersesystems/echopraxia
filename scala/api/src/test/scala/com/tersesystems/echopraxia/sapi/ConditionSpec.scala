package com.tersesystems.echopraxia.sapi

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.tersesystems.echopraxia.Field
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

import java.util

class ConditionSpec extends AnyFunSpec with BeforeAndAfterEach with Matchers {

  private val logger = LoggerFactory.getLogger(getClass)

  describe("findBoolean") {

    it("should match") {
      val condition: Condition = (_, ctx) => ctx.findBoolean("$.foo").getOrElse(false)
      logger.debug(condition, "found a foo == true", _.onlyBool("foo", true))

      matchThis("found a foo == true")
    }

    it("should return None if no match") {
      val condition: Condition = (_, ctx) => ctx.findBoolean("$.foo").getOrElse(false)
      logger.debug(condition, "found a foo == true")

      noMatch
    }

    it("should return None if match is not boolean") {
      val condition: Condition = (_, ctx) => ctx.findBoolean("$.foo").getOrElse(false)
      logger.debug(condition, "found a foo == true", _.onlyString("foo", "bar"))

      noMatch
    }
  } // findBoolean

  describe("findString") {

    it("should return some on match") {
      val condition: Condition = (_, ctx) => ctx.findString("$.foo").contains("bar")
      logger.debug(condition, "found a foo == bar", _.onlyString("foo", "bar"))

      matchThis("found a foo == bar")
    }

    it("should match on a complex object") {
      val condition: Condition = (_, ctx) => {
        val bar0 = ctx.findString("$.store.book[0].title")
        bar0.get == "Sayings of the Century"
      }
      logger.info(
        condition,
        "{}",
        fb => {
          fb.onlyObj(
            "store" ->
              fb.array(
                "book" -> Seq(
                  Field.Value.`object`(
                    fb.string("category", "reference"),
                    fb.string("author", "Nigel Rees"),
                    fb.string("title", "Sayings of the Century"),
                    fb.number("price", 8.95)
                  )
                )
              )
          )
        }
      )
    }

    it("should none on no match") {
      val condition: Condition = (_, ctx) => ctx.findString("$.foo").contains("bar")
      logger.debug(condition, "found a foo == bar")

      noMatch
    }

    it("should none on wrong type") {
      val condition: Condition = (_, ctx) => ctx.findString("$.foo").contains("bar")
      logger.debug(condition, "found a foo == bar", _.onlyNumber("foo", 1))

      noMatch
    }
  } // findString

  describe("findNumber") {
    it("should some on match") {
      val condition: Condition = (_, ctx) => ctx.findNumber("$.foo").exists(_.intValue() == 1)
      logger.debug(condition, "found a number == 1", _.onlyNumber("foo", 1))

      matchThis("found a number == 1")
    }
  } // findNumber

  describe("findNull") {
    it("should work with findNull") {
      val condition: Condition = (_, ctx) => ctx.findNull("$.foo")
      logger.debug(condition, "found a null!", _.onlyNullField("foo"))

      matchThis("found a null!")
    }
  }

  describe("findList") {
    it("should work with list of same type") {
      val condition: Condition = (_, ctx) => {
        val list   = ctx.findList("$.foo")
        val result = list.contains("derp")
        result
      }
      logger.debug(
        condition,
        "found a list with derp in it!",
        fb => {
          fb.onlyArray("foo", Array("derp"))
        }
      )

      matchThis("found a list with derp in it!")
    }

    it("should work with list with different type values") {
      val condition: Condition = (_, ctx) => {
        val nummatch = ctx.findList("$.foo").contains(1)
        val strmatch = ctx.findList("$.foo").contains("derp")
        nummatch && strmatch
      }
      logger.debug(
        condition,
        "found a list with 1 in it!",
        fb => {
          import Field.Value._
          fb.onlyArray("foo", array(string("derp"), number(1), bool(false)))
        }
      )

      matchThis("found a list with 1 in it!")
    }

    it("should match on list containing objects") {
      val condition: Condition = (_, ctx) => {
        val obj = ctx.findList("$.array")
        obj.head match {
          case map: Map[String, Any] =>
            map.get("a").contains(1) && map.get("c").contains(false)
          case _ =>
            false
        }
      }
      logger.debug(
        condition,
        "complex object",
        fb => {
          import Field.Value._
          val objectValue: ObjectValue = `object`(
            fb.value("a"    -> 1),
            fb.keyValue("b" -> "two"),
            fb.value("c"    -> false)
          )
          fb.onlyArray("array", Seq(objectValue))
        }
      )
    }
  }

  describe("object") {
    it("should match on simple object") {
      logger
        .withCondition((_, ctx) => ctx.findObject("$.foo").get("key").equals("value"))
        .debug("simple map", fb => fb.onlyObj("foo", fb.keyValue("key" -> "value")))

      matchThis("simple map")
    }

    it("should not match on no argument") {
      val condition: Condition = (_, ctx) => {
        ctx.findObject("$.foo").isDefined
      }
      logger.debug(condition, "no match", _.onlyNumber("bar", 1))

      noMatch
    }

    it("should not match on incorrect type") {
      val condition: Condition = (_, ctx) => {
        ctx.findObject("$.foo").isDefined
      }
      logger.debug(condition, "no match", _.onlyNumber("foo", 1))

      noMatch
    }

    it("should match on a complex object") {
      val condition: Condition = (_, ctx) => {
        val obj         = ctx.findObject("$.foo")
        val value1: Any = obj.get("a")
        value1 == (1)
      }
      logger.debug(
        condition,
        "complex object",
        fb =>
          fb.onlyObj(
            "foo" -> Seq(
              fb.value("a"    -> 1),
              fb.keyValue("b" -> "two"),
              fb.value("c"    -> false)
            )
          )
      )

      matchThis("complex object")
    }
  } // object

  it("should match on list") {
    val condition: Condition = (_, ctx) => {
      val opt: Seq[Any] = ctx.findList("$.foo")
      opt.nonEmpty
    }
    logger.debug(condition, "match list", fb => fb.onlyArray("foo" -> Seq(1, 2, 3)))

    matchThis("match list")
  }
  it("should not match on an object mismatch") {
    val condition: Condition = (_, ctx) => {
      val opt: Option[Map[String, Any]] = ctx.findObject("$.foo")
      opt.isDefined
    }
    logger.debug(condition, "no match", fb => fb.onlyKeyValue("foo" -> true))

    noMatch
  }

  it("should match on level") {
    val condition: Condition = (level, _) => level.isGreater(Level.DEBUG)

    logger.info(condition, "matches on level")
    matchThis("matches on level")
  }

  describe("throwable") {

    it("should match a subclass of throwable") {
      val t = new Exception()
      logger.info("matches on throwable {}", fb => fb.onlyKeyValue("derp" -> t))
      matchThis("matches on throwable {}")
    }
  }

  private def noMatch = {
    val listAppender: ListAppender[ILoggingEvent] = getListAppender
    val list: util.List[ILoggingEvent]            = listAppender.list
    list must be(empty)
  }

  private def matchThis(message: String) = {
    val listAppender: ListAppender[ILoggingEvent] = getListAppender
    val list: util.List[ILoggingEvent]            = listAppender.list
    val event: ILoggingEvent                      = list.get(0)
    event.getFormattedMessage must be(message)
  }

  override def beforeEach(): Unit = {
    getListAppender.list.clear()
  }

  private def loggerContext: LoggerContext = {
    org.slf4j.LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  }

  private def getListAppender: ListAppender[ILoggingEvent] = {
    loggerContext
      .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
      .getAppender("LIST")
      .asInstanceOf[ListAppender[ILoggingEvent]]
  }
}
