package com.mahanko.threadstask.exception;

public class CustomThreadException extends Exception {
    public CustomThreadException() {
    }

    public CustomThreadException(String message) {
        super(message);
    }

    public CustomThreadException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomThreadException(Throwable cause) {
        super(cause);
    }
}
