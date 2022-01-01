package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;

import java.util.function.Function;

public class FluentLogger<FB extends Field.Builder> {

    private final CoreLogger core;

    public FluentLogger(CoreLogger core) {
        this.core = core;
    }

    public EntryBuilder atInfo() {
        return atLevel(Level.INFO);
    }

    public EntryBuilder atLevel(Level level) {
        return new EntryBuilder(level);
    }

    public class EntryBuilder {
        EntryBuilder(Level info) {

        }

        public EntryBuilder message(String s) {
            return this;
        }

        public EntryBuilder argument(Function<FB, Field> f) {
            return this;
        }

        public void log() {
        }
    }
}
