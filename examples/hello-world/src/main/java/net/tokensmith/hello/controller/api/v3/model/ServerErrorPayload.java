package net.tokensmith.hello.controller.api.v3.model;

import net.tokensmith.otter.translatable.Translatable;

public class ServerErrorPayload implements Translatable {
    private String message;

    public ServerErrorPayload() {
    }

    public ServerErrorPayload(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
