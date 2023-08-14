package com.branow.file.kit.utils;

import com.branow.file.kit.utils.FileDeleter.DeleteOption;
import com.branow.file.kit.io.RuntimeIOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileDeleterTest {


    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/utils/file-deleter-test/");


    @AfterEach
    public void cleanFolder() {
        try (Stream<Path> stream = Files.list(resourceFolder)) {
            List<Path> children = stream.toList();
            for (Path child: children) {
                delete(child);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @ParameterizedTest
    @MethodSource("provideDeleteSuchFileNotExist")
    public void deleteSuchFileNotExist(FileDeleter deleter, Path path) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> deleter.delete(path));
    }

    @ParameterizedTest
    @MethodSource("provideDeleteDirectoryIsNotEmpty")
    public void deleteDirectoryIsNotEmpty(Path path, List<Path> children) {
        createDirectoryIfNotExists(path);
        for (Path child: children) {
            if (child.getFileName().toString().contains(".")) {
                createFileIfNotExists(child);
            } else {
                createDirectoryIfNotExists(child);
            }
        }
        FileDeleter deleter = FileDeleter.of();
        Assertions.assertThrows(IllegalArgumentException.class, () -> deleter.delete(path));
    }

    @ParameterizedTest
    @MethodSource("provideDeleteFile")
    public void deleteFile(FileDeleter deleter, Path path, boolean create) {
        if (create)
            createFileIfNotExists(path);

        deleter.delete(path);
        Assertions.assertTrue(Files.notExists(path));
    }

    @ParameterizedTest
    @MethodSource("provideDeleteDirectory")
    public void deleteDirectory(FileDeleter deleter, Path path, List<Path> children, boolean create) {
        if (create) {
            createDirectoryIfNotExists(path);
            for (Path child: children) {
                if (child.getFileName().toString().contains(".")) {
                    createFileIfNotExists(child);
                } else {
                    createDirectoryIfNotExists(child);
                }
            }
        }

        deleter.delete(path);
        Assertions.assertTrue(Files.notExists(path));
    }


    private static Stream<Arguments> provideDeleteSuchFileNotExist() {
        return Stream.of(
                Arguments.of(FileDeleter.of(), path("data.txt")),
                Arguments.of(FileDeleter.of(), path("path/dir/folder")),
                Arguments.of(FileDeleter.of(DeleteOption.WITH_CONTENT), path("git"))
        );
    }

    private static Stream<Arguments> provideDeleteDirectoryIsNotEmpty() {
        return Stream.of(
                Arguments.of(path("data"), List.of(path("data/bin"))),
                Arguments.of(path("path"), List.of(path("path/java.png"), path("path/data.txt"))),
                Arguments.of(path("git"), List.of(path("git/bin"), path("git/bin/data"),
                        path("git/bin/data/cd"), path("git/core")))
        );
    }

    private static Stream<Arguments> provideDeleteFile() {
        return Stream.of(
                Arguments.of(FileDeleter.of(), path("data.txt"), true),
                Arguments.of(FileDeleter.of(), path(".git"), true),
                Arguments.of(FileDeleter.of(DeleteOption.MAY_NOT_EXIST), path("bin.wav"), false)
        );
    }

    private static Stream<Arguments> provideDeleteDirectory() {
        return Stream.of(
                Arguments.of(
                        FileDeleter.of(),
                        path("folder"),
                        List.of(),
                        true),
                Arguments.of(
                        FileDeleter.of(DeleteOption.MAY_NOT_EXIST),
                        path("folder"),
                        List.of(),
                        false),
                Arguments.of(
                        FileDeleter.of(DeleteOption.WITH_CONTENT),
                        path("path"),
                        List.of(path("path/java.png"),
                                path("path/data.txt")),
                        true),
                Arguments.of(
                        FileDeleter.of(DeleteOption.WITH_CONTENT),
                        path("git"),
                        List.of(path("git/bin"),
                                path("git/bin/data"),
                                path("git/bin/data/cd"),
                                path("git/core")),
                        true)
        );
    }



    private static void createFileIfNotExists(Path p) {
        if (Files.exists(p))
            return;
        try {
            Files.createFile(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDirectoryIfNotExists(Path p) {
        if (Files.exists(p))
            return;
        try {
            Files.createDirectory(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void delete(Path path) {
        if (Files.exists(path)) {
            try {
                deleteCompletely(path);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }

    private static void deleteCompletely(Path path) throws IOException {
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

    private static Path path(String relativePath) {
        return Path.of(resourceFolder.toString(), relativePath);
    }

}
