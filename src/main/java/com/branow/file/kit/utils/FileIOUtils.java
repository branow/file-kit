package com.branow.file.kit.utils;

import com.branow.file.kit.io.RuntimeIOException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This class contains various static methods for reading, writing and appending
 * bytes or characters to files. The methods work with instances of class Path.
 * There is using class {@link FileChannel} to manipulate with file content.
 * The all trowing exceptions from {@link FileChannel} methods are wrapped
 * to {@link RuntimeIOException}.
 */
public class FileIOUtils {

    /**
     * The method reads string from this file.
     * The method is based on calling {@link FileIOUtils#readString(Path, Charset)}
     *
     * @param path The path fo the file from which the characters are read.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readString(Path, Charset)
     * */
    public static String readString(Path path) {
        return readString(path, Charset.defaultCharset());
    }

    /**
     * The method reads string matching length from this file.
     * The method is based on calling {@link FileIOUtils#readStringTo(Path, int, Charset)}.
     *
     * @param path The path fo the file from which the characters are read.
     * @param size The length of the string.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readStringTo(Path, int, Charset)
     * */
    public static String readStringTo(Path path, int size) {
        return readStringTo(path, size, Charset.defaultCharset());
    }

    /**
     * The method reads string from this file, starting from the file position.
     * The method is based on calling {@link FileIOUtils#readStringFrom(Path, int, Charset)}.
     *
     * @param path The path fo the file from which the characters are read.
     * @param pos The file position at which the reading is to begin. It is equal to an index of a character.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readStringFrom(Path, int, Charset)
     * */
    public static String readStringFrom(Path path, int pos) {
        return readStringFrom(path, pos, Charset.defaultCharset());
    }

    /**
     * The method reads string matching length from this file, starting from the file position.
     * The method is based on calling {@link FileIOUtils#readStringFromTo(Path, int, int, Charset)}
     *
     * @param path The path fo the file from which the characters are read.
     * @param pos The file position at which the reading is to begin. It is equal to an index of a character.
     * @param size The length of the string.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readStringFromTo(Path, int, int, Charset)
     * */
    public static String readStringFromTo(Path path, int pos, int size) {
        return readStringFromTo(path, pos, size, Charset.defaultCharset());
    }

    /**
     * The method reads string from this file.
     * The method is based on calling {@link FileIOUtils#readByteBuffer(Path)}}
     *
     * @param path The path fo the file from which the characters are read.
     * @param charset The charset to transform bytes to characters.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readByteBuffer(Path)
     * */
    public static String readString(Path path, Charset charset) {
        return charset.decode(readByteBuffer(path)).toString();
    }

    /**
     * The method reads string matching length from this file.
     * The method is based on calling {@link FileIOUtils#readString(Path, Charset)}.
     *
     * @param path The path fo the file from which the characters are read.
     * @param size The length of the string.
     * @param charset The charset to transform bytes to characters.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readString(Path, Charset)
     * */
    public static String readStringTo(Path path, int size, Charset charset) {
        return charset.decode(readByteBuffer(path)).toString().substring(0, size);
    }

    /**
     * The method reads string from this file, starting from the file position.
     * The method is based on calling {@link FileIOUtils#readString(Path, Charset)}.
     *
     * @param path The path fo the file from which the characters are read.
     * @param pos The file position at which the reading is to begin. It is equal to an index of a character.
     * @param charset The charset to transform bytes to characters.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readString(Path, Charset)
     * */
    public static String readStringFrom(Path path, int pos, Charset charset) {
        return readString(path, charset).substring(pos);
    }

    /**
     * The method reads string matching length from this file, starting from the file position.
     * The method is based on calling {@link FileIOUtils#readString(Path, Charset)}
     *
     * @param path The path fo the file from which the characters are read.
     * @param pos The file position at which the reading is to begin. It is equal to an index of a character.
     * @param size The length of the string.
     * @param charset The charset to transform bytes to characters.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readString(Path, Charset)
     * */
    public static String readStringFromTo(Path path, int pos, int size, Charset charset) {
        return readString(path, charset).substring(pos, pos + size);
    }


