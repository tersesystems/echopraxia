package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.{Level => JLevel}

object Level {
  case object TRACE extends Level(JLevel.TRACE)
  case object DEBUG extends Level(JLevel.DEBUG)
  case object INFO  extends Level(JLevel.INFO)
  case object WARN  extends Level(JLevel.WARN)
  case object ERROR extends Level(JLevel.ERROR)

  def asScala(level: JLevel): Level = level match {
    case JLevel.TRACE => TRACE
    case JLevel.DEBUG => DEBUG
    case JLevel.WARN  => WARN
    case JLevel.INFO  => INFO
    case JLevel.ERROR => ERROR
  }
}

sealed class Level(private val level: JLevel) {
  def isGreater(r: Level): Boolean = level.compareTo(r.level) > 0

  def isGreater(r: JLevel): Boolean = level.compareTo(r) > 0

  def isGreaterOrEqual(r: Level): Boolean = level.compareTo(r.level) >= 0

  def isGreaterOrEqual(r: JLevel): Boolean = level.compareTo(r) >= 0

  def isLess(r: Level): Boolean = level.compareTo(r.level) < 0

  def isLess(r: JLevel): Boolean = level.compareTo(r) < 0

  def isLessOrEqual(r: Level): Boolean = level.compareTo(r.level) <= 0

  def isLessOrEqual(r: JLevel): Boolean = level.compareTo(r) <= 0

  def isEqual(r: Level): Boolean = this.level == r.level

  def isEqual(r: JLevel): Boolean = this.level == r

  def asJava: JLevel = level
}
