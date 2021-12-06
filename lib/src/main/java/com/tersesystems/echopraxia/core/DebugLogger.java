package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.Arguments;
import com.tersesystems.echopraxia.Markers;

public interface DebugLogger {

    void debug(String message);

    void debug(String message, Arguments args);

    void debug(String message, Throwable e);

    void debug(String message, Arguments args, Throwable e);

    void debug(Markers markers, String message);

    void debug(Markers markers, String message, Arguments args);

    void debug(Markers markers, String message, Arguments args, Throwable e);

    interface Impl extends DebugLogger {

        CoreLogger core();

        @Override
        default void debug(String message) {
            core().log(Level.DEBUG, message);
        }

        @Override
        default void debug(String message, Arguments a) {
            core().log(Level.DEBUG, message, a);
        }

        @Override
        default void debug(String message, Throwable e) {
            core().log(Level.DEBUG, message, e);
        }

        @Override
        default void debug(String message, Arguments a, Throwable e) {
            core().log(Level.DEBUG, message, e);
        }

        @Override
        default void debug(Markers markers, String message) {
            core().log(Level.DEBUG, markers, message);
        }

        @Override
        default void debug(Markers markers, String message, Arguments args) {
            core().log(Level.DEBUG, markers, message, args);
        }

        @Override
        default void debug(Markers markers, String message, Arguments args, Throwable e) {
            core().log(Level.DEBUG, markers, message, args, e);
        }
    }
}
