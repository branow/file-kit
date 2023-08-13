package com.branow.file.kit.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The {@code Dao} interface describes methods that every Data Access Object must have (implements).
 * These basic methods are select, insert, update, remove and stream.
 *
 * @param <T>  the type of elements in this Data Access Object
 * @param <Id> the type of identifier which every element has.
 * @see AbstractFolderDao
 */
public interface Dao<T, Id> {

    /**
     * The method returns all the elements of the Data Access Object.
     *
     * @return All the elements.
     */
    List<T> select();

    /**
     * The method returns the element of the Data Access Object with the given {@code id}.
     *
     * @param id The identifier to get the element.
     * @return The optional of the found element by the given {@code id}.
     */
    Optional<T> select(Id id);

    /**
     * The method returns the elements of the Data Access Object that satisfy the condition.
     * The condition is used in the same way as in {@link Stream#filter(Predicate)}.
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     * @return All the elements that satisfy the condition.
     */
    List<T> select(Predicate<T> condition);

    /**
     * The method inserts (adds) new element to this Data Access Object.
     *
     * @param object The new element.
     * @throws IllegalArgumentException if the element is already inserted.
     */
    void insert(T object);

    /**
     * The method inserts (adds) new elements to this Data Access Object.
     *
     * @param collection The new elements.
     * @throws IllegalArgumentException if at least the one element is already inserted.
     */
    void insert(Collection<T> collection);

    /**
     * The method updates the existent element with the given element in this Data Access Object.
     * The element are matched by their id so if there isn't an id equal to the give element id,
     * the {@link IllegalArgumentException} is thrown.
     *
     * @param object The new element that exchanges the old element with the same id in this Data Access Object.
     * @throws IllegalArgumentException if there isn't an id equal to the give element id.
     */
    void update(T object);

    /**
     * The method updates the existent elements with the given elements in this Data Access Object.
     * The elements are matched by their id so if there isn't an id equal to the give element id,
     * the {@link IllegalArgumentException} is thrown.
     *
     * @param collection The new elements that exchanges the old elements with the same id in this Data Access Object.
     * @throws IllegalArgumentException if there isn't an id equal to the give element id.
     */
    void update(Collection<T> collection);

    /**
     * The method updates the existent elements with the given update function in this Data Access Object.
     * All the elements that satisfy the given {@code condition} are passed as parameters to
     * {@code update} function. The {@code update} function must not change the id of element because
     * it can cause losing data if the element with such id exist or throwing {@link IllegalArgumentException}
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     * @param update    The update function in which the matching elements are passed as parameters.
     * @throws IllegalArgumentException if the update function changes the element id.
     */
    void update(Predicate<T> condition, Consumer<T> update);

    /**
     * The method removes the element with the given {@code id} from the Data Access Object.
     * If elements with the {@code id} equal to the given doesn't exist, nothing is thrown and
     * the method just finishes work.
     *
     * @param id The identifier to remove the element.
     */
    void remove(Id id);

    /**
     * The method removes the elements from the Data Access Object that satisfy the condition.
     * The condition is used in the same way as in {@link Stream#filter(Predicate)}.
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     */
    void remove(Predicate<T> condition);

    /**
     * The method removes all the elements of the Data Access Object.
     */
    void remove();

    /**
     * The method returns a stream of all the elements of the Data Access Object.
     *
     * @return The stream of all the elements.
     */
    Stream<T> stream();
}
