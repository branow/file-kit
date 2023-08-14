package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;
import com.branow.file.kit.utils.FileCreator.CreateOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FileCreatorTest {

    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/utils/file-creator-test/");


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
    @MethodSource("provideCreateSuchFileAlreadyExistException")
    public void createSuchFileAlreadyExistException(Path path, boolean isDirectory) {
        if (isDirectory)
            createDirectoryIfNotExists(path.toString());
        else
            createFileIfNotExists(path.toString());

        FileCreator fc = FileCreator.of();
        Assertions.assertThrows(IllegalArgumentException.class, () -> fc.create(path));
    }

    @ParameterizedTest
    @MethodSource("provideCreateNonexistentParentException")
    public void createNonexistentParentException(Path path, CreateOption... options) {
        FileCreator fc = FileCreator.of(options);
        Assertions.assertThrows(RuntimeIOException.class, () -> fc.create(path));
    }

    @ParameterizedTest
    @MethodSource("provideCreate")
    public void create(FileCreator creator, Path path, boolean create, boolean isDirectory) {
        if (create) {
            if (isDirectory)
                createDirectoryIfNotExists(path.toString());
            else
                createFileIfNotExists(path.toString());
        }

        creator.create(path);
        Assertions.assertTrue(Files.exists(path));
    }


    private static Stream<Arguments> provideCreateSuchFileAlreadyExistException() {
        return Stream.of(
                Arguments.of(path("data.txt"), false),
                Arguments.of(path("folder"), true),
                Arguments.of(path(".git"), false)
        );
    }

    private static Stream<Arguments> provideCreateNonexistentParentException() {
        return Stream.of(
                Arguments.of(path("bin/com/data.txt"), new CreateOption[]{FileCreator.CreateOption.MAY_EXIST}),
                Arguments.of(path("data/folder"), new CreateOption[]{CreateOption.DIRECTORY}),
                Arguments.of(path("src/java/branow/unit/.git"), new CreateOption[]{})
        );
    }

    private static Stream<Arguments> provideCreate() {
        return Stream.of(
                Arguments.of(
                        FileCreator.of(CreateOption.MAY_EXIST),
                        path("data.txt"), true, false),
                Arguments.of(
                        FileCreator.of(CreateOption.MAY_EXIST, CreateOption.DIRECTORY),
                        path("folder"), true, true),
                Arguments.of(
                        FileCreator.of(),
                        path(".git"), false, false),
                Arguments.of(
                        FileCreator.of(CreateOption.DIRECTORY, CreateOption.NONEXISTENT_PARENT),
                        path("bin/com/data/folder"), false, true),
                Arguments.of(
                        FileCreator.of(CreateOption.NONEXISTENT_PARENT),
                        path("src/java/branow/unit/.git"), false, false)
        );
    }



    private static void createFileIfNotExists(String path) {
        Path p = Path.of(path);
        if (Files.exists(p))
            return;
        try {
            Files.createFile(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDirectoryIfNotExists(String path) {
        Path p = Path.of(path);
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
