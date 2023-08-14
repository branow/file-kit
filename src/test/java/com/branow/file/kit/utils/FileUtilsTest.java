package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileUtilsTest {

    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/utils/file-utils-test/");

    @AfterEach
    public void cleanFolder() {
        try (Stream<Path> stream = Files.list(resourceFolder)) {
            List<Path> children = stream.toList();
            for (Path child : children) {
                delete(child);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopySrcOrTargetDirNotExist")
    public void moveSrcOrTargetDirNotExist(boolean createSrc, boolean createTargetDir) {
        Path src = path("data.txt");
        Path targetDir = path("targetDir");

        if (createSrc)
            create(src, false);
        if (createTargetDir)
            create(targetDir, true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.move(src, targetDir));
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyTargetDirIsNotDirectory")
    public void moveTargetDirIsNotDirectory(Path targetDir) {
        Path src = path("data.txt");
        create(src, false);
        create(targetDir, false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.move(src, targetDir));
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyFileWithThisNameAlreadyExist")
    public void moveFileWithThisNameAlreadyExist(Path src, Path targetDir) {
        Path target = Path.of(targetDir.toString(), src.getFileName().toString());

        boolean directory = isDirectory(src);
        create(src, directory);
        create(target, directory);

        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.move(src, targetDir));
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyFile")
    public void moveFile(Path src, Path targetDir, boolean exchange, int srcSeed) {
        create(src, false);
        create(targetDir, true);
        writeRandom(src, srcSeed);

        Path expectedTarget = Path.of(targetDir.toString(), src.getFileName().toString());

        if (exchange) {
            create(expectedTarget);
            writeRandom(expectedTarget, 1234);
        }

        Path actualTarget = FileUtils.move(src, targetDir, exchange);
        Assertions.assertEquals(expectedTarget, actualTarget);
        Assertions.assertTrue(Files.notExists(src));

        byte[] expected = randomBytes(srcSeed);
        byte[] actual = read(actualTarget);
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyDir")
    public void moveDir(Path src, Path targetDir, boolean exchange, Map<String, Integer> children, List<String> targetChildren) {
        create(src, true);
        create(targetDir, true);

        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path child = Path.of(src.toString(), entry.getKey());
            create(child);
            if (isFile(child)) {
                writeRandom(child, entry.getValue());
            }
        }

        Path expectedTarget = Path.of(targetDir.toString(), src.getFileName().toString());

        if (exchange) {
            create(expectedTarget, true);
            for (String path : targetChildren) {
                Path child = Path.of(expectedTarget.toString(), path);
                create(child);
                if (isFile(child)) {
                    writeRandom(child, 2343);
                }
            }
        }

        Path actualTarget = FileUtils.move(src, targetDir, exchange);
        Assertions.assertEquals(expectedTarget, actualTarget);
        Assertions.assertTrue(Files.notExists(src));

        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path child = Path.of(expectedTarget.toString(), entry.getKey());
            Assertions.assertTrue(Files.exists(child));
            if (isFile(child)) {
                byte[] expected = randomBytes(entry.getValue());
                byte[] actual = read(child);
                Assertions.assertArrayEquals(expected, actual);
            }
        }

        if (exchange) {
            for (String path : targetChildren) {
                Path child = Path.of(expectedTarget.toString(), path);
                Assertions.assertTrue(Files.notExists(child));
            }
        }
    }


    @ParameterizedTest
    @MethodSource("provideRenameSrcNotExistOrNewNameIsEmpty")
    public void renameSrcNotExistOrNewNameIsEmpty(Path src, String newFileName) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.rename(src, newFileName));
    }

    @ParameterizedTest
    @MethodSource("provideRenameNewNameIsTheSameAsCurrent")
    public void renameNewNameIsTheSameAsCurrent(Path src, String newFileName) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.rename(src, newFileName));
    }


    @ParameterizedTest
    @MethodSource("provideRenameFileWithThisNameAlreadyExist")
    public void renameFileWithThisNameAlreadyExist(Path src, String newFileName) {
        Path target = Path.of(src.getParent().toString(), newFileName);

        boolean directory = isDirectory(src);
        create(src, directory);
        create(target, directory);

        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.rename(src, newFileName));
    }

    @ParameterizedTest
    @MethodSource("provideRenameFile")
    public void renameFile(Path src, String newFileName, boolean exchange, int srcSeed) {
        create(src, false);
        writeRandom(src, srcSeed);

        Path expectedTarget = Path.of(src.getParent().toString(), newFileName);

        if (exchange) {
            create(expectedTarget, false);
            writeRandom(expectedTarget, new Random().nextInt());
        }

        Path actualTarget = FileUtils.rename(src, newFileName, exchange);
        Assertions.assertEquals(expectedTarget, actualTarget);
        Assertions.assertTrue(Files.notExists(src));

        byte[] expected = randomBytes(srcSeed);
        byte[] actual = read(actualTarget);
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideRenameDir")
    public void renameDir(Path src, String newFileName, boolean exchange, Map<String, Integer> children, List<String> targetChildren) {
        create(src, true);
        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path child = Path.of(src.toString(), entry.getKey());
            create(child);
            if (isFile(child)) {
                writeRandom(child, entry.getValue());
            }
        }

        Path expectedTarget = Path.of(src.getParent().toString(), newFileName);

        if (exchange) {
            create(expectedTarget, true);
            for (String path : targetChildren) {
                Path child = Path.of(expectedTarget.toString(), path);
                create(child);
                if (isFile(child)) {
                    writeRandom(child, 2343);
                }
            }
        }

        Path actualTarget = FileUtils.rename(src, newFileName, exchange);
        Assertions.assertEquals(expectedTarget, actualTarget);
        Assertions.assertTrue(Files.notExists(src));

        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path child = Path.of(expectedTarget.toString(), entry.getKey());
            Assertions.assertTrue(Files.exists(child));
            if (isFile(child)) {
                byte[] expected = randomBytes(entry.getValue());
                byte[] actual = read(child);
                Assertions.assertArrayEquals(expected, actual);
            }
        }

        if (exchange) {
            for (String path : targetChildren) {
                Path child = Path.of(expectedTarget.toString(), path);
                Assertions.assertTrue(Files.notExists(child));
            }
        }
    }


    @ParameterizedTest
    @MethodSource("provideCopyToTheSameDirFileNotExist")
    public void copyToTheSameDirFileNotExist(Path src) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.copy(src));
    }


    @ParameterizedTest
    @MethodSource("provideCopyToTheSameDirFile")
    public void copyToTheSameDirFile(Path src, List<String> copyFileNames, int srcSeed) {
        create(src, false);
        writeRandom(src, srcSeed);

        for (String copyFileName : copyFileNames) {
            Path expectedCopy = Path.of(src.getParent().toString(), copyFileName);
            Path actualCopy = FileUtils.copy(src);
            Assertions.assertEquals(expectedCopy, actualCopy);
            Assertions.assertTrue(Files.exists(actualCopy));
            Assertions.assertTrue(Files.exists(src));

            byte[] expected = randomBytes(srcSeed);
            byte[] actualByteSrc = read(src);
            byte[] actualByteCopy = read(actualCopy);
            Assertions.assertArrayEquals(expected, actualByteSrc);
            Assertions.assertArrayEquals(expected, actualByteCopy);
        }
    }

    @ParameterizedTest
    @MethodSource("provideCopyToTheSameDirDir")
    public void copyToTheSameDirDir(Path src, List<String> copyFileNames, Map<String, Integer> children) {
        create(src, true);
        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path child = Path.of(src.toString(), entry.getKey());
            create(child);
            if (isFile(child)) {
                writeRandom(child, entry.getValue());
            }
        }

        for (String copyFileName : copyFileNames) {
            Path expectedCopy = Path.of(src.getParent().toString(), copyFileName);
            Path actualCopy = FileUtils.copy(src);
            Assertions.assertEquals(expectedCopy, actualCopy);
            Assertions.assertTrue(Files.exists(actualCopy));
            Assertions.assertTrue(Files.exists(src));

            for (Map.Entry<String, Integer> entry : children.entrySet()) {
                Path childActual = Path.of(actualCopy.toString(), entry.getKey());
                Path childSrc = Path.of(src.toString(), entry.getKey());
                Assertions.assertTrue(Files.exists(childActual));
                if (isFile(childSrc)) {
                    byte[] expected = randomBytes(entry.getValue());
                    byte[] actualByteSrc = read(childSrc);
                    byte[] actualByteCopy = read(childActual);
                    Assertions.assertArrayEquals(expected, actualByteSrc);
                    Assertions.assertArrayEquals(expected, actualByteCopy);
                }
            }
        }
    }


    @ParameterizedTest
    @MethodSource("provideMoveAndCopySrcOrTargetDirNotExist")
    public void copySrcOrTargetDirNotExist(boolean createSrc, boolean createTargetDir) {
        Path src = path("data.txt");
        Path targetDir = path("targetDir");

        if (createSrc)
            create(src, false);
        if (createTargetDir)
            create(targetDir, true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.copy(src, targetDir));
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyTargetDirIsNotDirectory")
    public void copyTargetDirIsNotDirectory(Path targetDir) {
        Path src = path("data.txt");
        create(src, false);
        create(targetDir, false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.move(src, targetDir));
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyFileWithThisNameAlreadyExist")
    public void copyFileWithThisNameAlreadyExist(Path src, Path targetDir) {
        Path target = Path.of(targetDir.toString(), src.getFileName().toString());

        boolean directory = isDirectory(src);
        create(src, directory);
        create(target, directory);

        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.copy(src, targetDir));
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyFile")
    public void copyFile(Path src, Path targetDir, boolean exchange, int srcSeed) {
        create(src, false);
        create(targetDir, true);
        writeRandom(src, srcSeed);

        Path expectedTarget = Path.of(targetDir.toString(), src.getFileName().toString());

        if (exchange) {
            create(expectedTarget);
            writeRandom(expectedTarget, 1234);
        }

        Path actualTarget = FileUtils.copy(src, targetDir, exchange);
        Assertions.assertEquals(expectedTarget, actualTarget);
        Assertions.assertTrue(Files.exists(src));
        Assertions.assertTrue(Files.exists(actualTarget));

        byte[] expected = randomBytes(srcSeed);
        byte[] actual = read(actualTarget);
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideMoveAndCopyDir")
    public void copyDir(Path src, Path targetDir, boolean exchange, Map<String, Integer> children, List<String> targetChildren) {
        create(src, true);
        create(targetDir, true);
        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path child = Path.of(src.toString(), entry.getKey());
            create(child);
            if (isFile(child)) {
                writeRandom(child, entry.getValue());
            }
        }

        Path expectedTarget = Path.of(targetDir.toString(), src.getFileName().toString());

        if (exchange) {
            create(expectedTarget, true);
            for (String path : targetChildren) {
                Path child = Path.of(expectedTarget.toString(), path);
                create(child);
                if (isFile(child)) {
                    writeRandom(child, 2343);
                }
            }
        }

        Path actualTarget = FileUtils.copy(src, targetDir, exchange);
        Assertions.assertEquals(expectedTarget, actualTarget);
        Assertions.assertTrue(Files.exists(src));
        Assertions.assertTrue(Files.exists(actualTarget));

        for (Map.Entry<String, Integer> entry : children.entrySet()) {
            Path childActual = Path.of(actualTarget.toString(), entry.getKey());
            Path childSrc = Path.of(src.toString(), entry.getKey());
            Assertions.assertTrue(Files.exists(childActual));
            Assertions.assertTrue(Files.exists(childSrc));
            if (isFile(childSrc)) {
                byte[] expected = randomBytes(entry.getValue());
                byte[] actualByteSrc = read(childSrc);
                byte[] actualByteCopy = read(childActual);
                Assertions.assertArrayEquals(expected, actualByteSrc);
                Assertions.assertArrayEquals(expected, actualByteCopy);
            }
        }

        if (exchange) {
            for (String path : targetChildren) {
                Path child = Path.of(expectedTarget.toString(), path);
                Assertions.assertTrue(Files.notExists(child));
            }
        }
    }


    @ParameterizedTest
    @MethodSource("provideChildAtDirNotExistOrNotDirectoryOrPositionIsMinus")
    public void childAtDirNotExistOrNotDirectoryOrPositionIsMinus(Path dir, int pos, boolean create) {
        if (create)
            create(dir, isDirectory(dir));
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileUtils.childAt(dir, pos));
    }

    @ParameterizedTest
    @MethodSource("provideChildAt")
    public void childAt(Path dir, List<String> childrenNames) {
        create(dir, true);
        List<Path> descendants = childrenNames.stream().map(e -> Path.of(dir.toString(), e)).toList();
        create(descendants);

        try (Stream<Path> childrenStream = Files.list(dir)) {
            List<Path> children = childrenStream.toList();

            for (int i = 0; i < children.size(); i++) {
                Path expected = children.get(i);
                Optional<Path> actual = FileUtils.childAt(dir, i);
                Assertions.assertTrue(actual.isPresent());
                Assertions.assertEquals(expected, actual.get());
            }

            Assertions.assertTrue(FileUtils.childAt(dir, children.size()).isEmpty());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideIndexOf")
    public void indexOf(List<Path> paths) {
        create(paths);
        for (Path path : paths) {
            try (Stream<Path> childrenStream = Files.list(path.getParent())) {
                List<Path> children = childrenStream.toList();
                int expected = children.indexOf(path);
                int actual = FileUtils.indexOf(path);
                Assertions.assertEquals(expected, actual);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @ParameterizedTest
    @MethodSource("provideGetFileNameWithoutExtension")
    public void getFileNameWithoutExtension(Path path, String expected) {
        String actual = FileUtils.getFileNameWithoutExtension(path);
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideGoInBreadth")
    public void goInBreadth(String root, List<String> leaves, List<String> expected) {
        Path rootPath = path(root);
        List<Path> leavesPaths = leaves.stream().map(FileUtilsTest::path).toList();
        create(leavesPaths);

        List<Path> expectedPaths = expected.stream().map(FileUtilsTest::path).toList();
        List<Path> actual = FileUtils.goInBreadth(rootPath);
        Assertions.assertEquals(expectedPaths, actual);
    }


    @ParameterizedTest
    @MethodSource("provideGoInBreadth")
    public void goInBreadthStream(String root, List<String> leaves, List<String> expected) {
        Path rootPath = path(root);
        List<Path> leavesPaths = leaves.stream().map(FileUtilsTest::path).toList();
        create(leavesPaths);

        List<Path> expectedPaths = expected.stream().map(FileUtilsTest::path).toList();
        List<Path> actual = FileUtils.goInBreadthStream(rootPath).toList();
        Assertions.assertEquals(expectedPaths, actual);
    }


    private static Stream<Arguments> provideMoveAndCopySrcOrTargetDirNotExist() {
        return Stream.of(
                Arguments.of(false, false),
                Arguments.of(false, true),
                Arguments.of(true, false)
        );
    }

    private static Stream<Arguments> provideMoveAndCopyTargetDirIsNotDirectory() {
        return Stream.of(
                Arguments.of(path("db.txt")),
                Arguments.of(path("folder")),
                Arguments.of(path("bin/db.txt")),
                Arguments.of(path("db/bin/cd/folder"))
        );
    }

    private static Stream<Arguments> provideMoveAndCopyFileWithThisNameAlreadyExist() {
        return Stream.of(
                Arguments.of(path("db.txt"), path("folder")),
                Arguments.of(path("db.txt"), path("folder/folder_2/folder_3")),
                Arguments.of(path("folder"), path("folder_2")),
                Arguments.of(path("folder/folder"), path("folder_2/folder_3/dir"))
        );
    }

    private static Stream<Arguments> provideMoveAndCopyFile() {
        return Stream.of(
                Arguments.of(path("data.txt"), path("folder"), false, 0),
                Arguments.of(path("folder/.git"), path("folder/fol/fol"), false, 15412),
                Arguments.of(path("data.txt"), path("folder"), true, 0),
                Arguments.of(path("folder/.git"), path("folder/fol/fol"), true, 15412)
        );
    }

    private static Stream<Arguments> provideMoveAndCopyDir() {
        return Stream.of(
                Arguments.of(path("folder"),
                        path("dir"),
                        false,
                        Map.of(),
                        List.of()),
                Arguments.of(path("fol/folder"),
                        path("folder/fol"),
                        false,
                        Map.of(),
                        List.of()),
                Arguments.of(path("out/cd"),
                        path("mysql"),
                        false,
                        Map.of("java.txt", 1246, "dir/body.bin", 6543, "folder/folder", 0),
                        List.of()),
                Arguments.of(path("folder"),
                        path("dir"),
                        true,
                        Map.of(),
                        List.of()),
                Arguments.of(path("fol/folder"),
                        path("folder/fol"),
                        true,
                        Map.of(),
                        List.of("bd.txt", "cd/cd/cd/data.txt", "folder")),
                Arguments.of(path("out/cd"),
                        path("mysql"),
                        true,
                        Map.of("java.txt", 1246, "dir/body.bin", 6543, "folder/folder", 0),
                        List.of("bd.txt", "cd_b/cd/cd/data.txt", "dir_b"))
        );
    }


    private static Stream<Arguments> provideRenameSrcNotExistOrNewNameIsEmpty() {
        return Stream.of(
                Arguments.of(path("data.txt"), "dt.txt"),
                Arguments.of(path("folder/dir/data.txt"), "dt.txt"),
                Arguments.of(path("dir/dir_2/dir_3"), "dir_last"),
                Arguments.of(path("data.txt"), ""),
                Arguments.of(path("folder/dir/data.txt"), ""),
                Arguments.of(path("dir/dir_2/dir_3"), "")
        );
    }

    private static Stream<Arguments> provideRenameNewNameIsTheSameAsCurrent() {
        return Stream.of(
                Arguments.of(path("data.txt"), "DATA.txt"),
                Arguments.of(path("data.txt"), "DATA.TXT"),
                Arguments.of(path("Data.txt"), "data.txt"),
                Arguments.of(path("folder/dir/data.txt"), "data.txt"),
                Arguments.of(path("folder/dir/data.txt"), "DATA.TXT"),
                Arguments.of(path("dir/dir_2/dir_3"), "diR_3")
        );
    }

    private static Stream<Arguments> provideRenameFileWithThisNameAlreadyExist() {
        return Stream.of(
                Arguments.of(path("data.txt"), "dt.txt"),
                Arguments.of(path("folder/dir/data.txt"), "dt.txt"),
                Arguments.of(path("dir/dir_2/dir_3"), "dir_last")
        );
    }

    private static Stream<Arguments> provideRenameFile() {
        return Stream.of(
                Arguments.of(path("data.txt"), "data_2.txt", false, 0),
                Arguments.of(path("folder/.git"), "file.git", false, 15412),
                Arguments.of(path("data.txt"), "data_2.txt", true, 0),
                Arguments.of(path("folder/.git"), "file.git", true, 15412)
        );
    }

    private static Stream<Arguments> provideRenameDir() {
        return Stream.of(
                Arguments.of(path("folder"), "dir", false, Map.of(), List.of()),
                Arguments.of(path("fol/folder"), "fol", false, Map.of(), List.of()),
                Arguments.of(path("out/cd"), "mysql", false,
                        Map.of("java.txt", 1246, "dir/body.bin", 6543, "folder/folder", 0),
                        List.of()),
                Arguments.of(path("folder"), "dir", true, Map.of(), List.of()),
                Arguments.of(path("fol/folder"), "fol", true, Map.of(),
                        List.of("bd.txt", "cd/cd/cd/data.txt", "folder")),
                Arguments.of(path("out/cd"), "mysql", true,
                        Map.of("java.txt", 1246, "dir/body.bin", 6543, "folder/folder", 0),
                        List.of("bd.txt", "cd_b/cd/cd/data.txt", "dir_b"))
        );
    }


    private static Stream<Arguments> provideCopyToTheSameDirFileNotExist() {
        return Stream.of(
                Arguments.of(path("data.txt")),
                Arguments.of(path("folder/dir/data.txt")),
                Arguments.of(path("dir/dir_2/dir_3"))
        );
    }

    private static Stream<Arguments> provideCopyToTheSameDirFile() {
        return Stream.of(
                Arguments.of(path("data.txt"), List.of("data-copy.txt"), 0),
                Arguments.of(path("folder/.git"), List.of("-copy.git"), 15412),
                Arguments.of(path("data.txt"),
                        List.of("data-copy.txt", "data-copy-2.txt", "data-copy-3.txt"),
                        0),
                Arguments.of(path("folder/.git"),
                        List.of("-copy.git", "-copy-2.git", "-copy-3.git", "-copy-4.git", "-copy-5.git"),
                        15412)
        );
    }

    private static Stream<Arguments> provideCopyToTheSameDirDir() {
        return Stream.of(
                Arguments.of(
                        path("folder"),
                        List.of("folder-copy"),
                        Map.of()),
                Arguments.of(
                        path("fol/folder"),
                        List.of("folder-copy"),
                        Map.of()),
                Arguments.of(
                        path("out/cd"),
                        List.of("cd-copy"),
                        Map.of("java.txt", 1246, "dir/body.bin", 6543, "folder/folder", 0)),
                Arguments.of(
                        path("folder"),
                        List.of("folder-copy", "folder-copy-2"),
                        Map.of()),
                Arguments.of(
                        path("fol/folder"),
                        List.of("folder-copy", "folder-copy-2", "folder-copy-3"),
                        Map.of()),
                Arguments.of(
                        path("out/cd"),
                        List.of("cd-copy", "cd-copy-2", "cd-copy-3", "cd-copy-4", "cd-copy-5"),
                        Map.of("java.txt", 1246, "dir/body.bin", 6543, "folder/folder", 0))
        );
    }


    private static Stream<Arguments> provideChildAtDirNotExistOrNotDirectoryOrPositionIsMinus() {
        return Stream.of(
                Arguments.of(path("data.txt"), 0, false),
                Arguments.of(path("data.txt"), 0, true),
                Arguments.of(path("folder"), 0, false),
                Arguments.of(path("folder"), -1, true),
                Arguments.of(path("folder/cd/dir"), -5, true)
        );
    }

    private static Stream<Arguments> provideChildAt() {
        return Stream.of(
                Arguments.of(path("folder"), List.of()),
                Arguments.of(path("folder"), List.of("data.txt", "dir")),
                Arguments.of(path("fol/folder"), List.of("data.txt", ".git", "dir")),
                Arguments.of(path("fol/cd/fol"), List.of("cd/data.txt", "cd/.git", "cd/dir")),
                Arguments.of(path("fol/cd/fol"), List.of("data.txt", ".git", "cd/cd/dir", "java/com", "d"))
        );
    }

    private static Stream<Arguments> provideIndexOf() {
        return Stream.of(
                Arguments.of(List.of(path("folder"))),
                Arguments.of(List.of(path("folder"), path("data.txt"), path("dir"))),
                Arguments.of(List.of(path("fol/folder"), path("data.txt"), path(".git"), path("dir"))),
                Arguments.of(List.of(path("fol/cd/fol"), path("cd/data.txt"), path("cd/.git"), path("cd/dir"))),
                Arguments.of(List.of(path("fol/cd/fol"), path("data.txt"), path(".git"), path("cd/cd/dir"), path("d")))
        );
    }


    private static Stream<Arguments> provideGetFileNameWithoutExtension() {
        return Stream.of(
                Arguments.of(path("folder"), "folder"),
                Arguments.of(path("data-2.txt"), "data-2"),
                Arguments.of(path(".gitignore"), ""),
                Arguments.of(path("1234.jpg"), "1234"),
                Arguments.of(path("Hello World!!!. (3).class"), "Hello World!!!. (3)")
        );
    }

    private static Stream<Arguments> provideGoInBreadth() {
        return Stream.of(
                Arguments.of(
                        "root.txt",
                        List.of("root.txt"),
                        List.of("root.txt")),
                Arguments.of(
                        "root",
                        List.of("root"),
                        List.of("root")),
                Arguments.of(
                        "root",
                        List.of("root/dir-1/dir-2/leave-3.png"),
                        List.of("root",
                                "root/dir-1",
                                "root/dir-1/dir-2",
                                "root/dir-1/dir-2/leave-3.png")),
                Arguments.of(
                        "root",
                        List.of("root/dir-1/dir-2/leave-3.bin",
                                "root/dir-1/dir-2/leave-3.jpg",
                                "root/leave-1.java",
                                "root/fol-1/fol-2/fol-3/fol-4",
                                "root/fol-1/fol-2/fol-3/leave-4.bin",
                                "root/fol-1/fol-2/fol-3/leave-4.txt",
                                "root/cd-1/leave-2.bin",
                                "root/cd-1/leave-2.class"),
                        List.of("root",
                                "root/cd-1",
                                "root/dir-1",
                                "root/fol-1",
                                "root/leave-1.java",
                                "root/cd-1/leave-2.bin",
                                "root/cd-1/leave-2.class",
                                "root/dir-1/dir-2",
                                "root/fol-1/fol-2",
                                "root/dir-1/dir-2/leave-3.bin",
                                "root/dir-1/dir-2/leave-3.jpg",
                                "root/fol-1/fol-2/fol-3",
                                "root/fol-1/fol-2/fol-3/fol-4",
                                "root/fol-1/fol-2/fol-3/leave-4.bin",
                                "root/fol-1/fol-2/fol-3/leave-4.txt"))
        );
    }


    private static void create(Collection<Path> paths) {
        paths.forEach(e -> create(e, isDirectory(e)));
    }

    private static void create(Path path) {
        create(path, isDirectory(path));
    }

    private static void create(Path path, boolean directory) {
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
                for (Path child : children) {
                    deleteCompletely(child);
                }
            }
        }
        Files.delete(path);
    }

    private static Path path(String relativePath) {
        return Path.of(resourceFolder.toString(), relativePath);
    }

    private static boolean isFile(Path path) {
        return path.getFileName().toString().contains(".");
    }

    private static boolean isDirectory(Path path) {
        return !isFile(path);
    }


    private static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(Path path, byte[] bytes) {
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeRandom(Path path, int seed) {
        write(path, randomBytes(seed));
    }

    private static byte[] randomBytes(int seed) {
        Random random = new Random(seed);
        return new byte[random.nextInt(0, 1024 * 1024)];
    }

}
