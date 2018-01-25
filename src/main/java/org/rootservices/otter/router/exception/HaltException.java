package org.rootservices.otter.router.exception;


public class HaltException extends Exception {
    public HaltException(String message) {
        super(message);
    }

    public HaltException(String message, Throwable cause) {
        super(message, cause);
    }
}
