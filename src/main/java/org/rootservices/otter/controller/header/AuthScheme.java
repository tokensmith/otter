package org.rootservices.otter.controller.header;


public enum AuthScheme {
    BEARER ("Bearer"),
    BASIC ("Basic");

    private String scheme;

    AuthScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }
}
