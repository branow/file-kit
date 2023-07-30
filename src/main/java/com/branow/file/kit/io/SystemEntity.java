package com.branow.file.kit.io;

import java.io.File;
import java.nio.file.Path;

/**
 * The {@code SystemEntity} is an interface that contains methods common
 * for any file entity (files and directories).
 * */
public interface SystemEntity {

    /**
     * The method rename this file to gotten filename.
     * @param newName new name of the file
     * */
    void rename(String newName);

    /**
     * The method return the size of this file (in bytes).
     * */
    long size();

    /**
     * The method return the size of this file in gotten inits
     * @param unit volume units in which the size is returned
     * */
    long size(VolumeUnit unit);

    /**
     * The method returns {@link File} instance of this file
     * @return {@link File} class instance
     * */
    File file();

    /**
     * The method returns {@link Path} instance of this file
     * @return {@link Path} class instance
     * */
    Path path();

}
