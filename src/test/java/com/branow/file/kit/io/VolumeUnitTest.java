package com.branow.file.kit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class VolumeUnitTest {

    @ParameterizedTest
    @MethodSource("provideConvertTest")
    public void convertTest(long expected, long value, VolumeUnit from, VolumeUnit to) {
        long actual = VolumeUnit.convert(value, from, to);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideConvertDecimalTest")
    public void convertDecimalTest(double expected, long value, VolumeUnit from, VolumeUnit to) {
        double actual = VolumeUnit.convertDecimal(value, from, to);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    private static Stream<Arguments> provideConvertTest() {
        return Stream.of(
                Arguments.of(0, 80, VolumeUnit.BIT, VolumeUnit.KILOBYTE),
                Arguments.of(2400, 300, VolumeUnit.BYTE, VolumeUnit.BIT),
                Arguments.of(16, 16400, VolumeUnit.MEGABYTE, VolumeUnit.GIGABYTE),
                Arguments.of(23, 25_000_000, VolumeUnit.BYTE, VolumeUnit.MEGABYTE),
                Arguments.of(2_048_000_000, 250_000, VolumeUnit.KILOBYTE, VolumeUnit.BIT)
        );
    }

    private static Stream<Arguments> provideConvertDecimalTest() {
        return Stream.of(
                Arguments.of(0.009765, 80, VolumeUnit.BIT, VolumeUnit.KILOBYTE),
                Arguments.of(2400, 300, VolumeUnit.BYTE, VolumeUnit.BIT),
                Arguments.of(16.015625, 16400, VolumeUnit.MEGABYTE, VolumeUnit.GIGABYTE),
                Arguments.of(23.841857, 25_000_000, VolumeUnit.BYTE, VolumeUnit.MEGABYTE),
                Arguments.of(2_048_000_000, 250_000, VolumeUnit.KILOBYTE, VolumeUnit.BIT)
        );
    }
}
