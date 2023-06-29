package com.branow.file.kit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class ByteFileTest {

    private static final Path resourceFolder = Path.of("src/test/java/resources/com/branow/file/kit/io/byte-file-test/");

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


    @ParameterizedTest
    @MethodSource("provideReadBytes")
    public void readBytes(String path, byte[] expect, int off) {
        createFileIfNotExists(path);

        byte[] bytes = new byte[expect.length + off];
        System.arraycopy(expect, 0, bytes, off, expect.length);
        write(path, bytes);

        ByteFile file = new ByteFile(path);
        Assertions.assertArrayEquals(expect, file.readBytes(off, expect.length));
        Assertions.assertArrayEquals(read(path), file.readBytes());

        if (bytes.length > 0) {
            Random random = new Random();
            int len = random.nextInt(bytes.length);
            Assertions.assertArrayEquals(Arrays.copyOf(read(path), len), file.readBytes(len));
        }
    }

    @ParameterizedTest
    @MethodSource("provideWriteBytes")
    public void writeBytes(String path, byte[] write, int off) {
        createFileIfNotExists(path);

        write(path, randomBytes(off + write.length));
        byte[] before = read(path);
        byte[] expect = new byte[before.length];
        System.arraycopy(before, 0, expect, 0, off);
        System.arraycopy(write, 0, expect, off, write.length);

        ByteFile file = new ByteFile(path);
        file.writeBytes(write, off);

        byte[] actual = read(path);
        Assertions.assertArrayEquals(expect, actual);
    }

    @ParameterizedTest
    @MethodSource("provideWriteBytes")
    public void appendBytes(String path, byte[] append, int off) {
        createFileIfNotExists(path);

        write(path, randomBytes(off + append.length));
        byte[] before = read(path);
        byte[] expect = new byte[before.length + append.length];
        System.arraycopy(before, 0, expect, 0, off);
        System.arraycopy(append, 0, expect, off, append.length);
        System.arraycopy(before, off, expect, off + append.length, before.length - off);

        ByteFile file = new ByteFile(path);
        file.appendBytes(append, off);

        byte[] actual = read(path);
        Assertions.assertArrayEquals(expect, actual);
    }

    private static Stream<Arguments> provideReadBytes() {
        return Stream.of(
                Arguments.of("/read_1.txt", new byte[] {}, 0),
                Arguments.of("/read_2.jpg", new byte[] {}, 5),
                Arguments.of("/read_3.mp3", randomBytes(), 0),
                Arguments.of("/read_4.doc", randomBytes(), 4523)
        );
    }

    private static Stream<Arguments> provideWriteBytes() {
        return Stream.of(
                Arguments.of("/write_1.txt", new byte[] {}, 0),
                Arguments.of("/write_2.jpg", new byte[] {}, 5),
                Arguments.of("/write_3.mp3", randomBytes(), 0),
                Arguments.of("/write_4.doc", randomBytes(), 4523)
        );
    }

    private static byte[] randomBytes() {
        Random random = new Random();
        return randomBytes(random.nextInt(1000));
    }

    private static byte[] randomBytes(int size) {
        Random random = new Random();
        byte[] res = new byte[size];
        random.nextBytes(res);
        return res;
    }


    private static void write(String path, byte[] bytes) {
        try {
            Files.write(Path.of(path), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] read(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
