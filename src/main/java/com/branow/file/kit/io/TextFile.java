package com.branow.file.kit.io;

import com.branow.file.kit.utils.FileIOUtils;
import com.branow.outfits.util.UniChar;
import com.branow.outfits.util.UniCharString;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * The {@code UniCharFile} lets to read, write, overwrite and append
 * string to the given file. It is a descendant of class {@link UniCharFile}
 * and all its methods based on methods of super class.
 */
public class TextFile extends UniCharFile {

    /**
     * Calls super constructor {@link UniCharFile#UniCharFile(Path)}.
     *
     * @param path The path of the file. Must be already existed.
     */
    public TextFile(Path path) {
        super(path);
    }

    /**
     * Calls super constructor {@link UniCharFile#UniCharFile(Path, Charset)}.
     *
     * @param path    The path of the file. Must be already existed.
     * @param charset The charset of the file data.
     */
    public TextFile(Path path, Charset charset) {
        super(path, charset);
    }

    /**
     * Reads the whole file and returns string of them.
     * It calls {@link FileIOUtils#readString(Path, Charset)}.
     *
     * @return The string of file data.
     * @see FileIOUtils#readString(Path, Charset)
     */
    public String readString() {
        return FileIOUtils.readString(path(), charset());
    }

    /**
     * Reads a string with the given length from the file and returns it.
     * It uses super method {@link UniCharFile#readUniChars(int)} and converter
     * {@link UniCharString}.
     *
     * @param length The maximum length of the string (number of uni chars to read).
     * @return The read string.
     * @see UniCharFile#readUniChars(int)
     * @see UniCharString
     */
    public String readString(int length) {
        UniChar[] uniChars = readUniChars(length);
        return UniCharString.toString(uniChars, charset());
    }

    /**
     * Reads a string from the file starting from the given position and returns it.
     * It uses super method {@link UniCharFile#readUniChars(long)} and converter
     * {@link UniCharString}.
     *
     * @param off The offset at which it starts reading string (char position).
     * @return The read string.
     * @see UniCharFile#readUniChars(long)
     * @see UniCharString
     */
    public String readString(long off) {
        UniChar[] uniChars = readUniChars(off);
        return UniCharString.toString(uniChars, charset());
    }

    /**
     * Reads a string from the file with the given length started from the given position
     * and returns them. It uses super method {@link UniCharFile#readUniChars(long, int)} and converter
     * {@link UniCharString}.
     *
     * @param off    The offset at which it starts reading string (char position).
     * @param length The maximum length of the string (number of uni chars to read).
     * @return The read string.
     * @see UniCharFile#readUniChars(long, int)
     * @see UniCharString
     */
    public String readString(long off, int length) {
        UniChar[] uniChars = readUniChars(off, length);
        return UniCharString.toString(uniChars, charset());
    }

    /**
     * Overwrites the given string to this file.
     * It calls {@link  FileIOUtils#write(Path, String, Charset)}.
     *
     * @param text The string to overwrite into the file.
     * @see FileIOUtils#write(Path, String, Charset)
     */
    public void overwriteString(String text) {
        FileIOUtils.overwrite(path(), text, charset());
    }

    /**
     * Overwrites the given string to this file skipping the given number of chars.
     * It calls {@link  FileIOUtils#write(Path, String, int, Charset)}.
     *
     * @param text The string to overwrite into the file.
     * @param off  The offset at which it starts overwriting.
     * @see FileIOUtils#write(Path, String, int, Charset)
     */
    public void overwriteString(String text, int off) {
        FileIOUtils.overwrite(path(), text, off, charset());
    }

    /**
     * Writes the given string to this file.
     * It calls {@link  FileIOUtils#write(Path, String, Charset)}.
     *
     * @param text The string to write into the file.
     * @see FileIOUtils#write(Path, String, Charset)
     */
    public void writeString(String text) {
        FileIOUtils.write(path(), text, charset());
    }

    /**
     * Writes the given string to this file skipping the given number of chars.
     * It calls {@link  FileIOUtils#write(Path, String, int, Charset)}.
     *
     * @param text The string to write into the file.
     * @param off  The offset at which it starts writing string.
     * @see FileIOUtils#write(Path, String, int, Charset)
     */
    public void writeString(String text, int off) {
        FileIOUtils.write(path(), text, off, charset());
    }

    /**
     * Appends the given string to this file.
     * It calls {@link  FileIOUtils#append(Path, String, Charset)}.
     *
     * @param text The string to append into the file.
     * @see FileIOUtils#append(Path, String, Charset)
     */
    public void appendString(String text) {
        FileIOUtils.append(path(), text, charset());
    }

    /**
     * Appends the given string to this file skipping the given number of chars.
     * It calls {@link  FileIOUtils#append(Path, String, int, Charset)}.
     *
     * @param text The string to append into the file.
     * @param off  The offset at which it starts appending chars.
     * @see FileIOUtils#append(Path, String, int, Charset)
     */
    public void appendString(String text, int off) {
        FileIOUtils.append(path(), text, off, charset());
    }

    /**
     * Returns string representation of this object.
     *
     * @return The string representation of this object.
     */
    @Override
    public String toString() {
        String s = super.toString();
        return s.replace(s.getClass().getSimpleName(), this.getClass().getSimpleName());
    }
}






