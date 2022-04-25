package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.core.CoreLogger

abstract class AbstractLoggerSupport[FB](val core: CoreLogger, val fieldBuilder: FB)
    extends DefaultMethodsSupport[FB] {
  def name: String = core.getName
}
