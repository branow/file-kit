package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@code FileCreator} is a class that lets delete a file according to gotten deleting options.<br>
 * DeleteOption: {@link FileDeleter.DeleteOption#WITH_CONTENT}, {@link FileDeleter.DeleteOption#MAY_NOT_EXIST}.
 * */
public class FileDeleter {

    /**
     * The {@code DeleteOption} describes possible file deleting options.
     * <ul>
     *   <li>{@link FileDeleter.DeleteOption#WITH_CONTENT} : if the file is a directory and not empty,
     *   all child files is deleted. In such case but without this option the exception is thrown.</li>
     *   <li>{@link FileDeleter.DeleteOption#MAY_NOT_EXIST} : if the file doesn't exist, it doesn't
     *   try to delete it. In such case but without this option the exception is thrown.</li>
     * </ul>
     * */
    public enum DeleteOption {
        WITH_CONTENT, MAY_NOT_EXIST,
    }

    public static FileDeleter of(DeleteOption... options) {
        boolean withContent = false, mayNotExist = false;
        for (DeleteOption option: options) {
            switch (option) {
                case WITH_CONTENT -> withContent = true;
                case MAY_NOT_EXIST -> mayNotExist = true;
            }
        }
        return new FileDeleter(withContent, mayNotExist);
    }

    private final boolean withContent;
    private final boolean mayNotExist;

    private FileDeleter(boolean withContent, boolean mayNotExist) {
        this.withContent = withContent;
        this.mayNotExist = mayNotExist;
    }

    /**
     * The method deletes a file by the gotten path according to gotten options throw the
     * {@link FileCreator#of(FileCreator.CreateOption...)}. The method uses the methods of class {@link Files}.
     * @param path the path to the file to delete
     * @see Files#delete(Path)
     * @see Files#list(Path)
     * */
    public void delete(Path path) {
        if (Files.notExists(path)) {
            if (mayNotExist)
                return;
            else
                throw new IllegalArgumentException("Such file doesn't exist: " + path);
        }

        try {
            if (Files.isDirectory(path)) {
                if (isEmpty(path)) {
                    Files.delete(path);
                } else {
                    if (withContent)
                        deleteCompletely(path);
                    else
                        throw new IllegalArgumentException("Directory is not empty: " + path);
                }
            } else {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void deleteCompletely(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                List<Path> children = stream.toList();
                for (Path child: children) {
                    deleteCompletely(child);
                }
            }
        }
        Files.delete(path);
    }

    private boolean isEmpty(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.findAny().isEmpty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
