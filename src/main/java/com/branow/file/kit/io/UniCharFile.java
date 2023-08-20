package com.branow.file.kit.io;

import com.branow.file.kit.utils.FileIOUtils;
import com.branow.outfits.util.UniChar;
import com.branow.outfits.util.UniCharString;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@code UniCharFile} lets to read, write, overwrite and append
 * {@link UniChar} arrays to the given file. It uses {@link UniCharFileMapper}
 * for reading operations that make them lighter (it keeps fewer data in memory).
 * All other operations are created on the bases of {@link FileIOUtils} methods
 * that works with strings. <br><br>
 * <p>
 * This class also contains all methods of class {@link ByteFile} as a descendant.
 */
public class UniCharFile extends ByteFile {

    private final Charset charset;

    /**
     * Calls {@link UniCharFile#UniCharFile(Path, Charset)} giving default charset
     * - {@link Charset#defaultCharset()}.
     *
     * @param path The path of the file. Must be already existed.
     */
    public UniCharFile(Path path) {
        this(path, Charset.defaultCharset());
    }

    /**
     * Calls super constructor {@link ByteFile#ByteFile(Path)}.
     *
     * @param path    The path of the file. Must be already existed.
     * @param charset The charset of the file data.
     */
    public UniCharFile(Path path, Charset charset) {
        super(path);
        this.charset = charset;
    }


    /**
     * Reads all {@link UniChar} from the file and returns them.
     * It uses {@link UniCharFileMapper}.
     *
     * @return The uni char array read from the file.
     * @see UniCharFileMapper#next()
     */
    public UniChar[] readUniChars() {
        UniCharFileMapper mapper = new UniCharFileMapper(path(), size(), charset);
        List<UniChar> list = new ArrayList<>();
        while (mapper.hasNext()) {
            list.add(mapper.next());
        }
        return list.toArray(UniChar[]::new);
    }

    /**
     * Reads {@link UniChar} from the file and returns them.
     * It uses {@link UniCharFileMapper}.
     *
     * @param length The maximum number of uni chars to read.
     * @return The uni char array read from the file.
     * @see UniCharFileMapper#next()
     */
    public UniChar[] readUniChars(int length) {
        UniCharFileMapper mapper = new UniCharFileMapper(path(), size(), charset);
        UniChar[] uniChars = new UniChar[length];
        for (int i = 0; i < length && mapper.hasNext(); i++) {
            uniChars[i] = mapper.next();
        }
        return uniChars;
    }

    /**
     * Reads {@link UniChar} from the file and returns them.
     * It uses {@link UniCharFileMapper}.
     *
     * @param off The offset at which it starts reading uni chars (char position).
     * @return The uni char array read from the file.
     * @see UniCharFileMapper#next()
     */
    public UniChar[] readUniChars(long off) {
        UniCharFileMapper mapper = new UniCharFileMapper(path(), size(), charset);
        List<UniChar> list = new ArrayList<>();
        for (int i = 0; mapper.hasNext(); i++) {
            UniChar ch = mapper.next();
            if (i >= off) {
                list.add(ch);
            }
        }
        return list.toArray(UniChar[]::new);
    }

    /**
     * Reads {@link UniChar} from the file and returns them.
     * It uses {@link UniCharFileMapper}.
     *
     * @param off    The offset at which it starts reading uni chars (char position).
     * @param length The maximum number of uni chars to read.
     * @return The uni char array read from the file.
     * @see UniCharFileMapper#next()
     */
    public UniChar[] readUniChars(long off, int length) {
        UniCharFileMapper mapper = new UniCharFileMapper(path(), size(), charset);
        UniChar[] uniChars = new UniChar[length];
        for (long i = 0; i < off + length && mapper.hasNext(); i++) {
            UniChar ch = mapper.next();
            if (i >= off) {
                uniChars[(int) (i - off)] = ch;
            }
        }
        return uniChars;
    }


    /**
     * Writes all the given {@link UniChar} (uni char) to this file.
     * It transforms {@code uniChars} to string representation and calls
     * {@link  FileIOUtils#write(Path, String, Charset)}.
     *
     * @param uniChars The uni char array to write into the file.
     * @see FileIOUtils#write(Path, String, Charset)
     */
    public void writeUniChars(UniChar[] uniChars) {
        FileIOUtils.write(path(), UniCharString.toString(uniChars, charset), charset);
    }

