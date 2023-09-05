package com.branow.file.kit.dao;

import com.branow.file.kit.io.TextFile;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The {@code TextFileDao} is a realization of the abstract class {@link AbstractTextFileDao}.
 * It allows to do operations: selection, insertion, removing and updating with the objects/elements
 * written to the given file. During any operation the all data is read from the file and overwritten
 * into it (if there is need).
 *
 * @param <T>  the type of elements in this Data Access Object
 * @param <Id> the type of identifier which every element has.
 * @see AbstractTextFileDao
 */
public class TextFileDao<T, Id> extends AbstractTextFileDao<T, Id> {

    private final StringConverter<Collection<T>> converter;

    /**
     * @param file          The file to read and writes dao elements.
     * @param converter     The converter to transform element string to collection of elements.
     * @param functionGetId The function that allows to get id (identifier) of any dao element.
     * @throws NullPointerException     if at least one of the parameters is null.
     * @throws IllegalArgumentException if the given {@code elementSeparator} is empty
     */
    public TextFileDao(TextFile file, StringConverter<Collection<T>> converter, Function<T, Id> functionGetId) {
        super(file, functionGetId);
        this.converter = converter;
    }

    /**
     * @return The converter that transforms string to collection of elements and vice versa.
     */
    public StringConverter<Collection<T>> getConverter() {
        return converter;
    }

    /**
     * Returns the iterator of all dao elements read from the file.
     *
     * @return The iterator of all dao elements.
     */
    @Override
    protected Iterator<T> iterator() {
        String list = file.readString();
        if (list.isEmpty()) {
            Stream<T> stream = Stream.of();
            return stream.iterator();
        }
        return converter.fromString(list).iterator();
    }

    /**
     * Converts the given collection of object to the string representation.
     *
     * @param collection The given collection to transform.
     * @return The string representation of the given collection.
     */
    @Override
    protected String toString(Collection<T> collection) {
        return converter.toString(collection);
    }

    /**
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return super.toString().replaceFirst(super.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
}
