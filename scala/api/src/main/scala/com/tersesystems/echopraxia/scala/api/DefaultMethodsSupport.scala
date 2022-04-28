package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.CoreLogger

trait DefaultMethodsSupport[FB] {
  def name: String

  def core: CoreLogger

  def fieldBuilder: FB
}
