package com.tersesystems.echopraxia.sapi.sourcecode.support

import com.tersesystems.echopraxia.core.CoreLogger
import com.tersesystems.echopraxia.sapi.FieldBuilder

/**
 * Abstract class used for easily providing DefaultMethodsSupport.
 */
abstract class AbstractLoggerSupport[FB <: FieldBuilder](val core: CoreLogger, val fieldBuilder: FB)
    extends DefaultMethodsSupport[FB] {
  def name: String = core.getName
}
