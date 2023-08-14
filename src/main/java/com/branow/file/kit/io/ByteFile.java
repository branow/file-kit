package com.branow.file.kit.io;

import com.branow.file.kit.utils.FileIOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 * An abstraction of any existing computer file that let read and write bytes.
 * The class extends {@link FileEntity}. All class methods uses methods of {@link FileIOUtils}.
 */
public class ByteFile extends FileEntity {

    /**
     * @see FileEntity#FileEntity(File)
     */
    public ByteFile(File file) {
        super(file);
    }

    /**
     * @see FileEntity#FileEntity(String)
     */
    public ByteFile(String path) {
        super(path);
    }

    /**
     * @see FileEntity#FileEntity(Path)
     */
    public ByteFile(Path path) {
        super(path);
    }


    /**
     * Reads all bytes from the file and returns them.
     *
     * @return The byte array read from the file.
     * @see FileIOUtils#readByteBuffer(Path)
     */
    public byte[] readBytes() {
        return FileIOUtils.readByteBuffer(path()).array();
    }

    /**
     * Reads bytes from the file and returns them.
     *
     * @param length The maximum number of bytes to read.
     * @return The byte array read from the file.
     * @see FileIOUtils#readByteBuffer(Path, int)
     */
    public byte[] readBytes(int length) {
        return FileIOUtils.readByteBuffer(path(), length).array();
    }

    /**
     * Reads bytes from the file and returns them.
     *
     * @param off    The offset at which it starts reading bytes.
     * @return The byte array read from the file.
     * @see FileIOUtils#readByteBuffer(Path, long)
     */
    public byte[] readBytes(long off) {
        return FileIOUtils.readByteBuffer(path(), off).array();
    }

    /**
     * Reads bytes from the file and returns them.
     *
     * @param off    The offset at which it starts reading bytes.
     * @param length The maximum number of bytes to read.
     * @return The byte array read from the file.
     * @see FileIOUtils#readByteBuffer(Path, long, int)
     */
    public byte[] readBytes(long off, int length) {
        return FileIOUtils.readByteBuffer(path(), off, length).array();
    }



    /**
     * Writes all the given bytes to this file skipping the given number of bytes.
     * All previous bytes of that file before the position equaling {@code bytes} length
     * are removed.
     *
     * @param bytes The bytes array that is written into the file.
     * @see FileIOUtils#write(Path, ByteBuffer)
     */
    public void writeBytes(byte[] bytes) {
        FileIOUtils.write(path(), ByteBuffer.wrap(bytes));
    }

    /**
     * Writes all the given bytes to this file skipping the given number of bytes.
     * All previous bytes of that file after {@code off} position and before
     * the position equaling {@code off} position plus {@code bytes} length are removed.
     *
     * @param bytes The bytes array that is written into the file.
     * @param off   The offset at which it starts writing bytes.
     * @see FileIOUtils#write(Path, ByteBuffer, long)
     */
    public void writeBytes(byte[] bytes, long off) {
        FileIOUtils.write(path(), ByteBuffer.wrap(bytes), off);
    }

    /**
     * Overwrites all the given bytes to this file skipping the given number of bytes.
     * All previous bytes of this file are removed.
     *
     * @param bytes The bytes array that is overwritten into the file.
     * @see FileIOUtils#overwrite(Path, ByteBuffer)
     */
    public void overwriteBytes(byte[] bytes) {
        FileIOUtils.overwrite(path(), ByteBuffer.wrap(bytes));
    }

    /**
     * Overwrites all the given bytes to this file skipping the given number of bytes.
     * All previous bytes of that file after {@code off} position are removed.
     *
     * @param bytes The bytes array that is overwritten into the file.
     * @param off   The offset at which it starts overwriting bytes.
     * @see FileIOUtils#overwrite(Path, ByteBuffer, long)
     */
    public void overwriteBytes(byte[] bytes, long off) {
        FileIOUtils.overwrite(path(), ByteBuffer.wrap(bytes), off);
    }


    /**
     * Appends all the given bytes to the end of this file.
     *
     * @param bytes The bytes array that is appending to the file.
     * @see FileIOUtils#append(Path, ByteBuffer)
     */
    public void appendBytes(byte[] bytes) {
        FileIOUtils.overwrite(path(), ByteBuffer.wrap(bytes));
    }

    /**
     * Appends all the given bytes to this file skipping the given number of bytes.
     *
     * @param bytes The bytes array that is appending to the file.
     * @param off   The offset at which it starts appending bytes.
     * @see FileIOUtils#append(Path, ByteBuffer, long)
     */
    public void appendBytes(byte[] bytes, long off) {
        FileIOUtils.overwrite(path(), ByteBuffer.wrap(bytes), off);
    }
}
