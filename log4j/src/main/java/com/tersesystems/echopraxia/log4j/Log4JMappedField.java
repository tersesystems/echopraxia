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

    public Field getTextField() {
        return this.textField;
    }

    public Field getStructuredField() {
        return this.structuredField;
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
        return new Log4JMappedField(textField.withAttribute(attr), structuredField);
    }

    @Override
    public Field withAttributes(Attributes attrs) {
        return new Log4JMappedField(textField.withAttributes(attrs), structuredField);
    }

    @Override
    public <A> Field withoutAttribute(AttributeKey<A> key) {
        return new Log4JMappedField(textField.withoutAttribute(key), structuredField);
    }

    @Override
    public Field withoutAttributes(Collection<AttributeKey<?>> keys) {
        return new Log4JMappedField(textField.withoutAttributes(keys), structuredField);
    }

    @Override
    public Field clearAttributes() {
        return new Log4JMappedField(textField.clearAttributes(), structuredField);
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
