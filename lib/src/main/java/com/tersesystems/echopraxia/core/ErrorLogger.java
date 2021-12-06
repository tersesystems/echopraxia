package com.tersesystems.echopraxia.core;

import com.tersesystems.echopraxia.*;

public interface ErrorLogger {

    void error(String message);

    void error(String message, Arguments a);

    void error(String message, Throwable e);

    void error(String message, Arguments a, Throwable e);

    void error(Markers markers, String message);

    void error(Markers markers, String message, Arguments a);

    void error(Markers markers, String message, Arguments a, Throwable e);

    interface Impl extends ErrorLogger {
        CoreLogger core();

        @Override
        default void error(String message) {
            core().log(Level.ERROR, message);
        }

        @Override
        default void error(String message, Arguments a) {
            core().log(Level.ERROR, message, a);
        }

        @Override
        default void error(String message, Throwable e) {
            core().log(Level.ERROR, message, e);
        }

        @Override
        default void error(String message, Arguments a, Throwable e) {
            core().log(Level.ERROR, message, e);
        }

        @Override
        default void error(Markers markers, String message) {
            core().log(Level.ERROR, markers, message);
        }

        @Override
        default void error(Markers markers, String message, Arguments args) {
            core().log(Level.ERROR, markers, message, args);
        }

        @Override
        default void error(Markers markers, String message, Arguments args, Throwable e) {
            core().log(Level.ERROR, markers, message, args, e);
        }
    }
}


