package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldConverter;
import org.jetbrains.annotations.NotNull;

public class Log4JFieldConverter implements FieldConverter {
    private static final Log4JFieldConverter INSTANCE = new Log4JFieldConverter();

    public static Log4JFieldConverter instance() {
        return INSTANCE;
    }

    @Override
    public @NotNull Object convertArgumentField(@NotNull Field field) {
        return field;
    }

    @Override
    public @NotNull Object convertLoggerField(@NotNull Field field) {
        return field;
    }
}
