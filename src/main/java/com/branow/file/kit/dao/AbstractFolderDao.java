package com.branow.file.kit.dao;

import com.branow.file.kit.io.DirectoryEntity;
import com.branow.file.kit.io.FileEntity;
import com.branow.file.kit.utils.FileDeleter;
import com.branow.file.kit.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The {@code AbstractFolderDao} is an abstraction of system folder where each file
 * can be converted to java object and vice versa. For this transforming the abstract methods
 * {@link AbstractFolderDao#read(Path)} and {@link AbstractFolderDao#read(Path)} are used.
 * To matching particular file with the object there is used the object id, string
 * representation of which is equal file name (without extension). So result of calling methods
 * {@code toString()} from different ids also must be different.
 * Important: {@code AbstractFolderDao} only works with files which extension equals given to
 * class constructor.
 *
 * @param <T>  the type of elements in this Data Access Object
 * @param <Id> the type of identifier which every element has.
 * @see Dao
 */
public abstract class AbstractFolderDao<T, Id> implements Dao<T, Id> {

    protected final DirectoryEntity folder;
    protected final Function<T, Id> functionGetId;
    protected final String fileExtension;


    /**
     * @param folder           The directory entity with files of which it is operated.
     * @param functionGetId         The function that allows to get id (identifier) of any dao element.
     * @param fileExtension The file extension string of files with which it is operated.
     * @throws NullPointerException if at least one of the parameters is null.
     */
    public AbstractFolderDao(DirectoryEntity folder, Function<T, Id> functionGetId, String fileExtension) {
        Objects.requireNonNull(folder, "The DirectoryEntity folder is null");
        Objects.requireNonNull(functionGetId, "The Function<T, Id> functionGetId is null");
        Objects.requireNonNull(fileExtension, "The String fileExtension is null");
        this.folder = folder;
        this.functionGetId = functionGetId;
        this.fileExtension = fileExtension;
    }

    /**
     * The method returns all the elements of the Data Access Object.
     *
     * @return All the elements.
     */
    @Override
    public List<T> select() {
        return stream().toList();
    }

    /**
     * The method returns the element of the Data Access Object with the given {@code id}.
     * The {@code id} string (created by calling {@link Objects#toString(Object)}) is equals
     * to file name.
     *
     * @param id The identifier to get the element.
     * @return The optional of the found element by the given {@code id}.
     */
    @Override
    public Optional<T> select(Id id) {
        Path path = path(id);
        return Files.exists(path) ? Optional.of(read(path)) : Optional.empty();
    }

    /**
     * The method returns the elements of the Data Access Object that satisfy the condition.
     * The condition is used in the same way as in {@link Stream#filter(Predicate)}.
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     * @return All the elements that satisfy the condition.
     */
    @Override
    public List<T> select(Predicate<T> condition) {
        return stream().filter(condition).toList();
    }

    /**
     * The method inserts (adds) new element/object to this Data Access Object.
     * It creates new file for the element and writes data from it to file.
     *
     * @param object The new element.
     * @throws IllegalArgumentException if the element with the same id is already inserted.
     */
    @Override
    public void insert(T object) {
        Id id = functionGetId.apply(object);
        Path path = path(id);
        if (Files.exists(path))
            throw new IllegalArgumentException("Element with the same id already inserted: " + id);
        FileUtils.create(path);
        write(object);
    }

    /**
     * The method inserts (adds) new elements to this Data Access Object.
     * It creates new files for the elements and writes data from them to the files.
     *
     * @param collection The new elements.
     * @throws IllegalArgumentException if at least the one element is already inserted.
     */
    @Override
    public void insert(Collection<T> collection) {
        List<Id> ids = collection.stream().map(functionGetId).filter(id -> select(id).isPresent()).toList();
        if (ids.isEmpty()) {
            collection.forEach(this::insert);
        } else {
            throw new IllegalArgumentException("Elements with the same ids already inserted: " + ids);
        }
    }

    /**
     * The method updates the existent element with the given elements in this Data Access Object.
     * The elements are matched by their id so if there isn't an id equal to the give element id,
     * the {@link IllegalArgumentException} is thrown.
     *
     * @param object The new element that exchanges the old element with the same id in this Data Access Object.
     * @throws IllegalArgumentException if there is no element with an id equal to the given element id.
     */
    @Override
    public void update(T object) {
        Id id = functionGetId.apply(object);
        if (Files.notExists(path(id)))
            throw new IllegalArgumentException("The element with such id doesn't exist: " + id);
        write(object);
    }

    /**
     * The method updates the existent elements with the given elements in this Data Access Object.
     * The elements are matched by their id so if there isn't an id equal to the give element id,
     * the {@link IllegalArgumentException} is thrown.
     *
     * @param collection The new elements that exchanges the old elements with the same id in this Data Access Object.
     * @throws IllegalArgumentException if there is no element with an id equal to the given element id at least.
     */
    @Override
    public void update(Collection<T> collection) {
        List<Id> idsNotExist = collection.stream().map(functionGetId).filter(id -> Files.notExists(path(id))).toList();
        if (idsNotExist.isEmpty()) {
            collection.forEach(this::write);
        } else {
            throw new IllegalArgumentException("The element with such id doesn't exist: " + idsNotExist);
        }
    }

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
    @Override
    public void update(Predicate<T> condition, Consumer<T> update) {
        List<T> updated = stream().filter(condition).peek(e -> {
            Id oldId = functionGetId.apply(e);
            update.accept(e);
            Id newId = functionGetId.apply(e);
            if (!Objects.equals(oldId, newId))
                throw new IllegalArgumentException("the update function changes the element id: " + oldId + " -> " + newId);
        }).toList();
        update(updated);
    }

    /**
     * The method removes the element and the matching file with the given {@code id} from the
     * Data Access Object. If elements with the {@code id} equal to the given doesn't exist,
     * nothing is thrown and the method just finishes work.
     *
     * @param id The identifier to remove the element.
     */
    @Override
    public void remove(Id id) {
        Path path = path(id);
        FileUtils.delete(path, FileDeleter.DeleteOption.MAY_NOT_EXIST);
    }

    /**
     * The method removes the elements and the matching files from the Data Access Object
     * that satisfy the condition. The condition is used in the same way as in {@link Stream#filter(Predicate)}.
     *
     * @param condition The predicate function that gets as parameter the dao element and return
     *                  {@code true} if the element satisfy the condition and {@code false} if it does not.
     */
    @Override
    public void remove(Predicate<T> condition) {
        stream().filter(condition).map(functionGetId).forEach(this::remove);
    }

    /**
     * The method removes all the elements and matching files of the Data Access Object.
     */
    @Override
    public void remove() {
        remove(t -> true);
    }

    /**
     * The method returns a stream of all the elements of the Data Access Object.
     *
     * @return The stream of all the elements.
     */
    @Override
    public Stream<T> stream() {
        return folder.childEntities().stream().filter(e ->
                e instanceof FileEntity && ((FileEntity) e).extension().equalsIgnoreCase(fileExtension)
        ).map(e -> read(e.path()));
    }


    public DirectoryEntity getFolder() {
        return folder;
    }

    public Function<T, Id> getFunctionGetId() {
        return functionGetId;
    }

    public String getFileExtension() {
        return fileExtension;
    }



    /**
     * The method creates a matching file path to the given id. The returned file path takes place
     * in the root folder (the folder given to class constructor). Its name equals the
     * given {@code id} string (method {@code toString()}) and file extension equal the given file
     * extension to class constructor.
     *
     * @param id The name of file.
     * @return The matching file path.
     */
    protected Path path(Id id) {
        return Path.of(folder.path().toString(), id + "." + fileExtension);
    }

    /**
     * The method writes data from the given object to the matching file
     * (using {@link AbstractFolderDao#path(Object)}).
     *
     * @param o The object from which data is written to the matching file.
     */
    protected abstract void write(T o);

    /**
     * The method read data from the given file path and converted it to dao element object.
     *
     * @param path The file path from which data is read.
     * @return The dao element object created by read data from the file.
     */
    protected abstract T read(Path path);
}
