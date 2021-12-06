package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.Arguments;
import com.tersesystems.echopraxia.Markers;

public interface TraceLogger {

    void trace(String message);

    void trace(String message, Throwable e);

    void trace(String message, Arguments a);

    void trace(String message, Arguments a, Throwable e);

    void trace(Markers markers, String message);

    void trace(Markers markers, String message, Arguments a);

    void trace(Markers markers, String message, Arguments a, Throwable e);

    interface Impl extends TraceLogger {
        CoreLogger core();

        @Override
        default void trace(String message) {
            core().log(Level.WARN, message);
        }

        @Override
        default void trace(String message, Arguments a) {
            core().log(Level.WARN, message, a);
        }

        @Override
        default void trace(String message, Throwable e) {
            core().log(Level.WARN, message, e);
        }

        @Override
        default void trace(String message, Arguments a, Throwable e) {
            core().log(Level.WARN, message, e);
        }

        @Override
        default void trace(Markers markers, String message) {
            core().log(Level.WARN, markers, message);
        }

        @Override
        default void trace(Markers markers, String message, Arguments args) {
            core().log(Level.WARN, markers, message, args);
        }

        @Override
        default void trace(Markers markers, String message, Arguments args, Throwable e) {
            core().log(Level.WARN, markers, message, args, e);
        }
    }
}
