package net.tokensmith.otter.controller.entity.mime;

public enum SubType {
    HTML ("html"), JSON ("json"), XML ("xml"), JWT ("jwt"), FORM ("x-www-form-urlencoded");

    private String value;

    SubType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toLowerCase();
    }
}
