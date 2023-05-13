package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldConverter;
import org.jetbrains.annotations.NotNull;

public class JULFieldConverter implements FieldConverter {

    private static final JULFieldConverter SINGLETON = new JULFieldConverter();

    public static FieldConverter instance() {
        return SINGLETON;
    }

    @Override
    public @NotNull Object convertArgumentField(@NotNull Field field) {
        return convertField(field);
    }

    @Override
    public @NotNull Object convertLoggerField(@NotNull Field field) {
        return convertField(field);
    }

    private Object convertField(Field field) {
        return field;
    }
}
