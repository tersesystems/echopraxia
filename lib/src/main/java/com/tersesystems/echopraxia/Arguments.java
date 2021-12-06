package com.tersesystems.echopraxia;

import java.util.Arrays;

public class Arguments {

    private final Object[] args;

    Arguments(Object[] args) {
        this.args = args;
    }

    public static Arguments of(Object... args) {
        return new Arguments(args);
    }

    public Object[] toObjects(Throwable e) {
        Object[] objects = Arrays.copyOf(args, args.length + 1);
        objects[args.length] = e;
        return objects;
    }

    public Object[] toObjects() {
        return args;
    }
}
