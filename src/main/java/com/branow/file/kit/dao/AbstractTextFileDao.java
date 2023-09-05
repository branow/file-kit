package com.branow.file.kit.dao;

import com.branow.file.kit.io.TextFile;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The {@code AbstractTextFileDao} allows to operate (select, update, remove, insert) objects
 * written in the given file. It reads and transform data from the file to list string representation
 * where every element can be transformed to java object by the given {@code converter}. All the
 * elements in a file separated by the give {@code elementSeparator}. <br>
 * This class realizes all methods of {@link Dao} interface that allows to do all possible action
 * with the object elements.
 *
 * @param <T>  the type of elements in this Data Access Object
 * @param <Id> the type of identifier which every element has.
 * @see Dao
 */
public abstract class AbstractTextFileDao<T, Id> implements Dao<T, Id> {

    protected final TextFile file;
    protected final Function<T, Id> functionGetId;

    /**
     * @param file          The file to read and writes dao elements.
     * @param functionGetId The function that allows to get id (identifier) of any dao element.
     * @throws NullPointerException     if at least one of the parameters is null.
     * @throws IllegalArgumentException if the given {@code elementSeparator} is empty
     */
    public AbstractTextFileDao(TextFile file, Function<T, Id> functionGetId) {
        Objects.requireNonNull(file, "The given file is null");
        Objects.requireNonNull(functionGetId, "The given functionGetId is null");
        this.file = file;
        this.functionGetId = functionGetId;
    }

    /**
     * @return The file.
     */
    public TextFile getFile() {
        return file;
    }

    /**
     * @return The function to get id from the element.
     */
    public Function<T, Id> getFunctionGetId() {
        return functionGetId;
    }


    /**
     * Returns all the elements read from the file.
     *
     * @return All the elements.
     */
    @Override
    public List<T> select() {
        return stream().toList();
    }

    /**
     * Returns the element from the file which id is equal to the given {@code id}.
     *
     * @param id The identifier to get the element.
     * @return The optional of the found element by the given {@code id}.
     */
    @Override
    public Optional<T> select(Id id) {
        return stream().filter(e -> functionGetId.apply(e).equals(id)).findAny();
    }

    /**
     * Returns the elements from the file that satisfy the condition.
     * The condition is used in the same way as in {@link Stream#filter(Predicate)}.
     *
     * @param condition The predicate function that gets as parameter the element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     * @return All the elements that satisfy the condition.
     */
    @Override
    public List<T> select(Predicate<T> condition) {
        return stream().filter(condition).toList();
    }

    /**
     * Inserts (adds) new element/object to this file.
     * It calls methods {@link AbstractTextFileDao#insert(Collection)}.
     *
     * @param object The new element.
     * @throws IllegalArgumentException if the element with the same id is already inserted.
     */
    @Override
    public void insert(T object) {
        insert(List.of(object));
    }

    /**
     * Inserts (adds) new elements to this file.
     *
     * @param collection The new elements.
     * @throws IllegalArgumentException if at least the one element is already inserted.
     */
    @Override
    public void insert(Collection<T> collection) {
        List<T> elements = stream().collect(Collectors.toList());
        List<T> same = elements.stream()
                .filter(e -> collection.stream().map(functionGetId).toList().contains(functionGetId.apply(e)))
                .toList();


        if (same.isEmpty()) {
            elements.addAll(collection);
            overwrite(elements);
        } else {
            throw new IllegalArgumentException("Elements with the same ids already inserted: " + same);
        }
    }

    /**
     * Updates the existent element with the given elements in this file.
     * It calls {@link AbstractTextFileDao#update(Collection)}
     *
     * @param object The new element that exchanges the old element with the same id in this file.
     * @throws IllegalArgumentException if there is no element with an id equal to the given element id.
     */
    @Override
    public void update(T object) {
        update(List.of(object));
    }

