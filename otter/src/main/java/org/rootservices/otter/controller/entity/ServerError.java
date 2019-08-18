package org.rootservices.otter.controller.entity;

import org.rootservices.otter.translatable.Translatable;

public class ServerError implements Translatable {
    private String message;

    public ServerError() {
    }

    public ServerError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
