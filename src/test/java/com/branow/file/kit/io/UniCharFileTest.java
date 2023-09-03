package com.branow.file.kit.io;

import com.branow.file.kit.JUnitTest;
import com.branow.file.kit.utils.FileIOUtils;
import com.branow.outfits.util.UniChar;
import com.branow.outfits.util.UniCharString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class UniCharFileTest extends JUnitTest {

    public UniCharFileTest() {
        super(Path.of("src/test/java/resources/com/branow/file/kit/io/uni-char-file-test"));
    }

    @ParameterizedTest
    @MethodSource("provideTest")
    public void readUniChars(byte[] data, Charset charset) {
        String src = new String(data, charset);

        Path path = path("read.txt");
        create(path);
        UniCharFile charFile = new UniCharFile(path, charset);
        FileIOUtils.write(path, src, charset);

        UniChar[] expected = UniCharString.toUniChars(src, charset);
        UniChar[] actual = charFile.readUniChars();
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideTest")
    public void readUniCharsTo(byte[] data, Charset charset) {
        String src = new String(data, charset);
        Random random = new Random(data.length);
        int length = random.nextInt(1, src.length());

        Path path = path("read.txt");
        create(path);
        UniCharFile charFile = new UniCharFile(path, charset);
        FileIOUtils.write(path, src, charset);

        UniChar[] expected = Arrays.copyOf(UniCharString.toUniChars(src, charset), length);
        UniChar[] actual = charFile.readUniChars(length);
        Assertions.assertArrayEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideTest")
    public void readUniCharsFrom(byte[] data, Charset charset) {
        String src = new String(data, charset);
        Random random = new Random(data.length);
        long off = random.nextLong(1, src.length());

        Path path = path("read.txt");
        create(path);
        UniCharFile charFile = new UniCharFile(path, charset);
        FileIOUtils.write(path, src, charset);
        UniChar[] chars = UniCharString.toUniChars(src, charset);

        UniChar[] expected = Arrays.copyOfRange(chars, (int) off, chars.length);
        UniChar[] actual = charFile.readUniChars(off);
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideTest")
    public void readUniCharsFromTo(byte[] data, Charset charset) {
        String src = new String(data, charset);
        Random random = new Random(data.length);
        int length = random.nextInt(1, src.length());
        long off = random.nextLong(0, src.length() - length);

        Path path = path("read.txt");
        create(path);
        UniCharFile charFile = new UniCharFile(path, charset);
        FileIOUtils.write(path, src, charset);
        UniChar[] chars = UniCharString.toUniChars(src, charset);

        UniChar[] expected = Arrays.copyOfRange(chars, (int) off, (int) off + length);
        UniChar[] actual = charFile.readUniChars(off, length);
        Assertions.assertArrayEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("provideTest")
    public void length(byte[] data, Charset charset) {
        String src = new String(data, charset);

        Path path = path("read.txt");
        create(path);
        UniCharFile charFile = new UniCharFile(path, charset);
        FileIOUtils.write(path, src, charset);

        long expected = UniCharString.toUniChars(src, charset).length;
        long actual = charFile.length();
        Assertions.assertEquals(expected, actual);
    }


    private static Stream<Arguments> provideTest() {
        return Stream.of(
                Arguments.of(random(-143154), StandardCharsets.ISO_8859_1),
                Arguments.of(random(135615), StandardCharsets.US_ASCII),
                Arguments.of(random(-1536411), StandardCharsets.UTF_8),
                Arguments.of(random(1345), StandardCharsets.UTF_16BE),
                Arguments.of(random(-916554), StandardCharsets.UTF_16LE),
                Arguments.of(random(4165), StandardCharsets.UTF_16)
        );
    }

    private static byte[] random(long seed) {
        Random random = new Random(seed);
        int size = random.nextInt(1, 2000);
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return bytes;
    }
}
