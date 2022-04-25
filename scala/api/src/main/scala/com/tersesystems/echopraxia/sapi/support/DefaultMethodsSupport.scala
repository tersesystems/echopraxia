package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.core.CoreLogger

trait DefaultMethodsSupport[FB] {
  def name: String

  def core: CoreLogger

  def fieldBuilder: FB
}
