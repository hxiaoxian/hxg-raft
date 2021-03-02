package com.hxg.sofa.jraft.util;

/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state.
 *
 *
 */
public final class Requires {


    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    public static void requireTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void requireTrue(boolean expression, Object message) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(message));
        }
    }

    public static void requireTrue(boolean expression, String fmt, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(fmt, args));
        }
    }

    private Requires() {
    }
}
