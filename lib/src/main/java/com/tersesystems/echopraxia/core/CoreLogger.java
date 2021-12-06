package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.*;

public abstract class CoreLogger {

    public abstract void log(Level level, String message);

    public abstract void log(Level level, String message, Arguments e);

    public abstract void log(Level level, String message, Throwable e);

    public abstract void log(Level level, String message, Arguments args, Throwable e);

    public abstract void log(Level level, Markers markers, String message);

    public abstract void log(Level level, Markers markers, String message, Arguments e);

    public abstract void log(Level level, Markers markers, String message, Throwable e);

    public abstract void log(Level level, Markers markers, String message, Arguments args, Throwable e);

    public static class Impl extends CoreLogger {
        private final org.slf4j.Logger logger;

        public Impl(org.slf4j.Logger logger) {
            this.logger = logger;
        }

        @Override
        public void log(Level level, String message) {
            switch(level) {
                case ERROR -> logger.error(message);
                case WARN -> logger.warn(message);
                case INFO -> logger.info(message);
                case DEBUG -> logger.debug(message);
                case TRACE -> logger.trace(message);
            }
        }

        @Override
        public void log(Level level, String message, Arguments args) {
            switch(level) {
                case ERROR -> logger.error(message, args.toObjects());
                case WARN -> logger.warn(message, args.toObjects());
                case INFO -> logger.info(message, args.toObjects());
                case DEBUG -> logger.debug(message, args.toObjects());
                case TRACE -> logger.trace(message, args.toObjects());
            }
        }

        @Override
        public void log(Level level, String message, Throwable e) {
            switch(level) {
                case ERROR -> logger.error(message, e);
                case WARN -> logger.warn(message, e);
                case INFO -> logger.info(message, e);
                case DEBUG -> logger.debug(message, e);
                case TRACE -> logger.trace(message, e);
            }
        }

        @Override
        public void log(Level level, String message, Arguments args, Throwable e) {
            Object[] argsPlusE = args.toObjects(e);
            switch(level) {
                case ERROR -> logger.error(message, argsPlusE);
                case WARN -> logger.warn(message, argsPlusE);
                case INFO -> logger.info(message, argsPlusE);
                case DEBUG -> logger.debug(message, argsPlusE);
                case TRACE -> logger.trace(message, argsPlusE);
            }
        }

        @Override
        public void log(Level level, Markers markers, String message) {
            switch(level) {
                case ERROR -> logger.error(markers.asMarker(), message);
                case WARN -> logger.warn(markers.asMarker(), message);
                case INFO -> logger.info(markers.asMarker(), message);
                case DEBUG -> logger.debug(markers.asMarker(), message);
                case TRACE -> logger.trace(markers.asMarker(), message);
            }
        }

        @Override
        public void log(Level level, Markers markers, String message, Arguments args) {
            switch(level) {
                case ERROR -> logger.error(markers.asMarker(), message, args.toObjects());
                case WARN -> logger.warn(markers.asMarker(), message, args.toObjects());
                case INFO -> logger.info(markers.asMarker(), message, args.toObjects());
                case DEBUG -> logger.debug(markers.asMarker(), message, args.toObjects());
                case TRACE -> logger.trace(markers.asMarker(), message, args.toObjects());
            }
        }

        @Override
        public void log(Level level, Markers markers, String message, Throwable e) {
            switch(level) {
                case ERROR -> logger.error(markers.asMarker(), message, e);
                case WARN -> logger.warn(markers.asMarker(), message, e);
                case INFO -> logger.info(markers.asMarker(), message, e);
                case DEBUG -> logger.debug(markers.asMarker(), message, e);
                case TRACE -> logger.trace(markers.asMarker(), message, e);
            }
        }

        @Override
        public void log(Level level, Markers markers, String message, Arguments args, Throwable e) {
            Object[] argsPlusE = args.toObjects(e);
            switch(level) {
                case ERROR -> logger.error(markers.asMarker(), message, argsPlusE);
                case WARN -> logger.warn(markers.asMarker(), message, argsPlusE);
                case INFO -> logger.info(markers.asMarker(), message, argsPlusE);
                case DEBUG -> logger.debug(markers.asMarker(), message, argsPlusE);
                case TRACE -> logger.trace(markers.asMarker(), message, argsPlusE);
            }
        }
    }
}
