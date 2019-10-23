package net.tokensmith.otter.translator.exception;


public class UnknownKeyException extends Exception {
    private String key;

    public UnknownKeyException(String message, Throwable cause, String key) {
        super(message, cause);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
