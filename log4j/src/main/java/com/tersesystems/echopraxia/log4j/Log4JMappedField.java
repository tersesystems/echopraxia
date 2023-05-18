package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class Log4JMappedField implements Field {

    private final Field textField;
    private final Field structuredField;

    public Log4JMappedField(Field textField, Field structuredField) {
        this.textField = textField;
        this.structuredField = structuredField;
    }

    @Override
    public @NotNull String name() {
        return structuredField.name();
    }

    @Override
    public @NotNull Value<?> value() {
        return structuredField.value();
    }

    @Override
    public @NotNull Attributes attributes() {
        return textField.attributes();
    }

    @Override
    public <A> Field withAttribute(Attribute<A> attr) {
        return textField.withAttribute(attr);
    }

    @Override
    public Field withAttributes(Attributes attrs) {
        return textField.withAttributes(attrs);
    }

    @Override
    public <A> Field withoutAttribute(AttributeKey<A> key) {
        return textField.withoutAttribute(key);
    }

    @Override
    public Field withoutAttributes(Collection<AttributeKey<?>> keys) {
        return textField.withoutAttributes(keys);
    }

    @Override
    public @NotNull List<Field> fields() {
        return structuredField.fields();
    }

    @Override
    public String toString() {
        return textField.toString();
    }
}
