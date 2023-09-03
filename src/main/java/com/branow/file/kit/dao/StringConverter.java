package com.branow.file.kit.dao;

/**
 * The {@code StringConverter} is an interface that describes two methods for transforming
 * an object to string and vice versa.
 */
public interface StringConverter<T> {

    /**
     * Transforms the given string to the matching object and return it.
     *
     * @param str The string to transform.
     * @return The matching object to the given string.
     */
    T fromString(String str);

    /**
     * Transforms the given object to the matching string and return it.
     *
     * @param o The object to transform.
     * @return The matching string to the given object.
     */
    String toString(T o);
}
