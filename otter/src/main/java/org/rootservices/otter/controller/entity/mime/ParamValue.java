package org.rootservices.otter.controller.entity.mime;

public enum ParamValue {
    UTF_8 ("utf-8"),
    US_ASCII ("us-ascii");

    private String value;

    ParamValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
