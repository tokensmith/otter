package org.rootservices.otter.controller.entity;


public enum StatusCode {
    OK (200),
    CREATED (201),
    BAD_REQUEST(400),
    NOT_FOUND (404),
    SERVER_ERROR (500),
    NOT_IMPLEMENTED (501);

    private Integer code;

    StatusCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
