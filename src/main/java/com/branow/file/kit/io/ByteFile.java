package com.branow.file.kit.io;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * An abstraction of any existing computer file
 * that let read and write bytes.
 * The class extends {@link FileEntity}.
 * */
public class ByteFile extends FileEntity {

    /**
     * The constructor base on the constructor
     * {@link FileEntity#FileEntity(File)}
     * */
    public ByteFile(File file) {
        super(file);
    }

    /**
     * The constructor base on the constructor
     * {@link FileEntity#FileEntity(File)}
     * */
    public ByteFile(String path) {
        super(path);
    }

    /**
     * The constructor base on the constructor
     * {@link FileEntity#FileEntity(File)}
     * */
    public ByteFile(Path path) {
        super(path);
    }


    /**
     * the method read all bytes from file and return it
     * @return an array of bytes read from the file
     * @see ByteFile#readBytes(int, int)
     * */
    public byte[] readBytes() {
        return readBytes(0, (int) size());
    }

    /**
     * the method read bytes from file and return it
     * @param length maximum number of bytes to read
     * @return an array of bytes read from the file
     * @see ByteFile#readBytes(int, int)
     * */
    public byte[] readBytes(int length) {
        return readBytes(0, length);
    }

    /**
     * the method read bytes from file and return it
     * @param off offset at which to start storing bytes
     * @param length maximum number of bytes to read
     * @return an array of bytes read from the file
     * @throws RuntimeIOException if IOException is thrown
     * @see BufferedInputStream#skipNBytes(long) 
     * @see BufferedInputStream#readNBytes(byte[], int, int)
     * */
    public byte[] readBytes(int off, int length) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file()))) {
            bis.skipNBytes(off);
            return bis.readNBytes(length);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }


    /**
     * the method writes all transmitted bytes to the file
     * @param bytes a bytes array that will be written into the file
     * @see ByteFile#writeBytes(byte[], int)
     * */
    public void writeBytes(byte[] bytes) {
        writeBytes(bytes, 0);
    }

    /**
     * the method writes or appends all transmitted bytes to the file
     * @param bytes a bytes array that will be written into the file
     * @param off the start offset in the data
     * @throws RuntimeIOException if IOException is thrown.
     * @see BufferedOutputStream#write(byte[])
     * */
    public void writeBytes(byte[] bytes, int off) {
        byte[] write = new byte[off + bytes.length];
        byte[] read = readBytes(off);
        System.arraycopy(read, 0, write, 0, read.length);
        System.arraycopy(bytes, 0, write, off, bytes.length);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file()))) {
            bos.write(write);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }


    /**
     * the method appends all transmitted bytes to the file
     * @param bytes a bytes array that will be written into the file
     * @see ByteFile#appendBytes(byte[], int)
     * */
    public void appendBytes(byte[] bytes) {
        writeBytes(bytes, 0);
    }

    /**
     * the method appends all transmitted bytes to the file
     * @param bytes a bytes array that will be written into the file
     * @param off the start offset in the data
     * @see BufferedOutputStream#write(byte[])
     * */
    public void appendBytes(byte[] bytes, int off) {
        byte[] read = readBytes();
        byte[] write = new byte[read.length + bytes.length];
        System.arraycopy(read, 0, write, 0, off);
        System.arraycopy(bytes, 0, write, off, bytes.length);
        System.arraycopy(read, off, write, off + bytes.length, read.length - off);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file()))) {
            bos.write(write);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
