package org.rootservices.otter.controller.header;


public enum ContentType {
    JWT ("application/jwt"),
    UTF_8 ("charset=UTF-8"),
    JWT_UTF_8 ("application/jwt;charset=UTF-8");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
