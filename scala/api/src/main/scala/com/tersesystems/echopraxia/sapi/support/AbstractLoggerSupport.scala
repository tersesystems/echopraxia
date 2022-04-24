package com.tersesystems.echopraxia.sapi.support

import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.FieldBuilder

abstract class AbstractLoggerSupport[FB <: FieldBuilder](val core: CoreLogger, val fieldBuilder: FB)
    extends DefaultMethodsSupport[FB] {
  def name: String = core.getName
}
