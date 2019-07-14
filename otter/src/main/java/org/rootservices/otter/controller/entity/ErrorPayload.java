package org.rootservices.otter.controller.entity;


// 113: once default 400 is there, then delete this.
public class ErrorPayload {
    private String error;
    private String description;

    public ErrorPayload() {}

    public ErrorPayload(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
