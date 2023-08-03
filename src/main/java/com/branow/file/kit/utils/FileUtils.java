package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class contains various methods for manipulating files (such as creating, deleting,
 * moving, copying). The methods work with instances of classes such as {@link File} and
 * {@link Path}, some method also works with {@link String}. Almost all methods were created
 * to manipulate with {@link Path} so there is transformation (using {@link File#toPath()})
 * to {@link Path} from {@link File} and {@link String}. The class is based on the methods
 * of the class {@link Files} and realizes functions that {@link Files} doesn't contain
 * or wraps them into another appearance (method's signature).
 */
public class FileUtils {

    /**
     * The method creates a file by the gotten path according to gotten creation options.
     * To see more about the options {@link FileCreator.CreateOption}
     *
     * @param path    the path to the file to create
     * @param options the creation options using to create file
     * @see FileCreator#of(FileCreator.CreateOption...)
     * @see FileCreator#create(Path)
     */
    public static void create(String path, FileCreator.CreateOption... options) {
        create(Path.of(path), options);
    }

    /**
     * The method creates a file by the gotten path according to gotten creation options.
     * To see more about the options {@link FileCreator.CreateOption}
     *
     * @param file    the path to the file to create
     * @param options the creation options using to create file
     * @see FileCreator#of(FileCreator.CreateOption...)
     * @see FileCreator#create(Path)
     */
    public static void create(File file, FileCreator.CreateOption... options) {
        create(file.toPath(), options);
    }

    /**
     * The method creates a file by the gotten path according to gotten creation options.
     * To see more about the options {@link FileCreator.CreateOption}
     *
     * @param path    the path to the file to create
     * @param options the creation options using to create file
     * @see FileCreator#of(FileCreator.CreateOption...)
     * @see FileCreator#create(Path)
     */
    public static void create(Path path, FileCreator.CreateOption... options) {
        FileCreator.of(options).create(path);
    }



    /**
     * The method deletes a file by the gotten path according to gotten deleting options.
     * To see more about the options {@link FileDeleter.DeleteOption}
     *
     * @param path    the path to the file to delete.
     * @param options the deleting options using to delete file
     * @see FileDeleter#of(FileDeleter.DeleteOption...)
     * @see FileDeleter#delete(Path)
     */
    public static void delete(String path, FileDeleter.DeleteOption... options) {
        delete(Path.of(path), options);
    }

    /**
     * The method deletes a file by the gotten path according to gotten deleting options.
     * To see more about the options {@link FileDeleter.DeleteOption}
     *
     * @param file    the path to the file to delete.
     * @param options the deleting options using to delete file
     * @see FileDeleter#of(FileDeleter.DeleteOption...)
     * @see FileDeleter#delete(Path)
     */
    public static void delete(File file, FileDeleter.DeleteOption... options) {
        delete(file.toPath(), options);
    }

    /**
     * The method deletes a file by the gotten path according to gotten deleting options.
     * To see more about the options {@link FileDeleter.DeleteOption}
     *
     * @param path    the path to the file to delete.
     * @param options the deleting options using to delete file
     * @see FileDeleter#of(FileDeleter.DeleteOption...)
     * @see FileDeleter#delete(Path)
     */
    public static void delete(Path path, FileDeleter.DeleteOption... options) {
        FileDeleter.of(options).delete(path);
    }



    /**
     * The method moves the gotten file to the target directory.
     *
     * @param src       the path of the file that is moved.
     * @param targetDir the target directory into which the gotten file is moved
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist,
     *                                  also if in the target directory already exists file with the same name as {@code src}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static File move(File src, File targetDir) {
        return move(src, targetDir, false);
    }

    /**
     * The method moves the gotten file to the target directory.
     *
     * @param src       the path of the file that is moved.
     * @param targetDir the target directory into which the gotten file is moved
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist,
     *                                  also if in the target directory already exists file with the same name as {@code src}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static Path move(Path src, Path targetDir) {
        return move(src, targetDir, false);
    }

    /**
     * The method moves the gotten file to the target directory.
     *
     * @param src       the path of the file that is moved.
     * @param targetDir the target directory into which the gotten file is moved
     * @param exchange  if it's {@code true} and in the target directory already
     *                  exists file with the same name as {@code src}, it is exchanged.
     *                  But if it's {@code false}, the {@link IllegalArgumentException} is thrown.
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist,
     *                                  also if in the target directory already exists file with the same name as {@code src}
     *                                  and {@code exchange is false}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static File move(File src, File targetDir, boolean exchange) {
        return move(src.toPath(), targetDir.toPath(), exchange).toFile();
    }

    /**
     * The method moves the gotten file to the target directory.
     *
     * @param src       the path of the file that is moved.
     * @param targetDir the target directory into which the gotten file is moved
     * @param exchange  if it's {@code true} and in the target directory already
     *                  exists file with the same name as {@code src}, it is exchanged.
     *                  But if it's {@code false}, the {@link IllegalArgumentException} is thrown.
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist,
     *                                  also if in the target directory already exists file with the same name as {@code src}
     *                                  and {@code exchange is false}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static Path move(Path src, Path targetDir, boolean exchange) {
        if (src == null) throw new NullPointerException("src is null");
        if (targetDir == null) throw new NullPointerException("targetDir is null");
        if (Files.notExists(src)) throw new IllegalArgumentException("src doesn't exist: " + src);
        if (Files.notExists(targetDir)) throw new IllegalArgumentException("targetDir doesn't exist: " + targetDir);
        if (isNotDirectory(targetDir)) throw new IllegalArgumentException("targetDir is not a directory: " + targetDir);

        Path target = Path.of(targetDir.toString(), src.getFileName().toString());

        if (Files.exists(target)) {
            if (exchange)
                delete(target, FileDeleter.DeleteOption.WITH_CONTENT);
            else
                throw new IllegalArgumentException("Target file already exists: " + target);
        }

        try {
            moveFile(src, target);
            return target;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * The method renames the gotten file to the new name.
     *
     * @param src         the path of the file that is renamed.
     * @param newFileName the new name of file.
     * @throws NullPointerException     if {@code src} or {@code newFileName} is null
     * @throws IllegalArgumentException if {@code src} doesn't exist. If in the file's parent's
     *                                  directory already exists file with the same name as {@code newFileName}. If
     *                                  {@code newFileName} is the same as current file name.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static File rename(File src, String newFileName) {
        return rename(src, newFileName, false);
    }

    /**
     * The method renames the gotten file to the new name.
     *
     * @param src         the path of the file that is renamed.
     * @param newFileName the new name of file.
     * @throws NullPointerException     if {@code src} or {@code newFileName} is null
     * @throws IllegalArgumentException if {@code src} doesn't exist. If in the file's parent's
     *                                  directory already exists file with the same name as {@code newFileName}. If
     *                                  {@code newFileName} is the same as current file name.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static Path rename(Path src, String newFileName) {
        return rename(src, newFileName, false);
    }

    /**
     * The method renames the gotten file to the new name.
     *
     * @param src         the path of the file that is renamed.
     * @param newFileName the new name of file.
     * @param exchange    if it's {@code true} and in the file's parent's directory already
     *                    exists file with the same name as {@code newFileName}, it is exchanged.
     *                    But if it's {@code false}, the {@link IllegalArgumentException} is thrown.
     * @throws NullPointerException     if {@code src} or {@code newFileName} is null
     * @throws IllegalArgumentException if {@code src} doesn't exist. If in the file's parent's
     *                                  directory already exists file with the same name as {@code newFileName} and {@code exchange}
     *                                  is {@code false}. If {@code newFileName} is the same as current file name and
     *                                  {@code exchange} is {@code false}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static File rename(File src, String newFileName, boolean exchange) {
        return rename(src.toPath(), newFileName, exchange).toFile();
    }

    /**
     * The method renames the gotten file to the new name.
     *
     * @param src         the path of the file that is renamed.
     * @param newFileName the new name of file.
     * @param exchange    if it's {@code true} and in the file's parent's directory already
     *                    exists file with the same name as {@code newFileName}, it is exchanged.
     *                    But if it's {@code false}, the {@link IllegalArgumentException} is thrown.
     * @throws NullPointerException     if {@code src} or {@code newFileName} is null
     * @throws IllegalArgumentException if {@code src} doesn't exist. If in the file's parent's
     *                                  directory already exists file with the same name as {@code newFileName} and {@code exchange}
     *                                  is {@code false}. If {@code newFileName} is the same as current file name and
     *                                  {@code exchange} is {@code false}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during moving file.
     * @see Files#move(Path, Path, CopyOption...)
     */
    public static Path rename(Path src, String newFileName, boolean exchange) {
        if (src == null) throw new NullPointerException("src is null");
        if (newFileName == null) throw new NullPointerException("newFileName is null");
        if (Files.notExists(src)) throw new IllegalArgumentException("src doesn't exist: " + src);
        if (newFileName.isEmpty()) throw new IllegalArgumentException("newFileName is empty");
        if (src.getFileName().toString().equalsIgnoreCase(newFileName) && !exchange)
            throw new IllegalArgumentException("newFileName is the same as previous: src -> " + src + " new name -> " + newFileName);

        Path target = Path.of(src.getParent().toString(), newFileName);

        if (Files.exists(target)) {
            if (exchange)
                delete(target, FileDeleter.DeleteOption.WITH_CONTENT);
            else
                throw new IllegalArgumentException("Target file already exists: " + target);
        }

        try {
            moveFile(src, target);
            return target;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * The method copies the gotten file to the parent directory. There is adding suffix
     * -copy to copied file.
     *
     * @param src the path of the file that is copied.
     * @throws NullPointerException     if {@code src} is null
     * @throws IllegalArgumentException if {@code src} doesn't exist.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during copying the file.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static File copy(File src) {
        return copy(src.toPath()).toFile();
    }

    /**
     * The method copies the gotten file to the parent directory. There is adding suffix
     * -copy to copied file.
     *
     * @param src the path of the file that is copied.
     * @throws NullPointerException     if {@code src} is null
     * @throws IllegalArgumentException if {@code src} doesn't exist.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during copying the file.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static Path copy(Path src) {
        if (src == null) throw new NullPointerException("src is null");
        if (Files.notExists(src)) throw new IllegalArgumentException("src doesn't exist: " + src);


        Path target = Path.of(src.getParent().toString(), getCopiedFileName(src, 1));
        int i = 2;
        while (Files.exists(target)) {
            target = Path.of(src.getParent().toString(), getCopiedFileName(src, i));
            i++;
        }

        try {
            copyFile(src, target);
            return target;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The method copies the gotten file to the target directory.
     *
     * @param src       the path of the file that is copied.
     * @param targetDir the target directory into which the gotten file is copied
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist. If in
     *                                  the target directory already exists file with the same name as {@code src}. If
     *                                  {@code targetDir} and {@code src} parent directory is the same.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during copying the file.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static File copy(File src, File targetDir) {
        return copy(src, targetDir, false);
    }

    /**
     * The method copies the gotten file to the target directory.
     *
     * @param src       the path of the file that is copied.
     * @param targetDir the target directory into which the gotten file is copied
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist. If in
     *                                  the target directory already exists file with the same name as {@code src}. If
     *                                  {@code targetDir} and {@code src} parent directory is the same.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during copying the file.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static Path copy(Path src, Path targetDir) {
        return copy(src, targetDir, false);
    }

    /**
     * The method copies the gotten file to the target directory.
     *
     * @param src       the path of the file that is copied.
     * @param targetDir the target directory into which the gotten file is copied
     * @param exchange  if it's {@code true} and in the target directory already
     *                  exists file with the same name as {@code src}, it is exchanged.
     *                  But if it's {@code false}, the {@link IllegalArgumentException} is thrown.
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist. If in
     *                                  the target directory already exists file with the same name as {@code src} and
     *                                  {@code exchange} is {@code false}. If {@code targetDir} and {@code src} parent directory
     *                                  is the same and {@code exchange} is {@code false}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during copying the file.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static File copy(File src, File targetDir, boolean exchange) {
        return copy(src.toPath(), targetDir.toPath(), exchange).toFile();
    }

    /**
     * The method copies the gotten file to the target directory.
     *
     * @param src       the path of the file that is copied.
     * @param targetDir the target directory into which the gotten file is copied
     * @param exchange  if it's {@code true} and in the target directory already
     *                  exists file with the same name as {@code src}, it is exchanged.
     *                  But if it's {@code false}, the {@link IllegalArgumentException} is thrown.
     * @throws NullPointerException     if {@code src} or {@code targetDir} is null
     * @throws IllegalArgumentException if {@code src} or {@code targetDir} doesn't exist. If in
     *                                  the target directory already exists file with the same name as {@code src} and
     *                                  {@code exchange} is {@code false}. If {@code targetDir} and {@code src} parent directory
     *                                  is the same and {@code exchange} is {@code false}.
     * @throws RuntimeIOException       if there is thrown {@link IOException} during copying the file.
     * @see Files#copy(Path, Path, CopyOption...)
     */
    public static Path copy(Path src, Path targetDir, boolean exchange) {
        if (src == null) throw new NullPointerException("src is null");
        if (targetDir == null) throw new NullPointerException("targetDir is null");
        if (Files.notExists(src)) throw new IllegalArgumentException("src doesn't exist: " + src);
        if (Files.notExists(targetDir)) throw new IllegalArgumentException("targetDir doesn't exist: " + targetDir);
        if (src.getParent().equals(targetDir) && !exchange) throw new IllegalArgumentException(
                "targetDir and srcDir is the same: target -> " + targetDir + " src -> " + src.getParent());

        Path target = Path.of(targetDir.toString(), src.getFileName().toString());

        if (Files.exists(target)) {
            if (exchange)
                delete(target, FileDeleter.DeleteOption.WITH_CONTENT);
            else
                throw new IllegalArgumentException("Target file already exists: " + target);
        }

        try {
            copyFile(src, target);
            return target;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * The method tests whether a file is not a directory. It is a contrary method to
     * {@link FileUtils#isDirectory(File)}
     *
     * @param file the path to the file to test
     * @return {@code true} - if file is not a directory
     * @throws NullPointerException     if {@code path} is null
     * @throws IllegalArgumentException if {@code path} doesn't exist
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public static boolean isNotDirectory(File file) {
        return isNotDirectory(file.toPath());
    }

    /**
     * The method tests whether a file is not a directory. It is a contrary method to
     * {@link FileUtils#isDirectory(Path)}
     *
     * @param path the path to the file to test
     * @return {@code true} - if file is not a directory
     * @throws NullPointerException     if {@code path} is null
     * @throws IllegalArgumentException if {@code path} doesn't exist
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public static boolean isNotDirectory(Path path) {
        return !isDirectory(path);
    }



    /**
     * The method tests whether a file is a directory.
     *
     * @param file the path to the file to test
     * @return {@code true} - if file is a directory
     * @throws NullPointerException     if {@code path} is null
     * @throws IllegalArgumentException if {@code path} doesn't exist
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public static boolean isDirectory(File file) {
        return isDirectory(file.toPath());
    }

    /**
     * The method tests whether a file is a directory.
     *
     * @param path the path to the file to test
     * @return {@code true} - if file is a directory
     * @throws NullPointerException     if {@code path} is null
     * @throws IllegalArgumentException if {@code path} doesn't exist
     * @see Files#isDirectory(Path, LinkOption...)
     */
    public static boolean isDirectory(Path path) {
        if (path == null) throw new NullPointerException("path is null");
        if (Files.notExists(path)) throw new IllegalArgumentException("path doesn't exist: " + path);
        return Files.isDirectory(path);
    }



    /**
     * The method returns the list of entities of gotten directory.
     *
     * @param dir the path to the directory.
     * @return list of entities of the directory.
     * @throws NullPointerException     if {@code dir} is null.
     * @throws IllegalArgumentException if {@code dir} doesn't exist or is not a directory.
     * @throws RuntimeIOException       if {@link IOException} is thrown during getting the entities.
     * @see Files#list(Path)
     */
    public static List<File> children(File dir) {
        return children(dir.toPath()).stream().map(Path::toFile).toList();
    }

    /**
     * The method returns the list of entities of gotten directory.
     *
     * @param dir the path to the directory.
     * @return list of entities of the directory.
     * @throws NullPointerException     if {@code dir} is null.
     * @throws IllegalArgumentException if {@code dir} doesn't exist or is not a directory.
     * @throws RuntimeIOException       if {@link IOException} is thrown during getting the entities.
     * @see Files#list(Path)
     */
    public static List<Path> children(Path dir) {
        if (dir == null) throw new NullPointerException("dir is null");
        if (Files.notExists(dir)) throw new IllegalArgumentException("dir doesn't exist: " + dir);
        if (isNotDirectory(dir)) throw new IllegalArgumentException("dir is not a directory: " + dir);

        try (Stream<Path> stream = Files.list(dir)) {
            return stream.toList();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method returns a directory entry at gotten position.
     *
     * @param dir      the path to the directory.
     * @param position the position of the file entry at directory. Numbers of the positions
     *                 start from zero (0, 1, 2 ...).
     * @return the path of entry wrapped to {@link Optional}.
     * @throws NullPointerException     if {@code dir} is null.
     * @throws IllegalArgumentException if {@code dir} doesn't exist or is not a directory.
     * @throws RuntimeIOException       if {@link IOException} is thrown during getting the entry.
     * @see Files#list(Path)
     */
    public static Optional<File> childAt(File dir, int position) {
        return childAt(dir.toPath(), position).map(Path::toFile);
    }

    /**
     * The method returns a directory entry at gotten position.
     *
     * @param dir      the path to the directory.
     * @param position the position of the file entry at directory. Numbers of the positions
     *                 start from zero (0, 1, 2 ...).
     * @return the path of entry wrapped to {@link Optional}.
     * @throws NullPointerException     if {@code dir} is null.
     * @throws IllegalArgumentException if {@code dir} doesn't exist or is not a directory.
     * @throws RuntimeIOException       if {@link IOException} is thrown during getting the entry.
     * @see Files#list(Path)
     */
    public static Optional<Path> childAt(Path dir, int position) {
        if (dir == null) throw new NullPointerException("dir is null");
        if (Files.notExists(dir)) throw new IllegalArgumentException("dir doesn't exist: " + dir);
        if (isNotDirectory(dir)) throw new IllegalArgumentException("dir is not a directory: " + dir);
        if (position < 0) throw new IllegalArgumentException("position mustn't be minus: " + position);

        try (Stream<Path> stream = Files.list(dir)) {
            return stream.skip(position).findFirst();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method returns a number of the position of gotten child file in parent directory
     *
     * @param child the path of child file which position is returned.
     * @return the number of the position of gotten child file in parent directory
     * @throws NullPointerException     if {@code child} is null.
     * @throws IllegalArgumentException if {@code child} doesn't exist.
     * @throws RuntimeIOException       if {@link IOException} is thrown during searching the entry.
     * @throws IllegalStateException    if the method cannot find the {@code position}.
     * @see Files#list(Path)
     */
    public static int indexOf(File child) {
        return indexOf(child.toPath());
    }

    /**
     * The method returns a number of the position of gotten child file in parent directory
     *
     * @param child the path of child file which position is returned.
     * @return the number of the position of gotten child file in parent directory
     * @throws NullPointerException     if {@code child} is null.
     * @throws IllegalArgumentException if {@code child} doesn't exist.
     * @throws RuntimeIOException       if {@link IOException} is thrown during searching the entry.
     * @throws IllegalStateException    if the method cannot find the {@code position}.
     * @see Files#list(Path)
     */
    public static int indexOf(Path child) {
        if (child == null) throw new NullPointerException("child is null");
        if (Files.notExists(child)) throw new IllegalArgumentException("child doesn't exist: " + child);

        try (Stream<Path> stream = Files.list(child.getParent())) {
            int i = 0;
            Iterator<Path> iterator = stream.iterator();
            while (iterator.hasNext()) {
                if (child.equals(iterator.next())) {
                    return i;
                }
                i++;
            }
            throw new IllegalStateException("Cannot find such file in parent directory: " + child);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method tests weather a gotten path ({@code  child}) is an entry file of
     * gotten directory ({@code  dir}). It tests whether a parent of {@code  child}
     * equals a {@code  dir}.
     *
     * @param dir   the path of directory which is tested to contains the file.
     * @param child the path of child file which is searched in the directory.
     * @return {@code true} - if the {@code  child} is an entry of {@code  dir}.
     * @throws NullPointerException     if {@code  dir} or {@code child} is null.
     * @throws IllegalArgumentException if {@code  dir} or {@code child} doesn't exist.
     *                                  Also, if {@code  dir} is not a directory.
     * @see Path#getParent()
     */
    public static boolean contains(File dir, File child) {
        return contains(dir.toPath(), child.toPath());
    }

    /**
     * The method tests weather a gotten path ({@code  child}) is an entry file of
     * gotten directory ({@code  dir}). It tests whether a parent of {@code  child}
     * equals a {@code  dir}.
     *
     * @param dir   the path of directory which is tested to contains the file.
     * @param child the path of child file which is searched in the directory.
     * @return {@code true} - if the {@code  child} is an entry of {@code  dir}.
     * @throws NullPointerException     if {@code  dir} or {@code child} is null.
     * @throws IllegalArgumentException if {@code  dir} or {@code child} doesn't exist.
     *                                  Also, if {@code  dir} is not a directory.
     * @see Path#getParent()
     */
    public static boolean contains(Path dir, Path child) {
        if (dir == null) throw new NullPointerException("dir is null");
        if (child == null) throw new NullPointerException("child is null");
        if (Files.notExists(dir)) throw new IllegalArgumentException("dir doesn't exist: " + dir);
        if (Files.notExists(child)) throw new IllegalArgumentException("child doesn't exist: " + dir);
        if (isNotDirectory(dir)) throw new IllegalArgumentException("dir is not a directory: " + dir);

        return dir.equals(child.getParent());
    }



    /**
     * The method tests weather a gotten directory doesn't have any entry (child) file.
     *
     * @param dir the path of directory which is tested.
     * @return {@code true} - if the {@code  dir} doesn't have any entry file.
     * @throws NullPointerException     if {@code  dir} is null.
     * @throws IllegalArgumentException if {@code  dir} doesn't exist or is not a directory
     * @throws RuntimeIOException       if {@link IOException} is thrown during testing.
     * @see Files#list(Path)
     */
    public static boolean isEmpty(File dir) {
        return isEmpty(dir.toPath());
    }

    /**
     * The method tests weather a gotten directory doesn't have any entry (child) file.
     *
     * @param dir the path of directory which is tested.
     * @return {@code true} - if the {@code  dir} doesn't have any entry file.
     * @throws NullPointerException     if {@code  dir} is null.
     * @throws IllegalArgumentException if {@code  dir} doesn't exist or is not a directory
     * @throws RuntimeIOException       if {@link IOException} is thrown during testing.
     * @see Files#list(Path)
     */
    public static boolean isEmpty(Path dir) {
        if (dir == null) throw new NullPointerException("dir is null");
        if (Files.notExists(dir)) throw new IllegalArgumentException("dir doesn't exist: " + dir);
        if (isNotDirectory(dir)) throw new IllegalArgumentException("dir is not a directory: " + dir);

        try (Stream<Path> stream = Files.list(dir)) {
            return stream.findAny().isEmpty();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method counts a number of entry files in a gotten directory.
     *
     * @param dir the path of directory entry files of which is counted.
     * @return the number of the entry files in the gotten directory.
     * @throws NullPointerException     if {@code  dir} is null.
     * @throws IllegalArgumentException if {@code  dir} doesn't exist or is not a directory
     * @throws RuntimeIOException       if {@link IOException} is thrown during testing.
     * @see Files#list(Path)
     */
    public static int childrenSize(File dir) {
        return childrenSize(dir.toPath());
    }

    /**
     * The method counts a number of entry files in a gotten directory.
     *
     * @param dir the path of directory entry files of which is counted.
     * @return the number of the entry files in the gotten directory.
     * @throws NullPointerException     if {@code  dir} is null.
     * @throws IllegalArgumentException if {@code  dir} doesn't exist or is not a directory
     * @throws RuntimeIOException       if {@link IOException} is thrown during testing.
     * @see Files#list(Path)
     */
    public static int childrenSize(Path dir) {
        if (dir == null) throw new NullPointerException("dir is null");
        if (Files.notExists(dir)) throw new IllegalArgumentException("dir doesn't exist: " + dir);
        if (isNotDirectory(dir)) throw new IllegalArgumentException("dir is not a directory: " + dir);

        try (Stream<Path> stream = Files.list(dir)) {
            return (int) stream.count();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method get cut name of a file without file extension.
     *
     * @param file the path of the file which name is returned
     * @return the cut name of tje file without file extension.
     * @throws NullPointerException if {@code  file} is null.
     */
    public static String getFileNameWithoutExtension(File file) {
        return getFileNameWithoutExtension(file.toPath());
    }

    /**
     * The method get cut name of a file without file extension.
     *
     * @param path the path of the file which name is returned
     * @return the cut name of tje file without file extension.
     * @throws NullPointerException if {@code  path} is null.
     */
    public static String getFileNameWithoutExtension(Path path) {
        if (path == null) throw new NullPointerException("path is null");
        return path.getFileName().toString().replaceFirst("[.][^.]+$", "");
    }



    /**
     * The method goes around the file tree of gotten root in breadth. The bypass is in order the first is
     * root file,the next is the first generation of root descendants (file entries of root file),the second
     * generation (file entries of the first generation) and so on.
     *
     * @param root the path of the root file which is gone around.
     * @return the list of all gone files.
     * @throws NullPointerException     if {@code  root} is null.
     * @throws IllegalArgumentException if {@code root} doesn't exist.
     */
    public static List<Path> goInBreadth(Path root) {
        if (root == null) throw new NullPointerException("root is null");
        if (Files.notExists(root)) throw new IllegalArgumentException("root doesn't exist: " + root);
        return goInWidth(new LinkedList<>(List.of(root)));
    }

    /**
     * The method goes around the file tree of gotten root in breadth. The bypass is in order the first is
     * root file,the next is the first generation of root descendants (file entries of root file),the second
     * generation (file entries of the first generation) and so on.
     *
     * @param root the path of the root file which is gone around.
     * @return the stream of all gone files.
     * @throws NullPointerException     if {@code  root} is null.
     * @throws IllegalArgumentException if {@code root} doesn't exist.
     */
    public static Stream<Path> goInBreadthStream(Path root) {
        Iterator<Path> iterator = new Iterator<>() {
            final LinkedList<Path> next = new LinkedList<>(List.of(root));

            @Override
            public boolean hasNext() {
                return !next.isEmpty();
            }

            @Override
            public Path next() {
                Path current = next.pollFirst();
                if (isDirectory(current))
                    next.addAll(children(current));
                return current;
            }

        };
        Spliterator<Path> spliterator =
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT);
        return StreamSupport.stream(spliterator, true);
    }


    private static void moveFile(Path src, Path target) throws IOException {
        if (Files.isDirectory(src)) {
            Files.createDirectory(target);
            for (Path child : children(src)) {
                Path childTarget = Path.of(target.toString(), child.getFileName().toString());
                if (Files.isDirectory(child)) {
                    moveFile(child, childTarget);
                } else {
                    Files.move(child, childTarget);
                }
            }
        } else {
            Files.move(src, target);
        }
        Files.deleteIfExists(src);
    }

    private static void copyFile(Path src, Path target) throws IOException {
        if (Files.isDirectory(src)) {
            Files.createDirectory(target);
            for (Path child : children(src)) {
                Path childTarget = Path.of(target.toString(), child.getFileName().toString());
                if (Files.isDirectory(child)) {
                    copyFile(child, childTarget);
                } else {
                    Files.copy(child, childTarget);
                }
            }
        } else {
            Files.copy(src, target);
        }
    }

    private static String getCopiedFileName(Path src, int number) {
        String srcName = src.getFileName().toString();
        String num = number > 1 ? "-" + number : "";
        if (Files.isDirectory(src)) {
            return srcName + "-copy" + num;
        } else {
            String[] parts = src.getFileName().toString().split("\\.");
            int index = Math.max(0, parts.length - 2);
            parts[index] = parts[index] + "-copy" + num;
            return String.join(".", parts);
        }
    }

    private static List<Path> goInWidth(LinkedList<Path> next) {
        List<Path> bypassed = new ArrayList<>();
        if (next.isEmpty()) return bypassed;
        while (!next.isEmpty()) {
            Path temp = next.pollFirst();
            bypassed.add(temp);
            if (isDirectory(temp))
                next.addAll(children(temp));
        }
        return bypassed;
    }
}
