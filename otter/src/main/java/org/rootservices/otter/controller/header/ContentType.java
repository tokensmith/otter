package org.rootservices.otter.controller.header;


public enum ContentType {
    JWT ("application/jwt"),
    JSON ("application/json"),
    UTF_8 ("charset=utf-8"),
    HTML_UTF_8 ("text/html; charset=utf-8;"),
    JWT_UTF_8 ("application/jwt; charset=utf-8"),
    JSON_UTF_8 ("application/json; charset=utf-8;"),
    FORM_URL_ENCODED ("application/x-www-form-urlencoded");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
