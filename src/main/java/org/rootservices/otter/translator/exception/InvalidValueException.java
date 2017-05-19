package org.rootservices.otter.translator.exception;


public class InvalidValueException extends Exception {
    private String key;

    public InvalidValueException(String message, Throwable cause, String key) {
        super(message, cause);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
