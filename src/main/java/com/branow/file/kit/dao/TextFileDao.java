package com.branow.file.kit.dao;

import com.branow.file.kit.io.TextFile;

import java.util.Arrays;
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

    /**
     * @param file             The file to read and writes dao elements.
     * @param elementSeparator The string separator to separate element in file.
     * @param converter        The converter to transform element string to java object.
     * @param functionGetId    The function that allows to get id (identifier) of any dao element.
     * @throws NullPointerException     if at least one of the parameters is null.
     * @throws IllegalArgumentException if the given {@code elementSeparator} is empty
     */
    public TextFileDao(TextFile file, String elementSeparator, StringConverter<T> converter, Function<T, Id> functionGetId) {
        super(file, elementSeparator, converter, functionGetId);
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
        return Arrays.stream(list.split(elementSeparator)).map(converter::fromString).iterator();
    }

    /**
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return super.toString().replaceFirst(super.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
}
