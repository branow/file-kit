package com.branow.file.kit.dao;

import com.branow.file.kit.io.TextFile;
import com.branow.file.kit.io.UniCharFileMapper;

import java.util.*;
import java.util.function.Function;

/**
 * The {@code TextFileDao} is a realization of the abstract class {@link AbstractTextFileDao}.
 * It allows to do operations: selection, insertion, removing and updating with the objects/elements
 * written to the given file. During any operation the all data is read from the file and overwritten
 * into it (if there is need) except operation reading. The reading of file is going gradually
 * that allows not to keep all data of the file in memory at a one moment. This feature gives benefit only
 * for big file (dozens megabytes and higher).
 *
 * @param <T>  the type of elements in this Data Access Object
 * @param <Id> the type of identifier which every element has.
 * @see AbstractTextFileDao
 */
public class TextFileStreamDao<T, Id> extends AbstractTextFileDao<T, Id> {

    protected final StringCollectionConverter<T> converter;

    /**
     * @param file          The file to read and writes dao elements.
     * @param converter     The converter to transform element string to collection of elements.
     * @param functionGetId The function that allows to get id (identifier) of any dao element.
     * @throws NullPointerException     if at least one of the parameters is null.
     * @throws IllegalArgumentException if the given {@code elementSeparator} is empty
     */
    public TextFileStreamDao(TextFile file, StringCollectionConverter<T> converter, Function<T, Id> functionGetId) {
        super(file, functionGetId);
        this.converter = converter;
    }

    /**
     * @return The converter that transforms string to collection of elements and vice versa.
     */
    public StringCollectionConverter<T> getConverter() {
        return converter;
    }

    /**
     * Returns the iterator of all dao elements read from the file.
     *
     * @return The iterator of all dao elements.
     */
    @Override
    public Iterator<T> iterator() {
        return new ElementIterator<>(file);
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

    private class ElementIterator<T> implements Iterator<T> {

        private final UniCharFileMapper mapper;
        private T current;

        private ElementIterator(TextFile file) {
            this.mapper = new UniCharFileMapper(file.path(), file.size(), file.charset());
        }

        @Override
        public boolean hasNext() {
            if (current == null)
                get();
            return current != null;
        }

        @Override
        public T next() {
            if (current == null)
                throw new NoSuchElementException();
            T temp = current;
            current = null;
            return temp;
        }

        private void get() {
            StringBuilder sb = new StringBuilder();
            while (mapper.hasNext()) {
                sb.append(mapper.next().toString(mapper.getCharset()));
                if (sb.toString().endsWith(converter.elementSeparator)) {
                    int index = sb.indexOf(converter.elementSeparator);
                    sb.delete(index, index + converter.elementSeparator.length());
                    break;
                }
            }
            if (!sb.isEmpty()) {
                current = (T) converter.fromString(sb.toString());
            }
        }
    }
}
