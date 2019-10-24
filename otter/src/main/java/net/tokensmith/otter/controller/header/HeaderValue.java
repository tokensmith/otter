package net.tokensmith.otter.controller.header;


public enum HeaderValue {
    INVALID_TOKEN ("error=\"invalid_token\""),
    NO_STORE ("no-store"),
    NO_CACHE ("no-cache");

    private String value;

    HeaderValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
