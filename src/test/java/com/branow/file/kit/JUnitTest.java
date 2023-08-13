package com.branow.file.kit;

import com.branow.file.kit.io.RuntimeIOException;

import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class JUnitTest {

    protected final Path resourceFolder;

    public JUnitTest(Path resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    @AfterEach
    public void cleanFolder() {
        try (Stream<Path> stream = Files.list(resourceFolder)) {
            List<Path> children = stream.toList();
            for (Path child : children) {
                delete(child);git
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected void create(Path path) {
        create(path, false);
    }

    protected void create(Path path, boolean directory) {
        if (Files.exists(path))
            return;
        try {
            if (Files.notExists(path.getParent()))
                Files.createDirectories(path.getParent());
            if (directory)
                Files.createDirectory(path);
            else
                Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void delete(Path path) {
        if (Files.exists(path)) {
            try {
                deleteCompletely(path);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }

    protected Path path(String relativePath) {
        return Path.of(resourceFolder.toString(), relativePath);
    }

    protected byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(Path path, byte[] bytes) {
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteCompletely(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                List<Path> children = stream.toList();
                for (Path child : children) {
                    deleteCompletely(child);
                }
            }
        }
        Files.delete(path);
    }
}
