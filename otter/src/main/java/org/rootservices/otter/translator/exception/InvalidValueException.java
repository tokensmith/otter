package org.rootservices.otter.translator.exception;


public class InvalidValueException extends Exception {
    private String key;
    private String value;

    public InvalidValueException(String message, Throwable cause, String key, String value) {
        super(message, cause);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
