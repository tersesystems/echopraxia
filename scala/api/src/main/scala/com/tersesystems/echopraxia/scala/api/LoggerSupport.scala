package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.FieldBuilderResult

trait LoggerSupport[FB] {

  type SELF[T] = LoggerSupport[T]

  def withCondition(scalaCondition: Condition): SELF[FB]

  def withFields(f: FB => FieldBuilderResult): SELF[FB]

  def withThreadContext: SELF[FB]

  def withFieldBuilder[T <: FB](newBuilder: T): SELF[T]
}
