package com.branow.file.kit.io;

import com.branow.file.kit.utils.FileIOUtils;
import com.branow.outfits.util.UniChar;
import com.branow.outfits.util.UniCharStreamMapper;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The {@code UniCharFileMapper} lets to read bytes from file and convert them
 * to {@link UniChar} objects gradually. This class extends class {@link UniCharStreamMapper}
 * that allows read bytes from the file in parts (16384 bytes). Such approach
 * let not to keep big amount of data in the operative memory in one moment.
 */
public class UniCharFileMapper extends UniCharStreamMapper {

    private final Path path;

    /**
     * @param path    The path of the file to read.
     * @param size    The size (in bytes) of the file to read.
     * @param charset The charset to convert bytes to {@link UniChar} objects.
     * @see UniCharStreamMapper#UniCharStreamMapper(long, Charset)
     */
    public UniCharFileMapper(Path path, long size, Charset charset) {
        super(size, charset);
        this.path = path;
    }

    /**
     * Returns the path to file of this mapper.
     *
     * @return The path to file of this mapper.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Reads the given number of bytes starting from the given position from the file.
     * It calls {@link FileIOUtils#readByteBuffer(Path, long, int)}.
     *
     * @param pos  The byte starting byte position.
     * @param size The number of bytes to read.
     * @return The byte buffer of the read bytes.
     * @see FileIOUtils#readByteBuffer(Path, long, int)
     */
    @Override
    protected ByteBuffer read(long pos, int size) {
        return FileIOUtils.readByteBuffer(path, pos, size);
    }

    /**
     * Compares this {@code UniCharFileMapper} to the given object. These objects are equal
     * if their {@code paths}, {@code charsets} and {@code sizes} are equal.
     *
     * @param o The given object to compare.
     * @return {@code True} if this object equals to the given.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniCharFileMapper mapper = (UniCharFileMapper) o;
        return Objects.equals(path, mapper.path) &&
                Objects.equals(getCharset(), mapper.getCharset()) &&
                Objects.equals(getSize(), mapper.getSize());
    }

    /**
     * Calculates a hash code of this object by {@code path}, {@code charset} and {@code size}.
     *
     * @return The hash code of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(path, getCharset(), getSize());
    }

    /**
     * Returns string representation of this object.
     *
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "UniCharFileMapper[ " +
                getSize() + " : " +
                getCharset() + " : " +
                path + " ]";
    }
}