    /**
     * The method reads bytes from this file to the given byte array.
     * The method is based on calling {@link FileIOUtils#read(Path, ByteBuffer)}
     *
     * @param path The path fo the file from which the bytes are read.
     * @param array The byte array in which the bytes are written.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#read(Path, ByteBuffer)
     * */
    public static void read(Path path, byte[] array) {
        read(path, ByteBuffer.wrap(array));
    }

    /**
     * The method reads bytes from this file to the given byte array, starting from the file position.
     * The method is based on calling {@link FileIOUtils#read(Path, ByteBuffer, long)}
     *
     * @param path The path fo the file from which the bytes are read.
     * @param array The byte array in which the bytes are written.
     * @param pos The file position at which the reading is to begin. It is equal to a number of a byte.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#read(Path, ByteBuffer, long)
     * */
    public static void read(Path path, byte[] array, long pos) {
        read(path, ByteBuffer.wrap(array), pos);
    }


    /**
     * The method reads bytes from this file to a byte buffer. The method is based on calling
     * {@link FileIOUtils#readByteBuffer(Path, long, int)}.
     *
     * @param path The path fo the file from which the bytes are read.
     * @return The instance of {@link ByteBuffer} filled read bytes.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readByteBuffer(Path, long, int)
     * */
    public static ByteBuffer readByteBuffer(Path path) {
        return readByteBuffer(path, (long) 0);
    }

    /**
     * The method reads a number of bytes from this file to a byte buffer. The method is based on calling
     * {@link FileIOUtils#readByteBuffer(Path, long, int)}.
     *
     * @param path The path fo the file from which the bytes are read.
     * @param size The number of the bytes to read.
     * @return The instance of {@link ByteBuffer} filled read bytes.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#readByteBuffer(Path, long, int)
     * */
    public static ByteBuffer readByteBuffer(Path path, int size) {
        return readByteBuffer(path, 0, size);
    }

