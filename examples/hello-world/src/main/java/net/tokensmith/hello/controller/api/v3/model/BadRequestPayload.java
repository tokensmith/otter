package net.tokensmith.hello.controller.api.v3.model;

import net.tokensmith.otter.translatable.Translatable;

public class BadRequestPayload implements Translatable {
    private String message;
    private String key;
    private String reason;

    public BadRequestPayload() {
    }

    public BadRequestPayload(String message, String key, String reason) {
        this.message = message;
        this.key = key;
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
