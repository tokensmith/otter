package org.rootservices.otter.controller.header;


public enum Header {
    AUTH ("Authorization"),
    AUTH_MISSING ("WWW-Authenticate"),
    ACCEPT ("Accept"),
    CACHE_CONTROL ("Cache-Control"),
    PRAGMA ("Pragma"),
    CONTENT_TYPE ("Content-TopLevelType"),
    LOCATION ("Location");

    private String value;

    Header(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
