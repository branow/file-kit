package com.branow.file.kit.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * An abstraction of any computer directory.
 * The class is useful to work with only existing file paths.
 * Object of DirectoryEntity can be converted to a {@link File} or {@link Path} instance.
 * @see SystemEntity
 * */
public class DirectoryEntity implements SystemEntity {

    private File file;

    /**
     * @throws IllegalArgumentException if the file doesn't exist
     * or file is not a directory
     * */
    public DirectoryEntity(File file) {
        validation(file.toPath());
        this.file = file;
    }

    /**
     * @throws IllegalArgumentException if the file doesn't exist
     * or file is not a directory
     * */
    public DirectoryEntity(String path) {
        this(new File(path));
    }

    /**
     * @throws IllegalArgumentException if the file doesn't exist
     * or file is not a directory
     * */
    public DirectoryEntity(Path path) {
        this(path.toFile());
    }


    /**
     * The method renames this directory to gotten directory name.
     * The method calls the {@code move()} method of class {@link Files}.
     * @param newName new name of the directory
     * @throws RuntimeIOException if IOException is thrown;
     * @throws NullPointerException if {@code newName} is null;
     * @throws IllegalArgumentException if {@code newName} is the same as current
     * @see Files#move(Path, Path, CopyOption...)
     * */
    @Override
    public void rename(String newName) {
        if (newName == null) throw new NullPointerException("new name is null");

        Path src = path();
        String targetDir = src.getParent().toString();
        Path target = Path.of(targetDir, newName);

        if (src.toString().equalsIgnoreCase(target.toString()))
            throw new IllegalArgumentException("new name is the same as current: current - " + src + ", new - " + target);

        try {
            move(src, target);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        file = target.toFile();
    }

    /**
     * The method return the size of all files of this directory (in bytes).
     * The method uses the {@code size()} method of class {@link Files}.
     * @throws RuntimeIOException if IOException is thrown;
     * @see Files#size(Path)
     * */
    @Override
    public long size() {
        try {
            return size(path());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * The method return the size of all files of this directory in gotten units.
     * The method uses the {@code size()} method of class {@link Files}.
     * @param unit volume units in which the size is returned
     * @throws RuntimeIOException if IOException is thrown;
     * @see FileEntity#size()
     * */
    @Override
    public long size(VolumeUnit unit) {
        return VolumeUnit.convert(size(), VolumeUnit.BYTE, unit);
    }

    /**
     * The method checks weather this directory contains any file.
     * @return {@code true} if this directory contains no files.
     * */
    public boolean isEmpty() {
        return children().isEmpty();
    }

    /**
     * The method return list of files of this directory
     * @return list of files of this directory
     * */
    public List<Path> children() {
        return childFiles().stream().map(File::toPath).toList();
    }

    /**
     * The method return list of files of this directory wrapped into SystemEntity.
     * Directories are changed into to {@link DirectoryEntity}, files to {@link FileEntity}.
     * @return list of files of this directory
     * */
    public List<SystemEntity> childEntities() {
        return children().stream().map(e -> {
            if (Files.isDirectory(e))
                return new DirectoryEntity(e);
            else
                return new FileEntity(e);
        }).toList();
    }

    /**
     * The method returns list of files of this directory
     * @return list of files of this directory
     * */
    public List<File> childFiles() {
        String[] files = file.list();
        if (files == null)
            return new ArrayList<>();
        else
            return Arrays.stream(files)
                    .map(f -> new File(file.toString() + File.separator + f)).toList();
    }


    /**
     * The method returns {@link File} instance of this directory
     * @return {@link File} class instance
     * */
    @Override
    public File file() {
        return file;
    }

    /**
     * The method returns {@link Path} instance of this directory
     * @return {@link Path} class instance
     * */
    @Override
    public Path path() {
        return file.toPath();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectoryEntity that = (DirectoryEntity) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }

    @Override
    public String toString() {
        return file.toString();
    }



    private void validation(Path path) {
        if (Files.notExists(path)) {
            throw new IllegalArgumentException("The file doesn't exist - "  + path);
        } else if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("The file is not a directory - "  + path);
        }
    }

    private void move(Path src, Path target) throws IOException {
        Files.createDirectory(target);
        try (Stream<Path> stream = Files.list(src)) {
            List<Path> children = stream.toList();
            if (!children.isEmpty()) {
                for (Path child: children()) {
                    Path childTarget = Path.of(target.toString(), child.getFileName().toString());
                    if (Files.isDirectory(child)) {
                        move(child, target);
                    } else {
                        Files.move(child, childTarget);
                    }
                }
            }
        }
        Files.delete(src);
    }

    private long size(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            long size = 0;
            try (Stream<Path> stream = Files.list(path)) {
                List<Path> children = stream.toList();
                for (Path child: children) {
                    size += size(child);
                }
            }
            return size;
        }
        return Files.size(path);
    }

}
