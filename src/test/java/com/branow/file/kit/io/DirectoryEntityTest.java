package com.branow.file.kit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;


public class DirectoryEntityTest {

    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/io/directory-entity-test/");


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

    @Test
    public void constructorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new DirectoryEntity((String) null));
    }

    @ParameterizedTest
    @MethodSource("provideConstructorFileNotExist")
    public void constructorFileNotExist(String dir) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DirectoryEntity(dir));
    }

    @ParameterizedTest
    @MethodSource("provideConstructorFileIsNotDirectory")
    public void constructorFileIsNotDirectory(String dir) {
        createFileIfNotExists(dir);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DirectoryEntity(dir));
    }

    @ParameterizedTest
    @MethodSource("provideConstructor")
    public void constructor(String dir) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);
        Assertions.assertEquals(de.toString(), dir);
        Assertions.assertEquals(de.path(), Path.of(dir));
        Assertions.assertEquals(de.file(), new File(dir));
    }



    @ParameterizedTest
    @MethodSource("provideSize")
    public void size(String dir, Map<String, Long> files, VolumeUnit unit) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);

        long sizeBytes = 0;
        for (Map.Entry<String, Long> file: files.entrySet()) {
            String filepath = dir + File.separator + file.getKey();
            createFileIfNotExists(filepath);

            Random random = new Random();
            byte[] bytes = new byte[file.getValue().intValue()];
            random.nextBytes(bytes);

            try {
                Files.write(Path.of(filepath), bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sizeBytes += file.getValue();
        }

        long expected = VolumeUnit.convert(sizeBytes, VolumeUnit.BYTE, unit);
        long actual = de.size(unit);
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideIsEmpty")
    public void isEmpty(String dir, List<String> filenames) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);

        List<Path> files = filenames.stream().map(e -> Path.of(dir, e)).toList();
        for (Path file: files) {
            createFileIfNotExists(file.toString());
        }
        Assertions.assertFalse(de.isEmpty());

        for (Path file: files) {
            try {
                delete(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Assertions.assertTrue(de.isEmpty());
    }


    @ParameterizedTest
    @MethodSource("provideChildren")
    public void children(String dir, List<String> filenames) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);

        List<Path> files = filenames.stream().map(e -> Path.of(dir, e)).toList();
        for (Path file: files) {
            createFileIfNotExists(file.toString());
        }

        Set<Path> expected = Set.copyOf(files);
        Set<Path> actual = Set.copyOf(de.children());
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideChildren")
    public void childEntities(String dir, List<String> filenames) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);

        List<Path> files = filenames.stream().map(e -> Path.of(dir, e)).toList();
        for (Path file: files) {
            createFileIfNotExists(file.toString());
        }

        Set<SystemEntity> expected = Set.copyOf(wrap(files));
        Set<SystemEntity> actual = Set.copyOf(de.childEntities());
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideChildren")
    public void childFiles(String dir, List<String> filenames) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);

        List<File> files = filenames.stream().map(e -> new File(dir + File.separator + e)).toList();
        for (File file: files) {
            createFileIfNotExists(file.toString());
        }

        Set<File> expected = Set.copyOf(files);
        Set<File> actual = Set.copyOf(de.childFiles());
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideRenameNameAlreadyExists")
    public void renameNameAlreadyExists(String dir, String newName) {
        createDirectoryIfNotExists(dir);
        createDirectoryIfNotExists(resourceFolder + File.separator + newName);
        DirectoryEntity de = new DirectoryEntity(dir);
        Assertions.assertThrows(RuntimeIOException.class, () -> de.rename(newName));
    }

    @ParameterizedTest
    @MethodSource("provideRenameNewNameIsTheSameAsCurrent")
    public void renameNewNameIsTheSameAsCurrent(String dir, String newName) {
        createDirectoryIfNotExists(dir);
        DirectoryEntity de = new DirectoryEntity(dir);
        Assertions.assertThrows(IllegalArgumentException.class, () -> de.rename(newName));
    }

    @ParameterizedTest
    @MethodSource("provideRename")
    public void rename(String dir, String newName) {
        createDirectoryIfNotExists(dir);
        Path src = Path.of(dir);
        Path target = Path.of(src.getParent().toString(), newName);

        DirectoryEntity de = new DirectoryEntity(src);
        de.rename(newName);

        Assertions.assertTrue(Files.notExists(src));
        Assertions.assertTrue(Files.exists(target));
        Assertions.assertEquals(target, de.path());
    }


    private static Stream<Arguments> provideConstructorFileNotExist() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "doc"),
                Arguments.of(resourceFolder + File.separator + "data_bull_placed"),
                Arguments.of(resourceFolder + File.separator + "bin/git/cd"),
                Arguments.of(resourceFolder + File.separator + "Hello World"),
                Arguments.of(resourceFolder + File.separator + " ")
        );
    }

    private static Stream<Arguments> provideConstructorFileIsNotDirectory() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "doc.txt"),
                Arguments.of(resourceFolder + File.separator + ".gitignore"),
                Arguments.of(resourceFolder + File.separator + "file-1234.")
        );
    }

    private static Stream<Arguments> provideConstructor() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "doc"),
                Arguments.of(resourceFolder + File.separator + "data_bull_placed"),
                Arguments.of(resourceFolder + File.separator + "Hello World")
        );
    }

    private static Stream<Arguments> provideSize() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "doc", Map.of(), VolumeUnit.BIT),
                Arguments.of(resourceFolder + File.separator + "folder", Map.of(
                        "file-1.jpg", 0L,
                        "file-2.bin", 25L
                ), VolumeUnit.BYTE),
                Arguments.of(resourceFolder + File.separator + "dir", Map.of(
                        "file-1.jpg", 0L,
                        "file-2.bin", 0L
                ), VolumeUnit.KILOBYTE),
                Arguments.of(resourceFolder + File.separator + "dir-2", Map.of(
                        "file-1.jpg", 3L * 1028 * 1028
                ), VolumeUnit.MEGABYTE),
                Arguments.of(resourceFolder + File.separator + "dir-3", Map.of(
                        "file-1.jpg", 0L,
                        "file-2.bin", 25L * 1028 * 1028,
                        "file-3.txt", 150L * 1028 * 1028,
                        "file-4.xxx", 2L * 1028 * 1028,
                        "file-5.exe", 5L * 1028 * 1028,
                        "file-6.jpg", 50L * 1028 * 1028
                ), VolumeUnit.GIGABYTE)
        );
    }

    private static Stream<Arguments> provideIsEmpty() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "folder", List.of("file.txt")),
                Arguments.of(resourceFolder + File.separator + "folder", List.of(".png", "did.", "empty.doc"))
        );
    }

    private static Stream<Arguments> provideChildren() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "folder", List.of()),
                Arguments.of(resourceFolder + File.separator + "folder", List.of("file.txt")),
                Arguments.of(resourceFolder + File.separator + "folder", List.of(".png", "did.4", "empty.doc"))
        );
    }

    private static Stream<Arguments> provideRenameNameAlreadyExists() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "folder", "folder2"),
                Arguments.of(resourceFolder + File.separator + "folder", "folder--"),
                Arguments.of(resourceFolder + File.separator + "folder", "img.jpg")
        );
    }

    private static Stream<Arguments> provideRenameNewNameIsTheSameAsCurrent() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "folder", "FOLDER"),
                Arguments.of(resourceFolder + File.separator + "folder", "FOLDer"),
                Arguments.of(resourceFolder + File.separator + "folder", "folder")
        );
    }

    private static Stream<Arguments> provideRename() {
        return Stream.of(
                Arguments.of(resourceFolder + File.separator + "folder", "folder2"),
                Arguments.of(resourceFolder + File.separator + "folder", "folder--"),
                Arguments.of(resourceFolder + File.separator + "folder", "img.jpg"),
                Arguments.of(resourceFolder + File.separator + "folder", "Java World_")
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

    private static void delete(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                List<Path> children = stream.toList();
                for (Path child: children) {
                    Files.delete(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    private static List<SystemEntity> wrap(List<Path> paths) {
        return paths.stream().map(e -> {
            if (Files.isDirectory(e))
                return new DirectoryEntity(e);
            else
                return new FileEntity(e);
        }).toList();
    }
}
