package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.CoreLogger

abstract class AbstractLoggerSupport[FB](val core: CoreLogger, val fieldBuilder: FB)
    extends DefaultMethodsSupport[FB] {
  def name: String = core.getName
}
