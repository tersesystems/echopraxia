package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.FieldBuilder

trait DefaultMethodsSupport[FB <: FieldBuilder] {
  def name: String

  def core: CoreLogger

  def fieldBuilder: FB
}
