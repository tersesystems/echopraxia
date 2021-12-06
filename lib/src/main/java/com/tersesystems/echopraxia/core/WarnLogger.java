package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.*;

public interface WarnLogger {

    void warn(String message);

    void warn(String message, Arguments a);

    void warn(String message, Throwable e);

    void warn(String message, Arguments a, Throwable e);

    void warn(Markers markers, String message);

    void warn(Markers markers, String message, Arguments a);

    void warn(Markers markers, String message, Arguments a, Throwable e);

    interface Impl extends WarnLogger {
        CoreLogger core();

        @Override
        default void warn(String message) {
            core().log(Level.WARN, message);
        }

        @Override
        default void warn(String message, Arguments a) {
            core().log(Level.WARN, message, a);
        }

        @Override
        default void warn(String message, Throwable e) {
            core().log(Level.WARN, message, e);
        }

        @Override
        default void warn(String message, Arguments a, Throwable e) {
            core().log(Level.WARN, message, e);
        }

        @Override
        default void warn(Markers markers, String message) {
            core().log(Level.WARN, markers, message);
        }

        @Override
        default void warn(Markers markers, String message, Arguments args) {
            core().log(Level.WARN, markers, message, args);
        }

        @Override
        default void warn(Markers markers, String message, Arguments args, Throwable e) {
            core().log(Level.WARN, markers, message, args, e);
        }
    }
}
