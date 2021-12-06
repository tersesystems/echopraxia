package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.slf4j.ILoggerFactory;

public final class LoggerFactory {

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        ILoggerFactory factory = org.slf4j.LoggerFactory.getILoggerFactory();
        org.slf4j.Logger logger = factory.getLogger(name);
        return new Logger(new CoreLogger.Impl(logger));
    }

    public Logger getLogger(LoggerResolver resolver) {
        return resolver.get(this);
    }
}
