package com.branow.file.kit.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@code StringCollectionConverter} allows to covert collection of objects to string
 * representation and vice versa.
 *
 * @param <T> the type of collection object.
 * */
public class StringCollectionConverter<T> implements StringConverter<Collection<T>>{

    protected final StringConverter<T> converter;
    protected final String elementSeparator;

    /**
     * @param converter The converter to convert element of collection to string.
     * @param elementSeparator The element separator string to separate elements in string representation.
     * @throws NullPointerException if at least one of the parameters is null.
     * @throws IllegalArgumentException if {@code elementSeparator} is empty.
     * */
    public StringCollectionConverter(StringConverter<T> converter, String elementSeparator) {
        Objects.requireNonNull(converter, "The converter is null");
        Objects.requireNonNull(elementSeparator, "The elementSeparator is null");
        if (elementSeparator.isEmpty()) throw new IllegalArgumentException("The elementSeparator is empty");
        this.converter = converter;
        this.elementSeparator = elementSeparator;
    }

    /**
     * @return The converter to convert element of collection to string.
     * */
    public StringConverter<T> getConverter() {
        return converter;
    }

    /**
     * @return The element separator string to separate elements in string representation.
     * */
    public String getElementSeparator() {
        return elementSeparator;
    }

    /**
     * Transforms the given string to the collection of objects and return it.
     *
     * @param str The string to transform.
     * @return The collection of objects to the given string.
     */
    @Override
    public Collection<T> fromString(String str) {
        if (str.isEmpty())
            return List.of();
        return Arrays.stream(str.split(elementSeparator)).map(converter::fromString).toList();
    }

    /**
     * Transforms the given collection of objects to the matching string and return it.
     *
     * @param collection The collection of objects to transform.
     * @return The matching string to the given collection of objects.
     */
    @Override
    public String toString(Collection<T> collection) {
        return collection.stream().map(converter::toString).collect(Collectors.joining(elementSeparator));
    }

    /**
     * Compares this object to the given object. These objects are equal
     * if their {@code converters} and {@code elementSeparators} are equal.
     *
     * @param o The given object to compare.
     * @return {@code True} if this object equals to the given.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringCollectionConverter<?> that = (StringCollectionConverter<?>) o;
        return Objects.equals(converter, that.converter) && Objects.equals(elementSeparator, that.elementSeparator);
    }

    /**
     * Calculates a hash code of this object by {@code converter} and {@code elementSeparator}.
     *
     * @return The hash code of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(converter, elementSeparator);
    }

    /**
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return "StringCollectionConverter{" +
                "converter=" + converter +
                ", elementSeparator='" + elementSeparator + '\'' +
                '}';
    }
}
