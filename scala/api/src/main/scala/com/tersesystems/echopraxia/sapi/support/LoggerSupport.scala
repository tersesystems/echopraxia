package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.sapi.{Condition, FieldBuilder}
import com.tersesystems.echopraxia.Field

trait LoggerSupport[FB <: FieldBuilder] {
  type SELF[F <: FieldBuilder]

  def withCondition(scalaCondition: Condition): SELF[FB]

  def withFields(f: FB => java.util.List[Field]): SELF[FB]

  def withThreadContext: SELF[FB]

  def withFieldBuilder[T <: FieldBuilder](newBuilderClass: Class[T]): SELF[T]

  def withFieldBuilder[T <: FieldBuilder](newBuilder: T): SELF[T]
}
