package com.tersesystems.echopraxia.sapi

import com.tersesystems.echopraxia.{
  Condition => JCondition,
  Level => JLevel,
  LoggingContext => JLoggingContext
}

trait Condition { self =>

  def test(level: Level, context: LoggingContext): Boolean

  def asJava: JCondition = { (level: JLevel, javaContext: JLoggingContext) =>
    {
      self.test(Level.asScala(level), LoggingContext(javaContext))
    }
  }
}