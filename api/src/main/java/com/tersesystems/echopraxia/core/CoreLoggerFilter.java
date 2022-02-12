package com.tersesystems.echopraxia.core;

import java.util.function.Supplier;

public interface CoreLoggerFilter {
  Supplier<CoreLogger> apply(Supplier<CoreLogger> supplier);
}
