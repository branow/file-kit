package com.branow.file.kit.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * An abstraction of any computer file.
 * The class is useful to work with only existing file paths.
 * Object of FileEntity can be converted to a {@link File} or {@link Path} instance.
 * */
public class FileEntity {

    private File file;

    /**
     * @throws IllegalArgumentException if the file doesn't exist
     * or file is a directory
     * */
    public FileEntity(File file) {
        validation(file.toPath());
        this.file = file;
    }

    /**
     * @throws IllegalArgumentException if the file doesn't exist
     * or file is a directory
     * */
    public FileEntity(String path) {
        this(new File(path));
    }

    /**
     * @throws IllegalArgumentException if the file doesn't exist
     * or file is a directory
     * */
    public FileEntity(Path path) {
        this(path.toFile());
    }

    /**
     * The method rename this file to gotten filename.
     * The method calls the {@code move()} method of class {@link Files}.
     * @param newName new name of the file without extension
     * @throws RuntimeIOException if IOException is thrown;
     * @see FileEntity#rename(String, String)
     * */
    public void rename(String newName) {
        rename(newName, extension());
    }

    /**
     * The method rename this file to gotten filename.
     * The method calls the {@code move()} method of class {@link Files}.
     * @param newName new name of the file
     * @param newExtension new extension of the file
     * @throws RuntimeIOException if IOException is thrown;
     * @throws NullPointerException if {@code newName} or {@code newExtension} is null;
     * @throws IllegalArgumentException if {@code newExtension} is empty or
     * {@code newName} and {@code newExtension} are the same as current (ignore case);
     * @see Files#move(Path, Path, CopyOption...)
     * */
    public void rename(String newName, String newExtension) {
        if (newName == null) throw new NullPointerException("new name is null");
        if (newExtension == null) throw new NullPointerException("new extension is null");
        if (newExtension.isEmpty()) throw new IllegalArgumentException("new extension is empty");

        Path src = path();
        String newFullName = newName + "." + newExtension;
        String currentName = src.getFileName().toString();

        if (currentName.equalsIgnoreCase(newFullName))
            throw new IllegalArgumentException("new name is the same as current: current - " + currentName + ", new - " + newFullName);

        String targetDir = src.getParent().toString();
        Path path = Path.of(targetDir, newFullName);
        try {
            Files.move(src, path);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        file = path.toFile();
    }


    /**
     * The method return the size of this file (in bytes).
     * The method calls the {@code size()} method of class {@link Files}.
     * @throws RuntimeIOException if IOException is thrown;
     * @see Files#size(Path)
     * */
    public long size() {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * The method return the size of this file (in bytes).
     * The method calls the {@code size()} method of class {@link Files}.
     * @throws RuntimeIOException if IOException is thrown;
     * @see FileEntity#size()
     * */
    public long size(VolumeUnit unit) {
        return VolumeUnit.convert(size(), VolumeUnit.BYTE, unit);
    }

    /**
     * The method returns file's extension string
     * @return file's extension string (for example txt, pdf, exe, xml, jpg).
     * Return empty string if the file doesn't have extension.
     * */
    public String extension() {
        String filename = file.getName();
        int dot = filename.lastIndexOf('.');
        if (dot == -1)
            return null;
        if (dot + 1 == filename.length())
            return "";
        return filename.substring(dot + 1);
    }

    /**
     * The method checks if size of the file is zero.
     * @return {@code true} - file size is zero,
     * {@code false} - isn't
     * */
    public boolean isZero() {
        return size() == 0;
    }

    /**
     * The method returns {@link File} instance of this file
     * @return {@link File} class instance
     * */
    public File file() {
        return file;
    }

    /**
     * The method returns {@link Path} instance of this file
     * @return {@link Path} class instance
     * */
    public Path path() {
        return file.toPath();
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    private void validation(Path path) {
        if (Files.notExists(path)) {
            throw new IllegalArgumentException("The file doesn't exist - "  + path);
        } else if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("The file is a directory - "  + path);
        }
    }
}