    /**
     * The method reads bytes from this file to a byte buffer, starting from the file position.
     *
     * @param path The path fo the file from which the bytes are read.
     * @param pos The file position at which the reading is to begin. It is equal to a number of a byte.
     * @return The instance of {@link ByteBuffer} filled read bytes.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileChannel#read(ByteBuffer, long)
     * */
    public static ByteBuffer readByteBuffer(Path path, long pos) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer bb = ByteBuffer.allocate((int) (fc.size() - pos));
            if (pos == 0)
                fc.read(bb);
            else
                fc.read(bb, pos);
            bb.flip();
            return bb;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * The method reads a number of bytes from this file to a byte buffer, starting from the file position.
     *
     * @param path The path fo the file from which the bytes are read.
     * @param pos The file position at which the reading is to begin. It is equal to a number of a byte.
     * @param size The number of the bytes to read.
     * @return The instance of {@link ByteBuffer} filled read bytes.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileChannel#read(ByteBuffer, long)
     * */
    public static ByteBuffer readByteBuffer(Path path, long pos, int size) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer bb = ByteBuffer.allocate(size);
            if (pos == 0)
                fc.read(bb);
            else
                fc.read(bb, pos);
            bb.flip();
            return bb;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * The method reads bytes from this file to the given buffer. The method is based on calling
     * {@link FileIOUtils#read(Path, ByteBuffer, long)}.
     *
     * @param path The path fo the file from which the bytes are read.
     * @param buffer The bytes buffer in which the bytes are written.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileIOUtils#read(Path, ByteBuffer, long)
     * */
    public static void read(Path path, ByteBuffer buffer) {
        read(path, buffer, 0);
    }

    /**
     * The method reads bytes from this file to the given buffer, starting from the file position.
     *
     * @param path The path fo the file from which the bytes are read.
     * @param buffer The bytes buffer in which the bytes are written.
     * @param pos The file position at which the reading is to begin. It is equal to a number of a byte.
     * @throws RuntimeIOException if an {@link IOException} is thrown during reading.
     * @see FileChannel#read(ByteBuffer, long)
     * */
    public static void read(Path path, ByteBuffer buffer, long pos) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            if (pos == 0)
                fc.read(buffer);
            else
                fc.read(buffer, pos);
            buffer.flip();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method appends the given string to this file. The method is based on calling
     * {@link FileIOUtils#append(Path, String, Charset)}.
     *
     * @param path The path of the file in which the characters are appended.
     * @param text The string from which characters are to be appended.
     * @throws RuntimeIOException if an {@link IOException} is thrown during appending.
     * @see FileIOUtils#append(Path, String, Charset)
     * */
    public static void append(Path path, String text) {
        append(path, text, Charset.defaultCharset());
    }

    /**
     * The method appends the given string to this file, starting from given file position.
     * The method is based on calling {@link FileIOUtils#append(Path, String, int, Charset)}.
     *
     * @param path The path of the file in which the characters are appended.
     * @param text The string from which characters are to be appended.
     * @param pos The file position at which the transfer is to begin. It is equal to an index of a character.
     * @throws RuntimeIOException if an {@link IOException} is thrown during appending.
     * @see FileIOUtils#append(Path, String, int, Charset)
     * */
    public static void append(Path path, String text, int pos) {
        append(path, text, pos, Charset.defaultCharset());
    }

    /**
     * The method appends the given string to this file.
     *
     * @param path The path of the file in which the characters are appended.
     * @param text The string from which characters are to be appended.
     * @param charset The charset to transform characters to bytes and vice versa.
     * @throws RuntimeIOException if an {@link IOException} is thrown during appending.
     * @see FileIOUtils#readString(Path, Charset)
     * @see FileIOUtils#overwrite(Path, String, Charset)
     * */
    public static void append(Path path, String text, Charset charset) {
        String read = readString(path, charset);
        String write = read + text;
        overwrite(path, write, charset);
    }

    /**
     * The method appends the given string to this file, starting from given file position.
     *
     * @param path The path of the file in which the characters are appended.
     * @param text The string from which characters are to be appended.
     * @param pos The file position at which the transfer is to begin. It is equal to an index of a character.
     * @param charset The charset to transform characters to bytes and vice versa.
     * @throws RuntimeIOException if an {@link IOException} is thrown during appending.
     * @see FileIOUtils#readStringFrom(Path, int, Charset)
     * @see FileIOUtils#overwrite(Path, String, int, Charset)
     * */
    public static void append(Path path, String text, int pos, Charset charset) {
        String read = readStringFrom(path, pos, charset);
        String write =  text + read;
        overwrite(path, write, pos, charset);
    }

    /**
     * The method appends the given bytes to this file.
     *
     * @param path The path of the file in which the bytes are appended.
     * @param buffer The buffer from which bytes are to be appended.
     * @throws RuntimeIOException if an {@link IOException} is thrown during appending.
     * @see FileChannel#write(ByteBuffer)
     * */
    public static void append(Path path, ByteBuffer buffer) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.APPEND)) {
            fc.write(buffer);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * The method appends the given bytes to this file, starting from given file position.
     *
     * @param path The path of the file in which the bytes are appended.
     * @param buffer The buffer from which bytes are to be appended.
     * @param pos The file position at which the transfer is to begin. It is equal to a number of a byte.
     * @throws RuntimeIOException if an {@link IOException} is thrown during appending.
     * @see FileIOUtils#readByteBuffer(Path, long)
     * @see FileChannel#write(ByteBuffer, long)
     * */
    public static void append(Path path, ByteBuffer buffer, long pos) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE)) {
            ByteBuffer read2 = readByteBuffer(path, pos);
            fc.write(buffer, pos);
            fc.write(read2, pos + buffer.limit());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



    /**
     * The method writes the string to this file. All the characters that are after the zero position
     * and before the position equaling the string size are deleted, and the given string characters
     * are moved to their positions. The method is based on calling
     * {@link FileIOUtils#write(Path, String, Charset)}.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @throws RuntimeIOException if an {@link IOException} is thrown during writing.
     * @see FileIOUtils#write(Path, String, Charset)
     * */
    public static void write(Path path, String text) {
        write(path, text, Charset.defaultCharset());
    }

    /**
     * The method writes the string to this file, starting at the given file position.
     * All the characters that are after the given position and before the given position plus
     * the string size are deleted, and the given string characters are moved to their positions.
     * The method is based on calling {@link FileIOUtils#write(Path, String, int, Charset)}.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @param pos The file position at which the transfer is to begin. It is equal to an index of character.
     * @throws RuntimeIOException if an {@link IOException} is thrown during writing.
     * @see FileIOUtils#write(Path, String, int, Charset)
     * */
    public static void write(Path path, String text, int pos) {
        write(path, text, pos, Charset.defaultCharset());
    }

    /**
     * The method writes the string to this file. All the previous characters of the file are deleted
     * and the given string characters are moved to their positions. The method is based on calling
     * {@link FileIOUtils#overwrite(Path, String, Charset)}.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @throws RuntimeIOException if an {@link IOException} is thrown during overwriting.
     * @see FileIOUtils#overwrite(Path, String, Charset)
     * */
    public static void overwrite(Path path, String text) {
        overwrite(path, text, 0, Charset.defaultCharset());
    }

    /**
     * The method writes the string to this file, starting at the given file position. All
     * the characters that are after the given position are deleted, and the given string characters
     * are moved to their positions. The method is based on calling {@link FileIOUtils#overwrite(Path, String, int, Charset)}
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @param pos The file position at which the transfer is to begin. It is equal to an index of character.
     * @throws RuntimeIOException if an {@link IOException} is thrown during overwriting.
     * @see FileIOUtils#overwrite(Path, String, int, Charset)
     * */
    public static void overwrite(Path path, String text, int pos) {
        overwrite(path, text, pos, Charset.defaultCharset());
    }

    /**
     * The method writes the string to this file. All the characters that are after the zero position
     * and before the position equaling the string size are deleted, and the given string characters
     * are moved to their positions.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @param charset The charset to transform characters to bytes and vice versa.
     * @throws RuntimeIOException if an {@link IOException} is thrown during writing.
     * @see FileIOUtils#readString(Path, Charset)
     * @see FileIOUtils#overwrite(Path, String, Charset)
     * */
    public static void write(Path path, String text, Charset charset) {
        String read = readString(path, charset);
        String write = text.length() < read.length() ? text + read.substring(text.length()) : text;
        overwrite(path, write, charset);
    }

    /**
     * The method writes the string to this file, starting at the given file position. All
     * the characters that are after the given position and before the given position plus
     * the string size are deleted, and the given string characters are moved to their positions.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @param pos The file position at which the transfer is to begin. It is equal to an index of character.
     * @param charset The charset to transform characters to bytes and vice versa.
     * @throws RuntimeIOException if an {@link IOException} is thrown during writing.
     * @see FileIOUtils#readString(Path, Charset)
     * @see FileIOUtils#overwrite(Path, String, Charset)
     * */
    public static void write(Path path, String text, int pos, Charset charset) {
        String read = readString(path, charset);
        String write = read.substring(0, pos) + text + (pos + text.length() < read.length() ? read.substring(pos + text.length()) : "");
        overwrite(path, write, charset);
    }

    /**
     * The method writes the string to this file. All the previous characters of the file are deleted
     * and the given string characters are moved to their positions. The method is based on calling
     * {@link FileIOUtils#overwrite(Path, ByteBuffer)}.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @param charset The charset to transform characters to bytes.
     * @throws RuntimeIOException if an {@link IOException} is thrown during overwriting.
     * @see FileIOUtils#overwrite(Path, ByteBuffer)
     * */
    public static void overwrite(Path path, String text, Charset charset) {
        overwrite(path, charset.encode(text));
    }

    /**
     * The method writes the string to this file, starting at the given file position. All
     * the characters that are after the given position are deleted, and the given string characters
     * are moved to their positions.
     *
     * @param path The path of the file in which string is written.
     * @param text The string from which characters are to be transferred position.
     * @param pos The file position at which the transfer is to begin. It is equal to an index of character.
     * @param charset The charset to transform characters to bytes and vice versa.
     * @throws RuntimeIOException if an {@link IOException} is thrown during overwriting.
     * @see FileIOUtils#readString(Path, Charset)
     * @see FileIOUtils#overwrite(Path, String, Charset)
     * */
    public static void overwrite(Path path, String text, int pos, Charset charset) {
        String read = readString(path, charset);
        String write = read.substring(0, pos) + text;
        overwrite(path, write, charset);
    }


    /**
     * The method writes a sequence of bytes to this file from the given buffer. The bytes that are
     * from zero position to the buffer size are replaced with the buffer bytes. The method is
     * based on calling {@link FileIOUtils#write(Path, ByteBuffer, long)}
     *
     * @param path The path of the file in which bytes are written.
     * @param buffer The buffer from which bytes are to be transferred position.
     * @throws RuntimeIOException if an {@link IOException} is thrown during writing.
     * @see FileChannel#write(ByteBuffer, long)
     * */
    public static void write(Path path, ByteBuffer buffer) {
        write(path, buffer, 0);
    }

    /**
     * The method writes a sequence of bytes to this file from the given buffer, starting
     * at the given file position. The bytes that are from the given position to the given
     * position plus buffer size are replaced with the buffer bytes.
     *
     * @param path The path of the file in which bytes are written.
     * @param buffer The buffer from which bytes are to be transferred position.
     * @param pos The file position at which the transfer is to begin. It is equal to a number of a byte.
     * @throws RuntimeIOException if an {@link IOException} is thrown during writing.
     * @see FileChannel#write(ByteBuffer, long)
     * */
    public static void write(Path path, ByteBuffer buffer, long pos) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE)) {
            fc.write(buffer, pos);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * The method writes a sequence of bytes to this file from the given buffer. All the previous
     * bytes of the file are deleted, using {@link FileChannel#truncate(long)}. The method is
     * based on calling {@link FileIOUtils#overwrite(Path, ByteBuffer, long)}.
     *
     * @param path The path of the file in which bytes are written.
     * @param buffer The buffer from which bytes are to be transferred position.
     * @throws RuntimeIOException if an {@link IOException} is thrown during overwriting.
     * @see FileChannel#truncate(long)
     * @see FileChannel#write(ByteBuffer, long)
     * */
    public static void overwrite(Path path, ByteBuffer buffer) {
        overwrite(path, buffer, 0);
    }

    /**
     * The method writes a sequence of bytes to this file from the given buffer, starting
     * at the given file position. All the bytes that were after the given position are
     * deleted, there is used {@link FileChannel#truncate(long)}, and the given buffer bytes
     * are moved to their positions.
     *
     * @param path The path of the file in which bytes are written.
     * @param buffer The buffer from which bytes are to be transferred position.
     * @param pos The file position at which the transfer is to begin. It is equal to a number of a byte.
     * @throws RuntimeIOException if an {@link IOException} is thrown during overwriting.
     * @see FileChannel#truncate(long)
     * @see FileChannel#write(ByteBuffer, long)
     * */
    public static void overwrite(Path path, ByteBuffer buffer, long pos) {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE)) {
            fc.truncate(pos);
            fc.write(buffer, pos);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

}
