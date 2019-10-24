package net.tokensmith.otter.translator.exception;


public class DuplicateKeyException extends Exception {
    private String key;

    public DuplicateKeyException(String message, Throwable cause, String key) {
        super(message, cause);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