    /**
     * Updates the existent elements with the given elements in this file.
     * The elements are matched by their id so if there isn't an id equal to the given element id,
     * the {@link IllegalArgumentException} is thrown.
     *
     * @param collection The new elements that exchanges the old elements with the same id in this file.
     * @throws IllegalArgumentException if there is no element with an id equal to the given element id at least.
     */
    @Override
    public void update(Collection<T> collection) {
        List<T> elements = stream().collect(Collectors.toList());
        List<T> updated = collection.stream().toList();
        validateUpdating(elements, updated);
        update(elements, updated);
    }

    /**
     * Updates the existent elements with the given update function in this file.
     * All the elements that satisfy the given {@code condition} are passed as parameters to
     * {@code update} function. The {@code update} function must not change the id of element because
     * it can cause losing data if the element with such id exist or throwing {@link IllegalArgumentException}
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     * @param update    The update function in which the matching elements are passed as parameters.
     * @throws IllegalArgumentException if the update function changes the element id
     */
    @Override
    public void update(Predicate<T> condition, Consumer<T> update) {
        List<T> elements = stream().collect(Collectors.toList());
        List<T> collection = elements.stream().filter(condition).peek(e -> {
            Id oldId = functionGetId.apply(e);
            update.accept(e);
            Id newId = functionGetId.apply(e);
            if (!Objects.equals(oldId, newId))
                throw new IllegalArgumentException("the update function changes the element id: " + oldId + " -> " + newId);
        }).toList();
        update(elements, collection);
    }

    /**
     * Removes the element which id is equal to the given one from the
     * file. If elements with the {@code id} equal to the given doesn't exist,
     * nothing is thrown and the method just finishes work.
     * It calls {@link AbstractTextFileDao#remove(Predicate)}.
     *
     * @param id The identifier to remove the element.
     */
    @Override
    public void remove(Id id) {
        remove(e -> id.equals(functionGetId.apply(e)));
    }

    /**
     * Removes the elements that satisfy the condition from the file.
     * The condition is used in the same way as in {@link Stream#filter(Predicate)}.
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     */
    @Override
    public void remove(Predicate<T> condition) {
        List<T> elements = stream().filter(condition.negate()).toList();
        overwrite(elements);
    }

    /**
     * Removes all the elements from the file.
     */
    @Override
    public void remove() {
        overwrite(new ArrayList<>());
    }

    /**
     * Returns a stream of all the elements of the file.
     *
     * @return The stream of all the elements.
     */
    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), true);
    }

    /**
     * Compares this object to the given object. These objects are equal
     * if their {@code files}, {@code elementSeparator}, {@code converters} and {@code functionGetIds}
     * are equal.
     *
     * @param o The given object to compare.
     * @return {@code True} if this object equals to the given.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTextFileDao<?, ?> that = (AbstractTextFileDao<?, ?>) o;
        return Objects.equals(file, that.file) && Objects.equals(functionGetId, that.functionGetId);
    }

    /**
     * Calculates a hash code of this object by {@code file}, {@code elementSeparator},
     * {@code functionGetId} and {@code converter}.
     *
     * @return The hash code of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(file, functionGetId);
    }

    /**
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + file + "]";
    }

    /**
     * Converts the given collection of object to the string representation.
     *
     * @param collection The given collection to transform.
     * @return The string representation of the given collection.
     */
    protected abstract String toString(Collection<T> collection);

    /**
     * Returns the iterator of all dao elements read from the file.
     *
     * @return The iterator of all dao elements.
     */
    protected abstract Iterator<T> iterator();


    private void update(List<T> elements, List<T> updated) {
        for (T element : updated) {
            for (int i = 0; i < elements.size(); i++) {
                T old = elements.get(i);
                if (functionGetId.apply(element).equals(functionGetId.apply(old))) {
                    elements.remove(old);
                    elements.add(i, element);
                }
            }
        }
        overwrite(elements);
    }

    private void validateUpdating(List<T> elements, List<T> updated) {
        List<Id> ids = elements.stream().map(functionGetId).toList();
        List<Id> notExist = updated.stream().map(functionGetId)
                .filter(e -> !ids.contains(e)).toList();

        if (!notExist.isEmpty()) {
            throw new IllegalArgumentException("The elements with such id doesn't exist: " + notExist);
        }
    }

    private void overwrite(Collection<T> collection) {
        file.overwriteString(toString(collection));
    }
}
