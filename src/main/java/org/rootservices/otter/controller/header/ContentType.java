package org.rootservices.otter.controller.header;


public enum ContentType {
    JWT ("application/jwt"),
    JSON ("application/json"),
    UTF_8 ("charset=UTF-8"),
    JWT_UTF_8 ("application/jwt;charset=UTF-8"),
    JSON_UTF_8 ("application/json;charset=UTF-8"),
    FORM_URL_ENCODED ("application/x-www-form-urlencoded");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