    /**
     * Writes all the given {@link UniChar} (uni char) to this file skipping the given number of uni chars.
     * It transforms {@code uniChars} to string representation and calls
     * {@link  FileIOUtils#write(Path, String, int, Charset)}.
     *
     * @param uniChars The uni char array to write into the file.
     * @param off      The offset at which it starts writing uni chars.
     * @see FileIOUtils#write(Path, String, int, Charset)
     */
    public void writeUniChars(UniChar[] uniChars, int off) {
        FileIOUtils.write(path(), UniCharString.toString(uniChars, charset), off, charset);
    }


    /**
     * Overwrites all the given {@code uniChars} to this file.
     * It transforms {@code uniChars} to string representation and calls
     * {@link  FileIOUtils#overwrite(Path, String, Charset)}.
     *
     * @param uniChars The uni char array to overwrite into the file.
     * @see FileIOUtils#write(Path, String, Charset)
     */
    public void overwriteUniChars(UniChar[] uniChars) {
        FileIOUtils.overwrite(path(), UniCharString.toString(uniChars, charset), charset);
    }

    /**
     * Overwrites all the given {@code uniChars} to this file skipping the given number of uni chars.
     * It transforms {@code uniChars} to string representation and calls
     * {@link  FileIOUtils#write(Path, String, int, Charset)}.
     *
     * @param uniChars The uni char array to overwrite into the file.
     * @param off      The offset at which it starts overwriting uni chars.
     * @see FileIOUtils#write(Path, String, int, Charset)
     */
    public void overwriteUniChars(UniChar[] uniChars, int off) {
        FileIOUtils.overwrite(path(), UniCharString.toString(uniChars, charset), off, charset);
    }


    /**
     * Appends all the given {@code uniChars} to this file.
     * It transforms {@code uniChars} to string representation and calls
     * {@link  FileIOUtils#append(Path, String, Charset)}.
     *
     * @param uniChars The uni char array to append into the file.
     * @see FileIOUtils#write(Path, String, Charset)
     */
    public void appendUniChars(UniChar[] uniChars) {
        FileIOUtils.append(path(), UniCharString.toString(uniChars, charset), charset);
    }

    /**
     * Appends all the given {@code uniChars} to this file skipping the given number of uni chars.
     * It transforms {@code uniChars} to string representation and calls
     * {@link  FileIOUtils#append(Path, String, int, Charset)}.
     *
     * @param uniChars The uni char array to append into the file.
     * @param off      The offset at which it starts appending uni chars.
     * @see FileIOUtils#append(Path, String, int, Charset)
     */
    public void appendUniChars(UniChar[] uniChars, int off) {
        FileIOUtils.append(path(), UniCharString.toString(uniChars, charset), off, charset);
    }


    /**
     * Returns the number of uni chars of this file that equals to length of read string.
     * But it uses {@link UniCharFileMapper} that makes the operation lighter than just
     * reading string from this file and returning its length.
     *
     * @return The number of uni chars of this file.
     */
    public long length() {
        UniCharFileMapper mapper = new UniCharFileMapper(path(), size(), charset);
        long count = 0;
        while (mapper.hasNext()) {
            mapper.next();
            count++;
        }
        return count;
    }

    /**
     * Returns the charset of this file.
     *
     * @return The charset of this file.
     */
    public Charset charset() {
        return charset;
    }


    /**
     * Compares this {@code UniCharFile} to the given object. These objects are equal
     * if {@link ByteFile#equals(Object)} return true and their {@code charsets} are equal.
     *
     * @param o The given object to compare.
     * @return {@code True} if this object equals to the given.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UniCharFile that = (UniCharFile) o;
        return Objects.equals(charset, that.charset);
    }

    /**
     * Calculates a hash code of this object by {@link ByteFile#hashCode()} and {@code charset}.
     *
     * @return The hash code of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), charset);
    }

    /**
     * Returns string representation of this object.
     *
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[ " + charset + " " + path() + " ]";
    }
}
