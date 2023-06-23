package com.branow.file.kit.io;

/**
 * An abstraction of possible data volumes.
 * The class is used to prepare appearance of data of file size
 * or other to more readable form.
 * */
public enum VolumeUnit {

    BIT(1), BYTE(8),
    KILOBYTE(1024 * 8),
    MEGABYTE(1024 * 1024 * 8),
    GIGABYTE(1024L * 1024 * 1024 * 8);

    /**
     * The method converts the gotten value to another unit of measurement.
     * Returns the integer value of the result.
     * @param value value that will be converted
     * @param from the volume unit of measurement of gotten value
     * @param to the volume unit of measurement of returned value
     * @return value in other units of measurement
     * */
    public static long convert(long value, VolumeUnit from, VolumeUnit to) {
        return value * to.value / from.value;
    }

    /**
     * The method converts the gotten value to another unit of measurement.
     * Returns the decimal value of the result.
     * @param value value that will be converted
     * @param from the volume unit of measurement of gotten value
     * @param to the volume unit of measurement of returned value
     * @return value in other units of measurement
     * */
    public static double convertDecimal(long value, VolumeUnit from, VolumeUnit to) {
        return ((double) value * to.value) / from.value;
    }

    private final long value;

    VolumeUnit(long value) {
        this.value = value;
    }
}
