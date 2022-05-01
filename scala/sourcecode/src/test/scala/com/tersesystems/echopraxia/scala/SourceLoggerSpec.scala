package com.tersesystems.echopraxia.scala

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.tersesystems.echopraxia.scala.api.{Condition, Level, LoggingContext}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

import java.util

class SourceLoggerSpec extends AnyFunSpec with BeforeAndAfterEach with Matchers {

  private val logger = LoggerFactory.getLogger

  describe("source code") {
    it("should return source code info") {
      val condition: Condition = (level: Level, context: LoggingContext) => {
        context.findString("$.sourcecode.file") match {
          case Some(file) if file.endsWith("LoggerSpec.scala") =>
            true
          case _ =>
            false
        }
      }
      logger.info(condition, "logs if has sourcecode.file")
      matchThis("logs if has sourcecode.file")
    }
  }

  private def matchThis(message: String) = {
    val listAppender: ListAppender[ILoggingEvent] = getListAppender
    val list: util.List[ILoggingEvent]            = listAppender.list
    val event: ILoggingEvent                      = list.get(0)
    event.getMessage must be(message)
  }

  private def loggerContext: LoggerContext = {
    org.slf4j.LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  }

  override def beforeEach(): Unit = {
    getListAppender.list.clear()
  }

  private def getListAppender: ListAppender[ILoggingEvent] = {
    loggerContext
      .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
      .getAppender("LIST")
      .asInstanceOf[ListAppender[ILoggingEvent]]
  }
}
