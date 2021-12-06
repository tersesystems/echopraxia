package com.tersesystems.echopraxia;

public interface Argument<V> {
    String name();

    V value();

    static <VV> Argument<VV> from(String name, VV value) {
        return new Argument<VV>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public VV value() {
                return value;
            }
        };
    }
}
