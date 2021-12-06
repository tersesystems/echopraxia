package com.tersesystems.echopraxia;

import java.util.function.Supplier;

public interface LoggerResolver {
    Logger get(LoggerFactory factory);
}
