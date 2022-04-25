package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.sapi.Condition
import com.tersesystems.echopraxia.Field

trait LoggerSupport[FB] {

  type SELF[T] = LoggerSupport[T]

  def withCondition(scalaCondition: Condition): SELF[FB]

  def withFields(f: FB => java.util.List[Field]): SELF[FB]

  def withThreadContext: SELF[FB]

  def withFieldBuilder[T <: FB](newBuilderClass: Class[T]): SELF[T]

  def withFieldBuilder[T <: FB](newBuilder: T): SELF[T]
}
