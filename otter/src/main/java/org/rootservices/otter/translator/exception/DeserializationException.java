package org.rootservices.otter.translator.exception;

import java.util.Optional;

public class DeserializationException extends Exception {
    private Optional<String> key = Optional.empty();
    private Reason reason;

    public DeserializationException(String message, Reason reason, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public DeserializationException(String message, String key, Reason reason, Throwable cause) {
        super(message, cause);
        this.key = Optional.of(key);
        this.reason = reason;
    }

    public Optional<String> getKey() {
        return key;
    }

    public void setKey(Optional<String> key) {
        this.key = key;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }
}
