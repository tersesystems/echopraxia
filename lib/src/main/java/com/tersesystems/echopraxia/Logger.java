package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.*;

public class Logger implements ErrorLogger.Impl, WarnLogger.Impl, InfoLogger.Impl, DebugLogger.Impl, TraceLogger.Impl {

    private final CoreLogger core;

    public Logger(CoreLogger core) {
        this.core = core;
    }

    @Override
    public CoreLogger core() {
        return core;
    }
}

