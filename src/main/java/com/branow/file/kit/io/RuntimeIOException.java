package com.branow.file.kit.io;

import java.io.IOException;

/**
 * A runtime exception wrapper for {@link IOException}.
 * Created to simplify work with file reading and writing in way
 * exchanging checked exception to unchecked when
 * probability of throwing checked exception is enough low
 * */
public class RuntimeIOException extends RuntimeException{

    public RuntimeIOException() {
        super();
    }

    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }

}