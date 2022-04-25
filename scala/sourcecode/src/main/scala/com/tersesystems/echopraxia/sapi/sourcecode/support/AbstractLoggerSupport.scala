package com.tersesystems.echopraxia.sapi.sourcecode.support

import com.tersesystems.echopraxia.core.CoreLogger

/**
 * Abstract class used for easily providing DefaultMethodsSupport.
 */
abstract class AbstractLoggerSupport[FB](val core: CoreLogger, val fieldBuilder: FB)
    extends DefaultMethodsSupport[FB] {
  def name: String = core.getName
}
