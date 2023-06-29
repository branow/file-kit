package com.branow.file.kit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class FileEntityTest {


    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/io/file-entity-test/");

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

    @AfterEach
    public void cleanFolder() {
        try (Stream<Path> children = Files.list(resourceFolder)) {
            children.forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void constructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new FileEntity((File) null));
    }

    @Test
    public void constructorFileNotExists() {
        String path = "D:\\epam\\db-web\\RDBS-and-SQL-Essentials\\cinema\\build0808.txt";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileEntity(path));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileEntity(new File(path)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileEntity(Path.of(path)));
    }

    @ParameterizedTest
    @MethodSource("provideConstructorFileIsDirectory")
    public void constructorFileIsDirectory(String path) {
        createDirectoryIfNotExists(path);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileEntity(Path.of(path)));
    }



    @ParameterizedTest
    @MethodSource("provideRenameNull")
    public void renameNull(String newName, String newExtension) {
        String path = "src/test/java/resources/com/branow/file/kit/io/file-entity-test/renameNull.txt";
        createFileIfNotExists(path);
        FileEntity file = new FileEntity(path);
        Assertions.assertThrows(NullPointerException.class, () -> file.rename(newName, newExtension));
    }

    @Test
    public void renameExtensionIsEmpty() {
        String path = "src/test/java/resources/com/branow/file/kit/io/file-entity-test/renameExtensionIsEmpty.txt";
        createFileIfNotExists(path);
        FileEntity file = new FileEntity(path);
        Assertions.assertThrows(IllegalArgumentException.class, () -> file.rename("newRenameExtensionIsEmpty", ""));
    }

    @ParameterizedTest
    @MethodSource("provideRenameFileWithSuchNameAlreadyExists")
    public void renameFileWithSuchNameAlreadyExists(String newName, String newExtension) {
        String path = "src/test/java/resources/com/branow/file/kit/io/file-entity-test/renameFileWithSuchNameAlreadyExists.txt";
        String path2 = "src/test/java/resources/com/branow/file/kit/io/file-entity-test/" + newName + "." + newExtension;
        createFileIfNotExists(path);
        createFileIfNotExists(path2);
        FileEntity file = new FileEntity(path);
        Assertions.assertThrows(RuntimeIOException.class, () -> file.rename(newName, newExtension));
    }

    @ParameterizedTest
    @MethodSource("provideSize")
    public void size(String path, long size, VolumeUnit unit) {
        createFileIfNotExists(path);
        long sizeBytes = VolumeUnit.convert(size, unit, VolumeUnit.BYTE);
        Random random = new Random();
        byte[] bytes = new byte[(int) sizeBytes];
        random.nextBytes(bytes);
        try {
            Files.write(Path.of(path), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileEntity file = new FileEntity(path);
        Assertions.assertEquals(size, file.size(unit));
    }

    @ParameterizedTest
    @MethodSource("provideExtension")
    public void extension(String path, String extension) {
        createFileIfNotExists(path);
        FileEntity file = new FileEntity(path);
        Assertions.assertEquals(extension, file.extension());
    }

    @ParameterizedTest
    @MethodSource("provideIsZero")
    public void isZero(String path, boolean isZero) {
        createFileIfNotExists(path);

        if (!isZero) {
            Random random = new Random();
            byte[] bytes = new byte[random.nextInt() % 100];
            random.nextBytes(bytes);
            try {
                Files.write(Path.of(path), bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FileEntity file = new FileEntity(path);
        Assertions.assertEquals(isZero, file.isZero());
    }


    private static Stream<Arguments> provideConstructorFileIsDirectory() {
        return Stream.of(
                Arguments.of("src/test/java/resources/com/branow/file/kit/io/file-entity-test/.folder"),
                Arguments.of("src/test/java/resources/com/branow/file/kit/io/file-entity-test/folder.folder"),
                Arguments.of("src/test/java/resources/com/branow/file/kit/io/file-entity-test/folder_1")
        );
    }

    private static Stream<Arguments> provideRenameNull() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, ".txt"),
                Arguments.of("newFileName", null)
        );
    }

    private static Stream<Arguments> provideRenameFileWithSuchNameAlreadyExists() {
        return Stream.of(
                Arguments.of("renameFileWithSuchNameAlreadyExistsCopy", "txt"),
                Arguments.of("renameFileWithSuchNameAlreadyExists2", "txt")
        );
    }

    private static Stream<Arguments> provideSize() {
        return Stream.of(
                Arguments.of(resourceFolder + "/sizeZero.txt", 0L, VolumeUnit.BYTE),
                Arguments.of(resourceFolder + "/sizeBytes.bin", 15L, VolumeUnit.BYTE),
                Arguments.of(resourceFolder + "/sizeBits.jpg", 1024L, VolumeUnit.BIT),
                Arguments.of(resourceFolder + "/sizeMegabytes.mp3", 2L, VolumeUnit.MEGABYTE)
        );
    }

    private static Stream<Arguments> provideExtension() {
        return Stream.of(
                Arguments.of(resourceFolder + "/extension.txt", "txt"),
                Arguments.of(resourceFolder + "/extension.BIN", "BIN"),
                Arguments.of(resourceFolder + "/.jpg", "jpg"),
                Arguments.of(resourceFolder + "/extension.", "")
        );
    }

    private static Stream<Arguments> provideIsZero() {
        return Stream.of(
                Arguments.of(resourceFolder + "/zero.txt", false),
                Arguments.of(resourceFolder + "/zero.BIN", true),
                Arguments.of(resourceFolder + "/.jpg", true),
                Arguments.of(resourceFolder + "/zero.", false)
        );
    }
}
