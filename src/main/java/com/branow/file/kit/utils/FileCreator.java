package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

/**
 * The {@code FileCreator} is a class that lets create a file appropriately to gotten creation options.<br>
 * CreateOptions: {@link CreateOption#DIRECTORY}, {@link CreateOption#NONEXISTENT_PARENT},
 * {@link CreateOption#MAY_EXIST}
 * */
public class FileCreator {

    /**
     * The {@code CreateOption} describes possible file creation options.
     * <ul>
     *   <li>{@link CreateOption#DIRECTORY} : create a directory. If this option isn't added,
     *   it creates a usual file.</li>
     *   <li>{@link CreateOption#NONEXISTENT_PARENT} : create all nonexistent parents of the
     *   file if they are.</li>
     *   <li>{@link CreateOption#MAY_EXIST} : doesn't create new file or throw exception if
     *   the file of gotten path already exists.</li>
     * </ul>
     * */
    public enum CreateOption {
        DIRECTORY, NONEXISTENT_PARENT, MAY_EXIST,
    }

    /**
     * The method create an instance of {@code FileCreator} class according to gotten
     * creation options.
     * @param options are the file creation options according to which the {@code FileCreator}
     * instance is adjusted.
     * @return  an instance of {@code FileCreator} class.
     * @see CreateOption
     * */
    public static FileCreator of(CreateOption... options) {
        boolean isDirectory = false, canHasNonexistentParent = false, canBeExistent = false;
        for (CreateOption option: options) {
            switch (option) {
                case DIRECTORY -> isDirectory = true;
                case NONEXISTENT_PARENT -> canHasNonexistentParent = true;
                case MAY_EXIST -> canBeExistent = true;
            }
        }
        return new FileCreator(isDirectory, canHasNonexistentParent, canBeExistent);
    }


    private final boolean isDirectory;
    private final boolean mayHasNonexistentParent;
    private final boolean mayBeExistent;

    private FileCreator(boolean isDirectory, boolean canHasNonexistentParent, boolean canBeExistent) {
        this.isDirectory = isDirectory;
        this.mayHasNonexistentParent = canHasNonexistentParent;
        this.mayBeExistent = canBeExistent;
    }

    /**
     * The method creates a file by the gotten path according to gotten options throw the
     * {@link FileCreator#of(CreateOption...)}. The method uses the methods of class {@link Files}.
     * @param path the path to the file to create
     * @see Files#createFile(Path, FileAttribute[])
     * @see Files#createDirectory(Path, FileAttribute[])
     * @see Files#createDirectories(Path, FileAttribute[])
     * */
    public void create(Path path) {
        if (Files.exists(path)) {
            if (mayBeExistent)
                return;
            else
                throw new IllegalArgumentException("Such file already exists: " + path);
        }

        try {
            if (mayHasNonexistentParent)
                Files.createDirectories(path.getParent());

            if (isDirectory)
                Files.createDirectory(path);
            else
                Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

}
