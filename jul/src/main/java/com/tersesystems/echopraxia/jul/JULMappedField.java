package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class JULMappedField implements Field {

    private final Field textField;
    private final Field structuredField;

    public JULMappedField(Field textField, Field structuredField) {
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
    public <A> @NotNull Field withAttribute(@NotNull Attribute<A> attr) {
        return new JULMappedField(textField.withAttribute(attr), structuredField);
    }

    @Override
    public @NotNull Field withAttributes(@NotNull Attributes attrs) {
        return new JULMappedField(textField.withAttributes(attrs), structuredField);
    }

    @Override
    public <A> @NotNull Field withoutAttribute(@NotNull AttributeKey<A> key) {
        return new JULMappedField(textField.withoutAttribute(key), structuredField);
    }

    @Override
    public @NotNull Field withoutAttributes(@NotNull Collection<AttributeKey<?>> keys) {
        return new JULMappedField(textField.withoutAttributes(keys), structuredField);
    }

    @Override
    public @NotNull Field clearAttributes() {
        return new JULMappedField(textField.clearAttributes(), structuredField);
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
