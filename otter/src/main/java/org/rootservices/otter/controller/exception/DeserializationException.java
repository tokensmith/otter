package org.rootservices.otter.controller.exception;


public class DeserializationException extends Exception {
    private String description;

    public DeserializationException(String message, Throwable cause, String description) {
        super(message, cause);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
