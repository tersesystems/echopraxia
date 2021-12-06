package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.*;

public interface InfoLogger {

    void info(String message);

    void info(String message, Arguments a);

    void info(String message, Throwable e);

    void info(String message, Arguments a, Throwable e);

    void info(Markers markers, String message);

    void info(Markers markers, String message, Arguments a);

    void info(Markers markers, String message, Arguments a, Throwable e);

    interface Impl extends InfoLogger {
        CoreLogger core();

        @Override
        default void info(String message) {
            core().log(Level.INFO, message);
        }

        @Override
        default void info(String message, Arguments a) {
            core().log(Level.INFO, message, a);
        }

        @Override
        default void info(String message, Throwable e) {
            core().log(Level.INFO, message, e);
        }

        @Override
        default void info(String message, Arguments a, Throwable e) {
            core().log(Level.INFO, message, e);
        }

        @Override
        default void info(Markers markers, String message) {
            core().log(Level.INFO, markers, message);
        }

        @Override
        default void info(Markers markers, String message, Arguments args) {
            core().log(Level.INFO, markers, message, args);
        }

        @Override
        default void info(Markers markers, String message, Arguments args, Throwable e) {
            core().log(Level.INFO, markers, message, args, e);
        }
    }
}
