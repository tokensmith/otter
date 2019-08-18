package org.rootservices.otter.controller.entity;

import org.rootservices.otter.translatable.Translatable;

import java.util.List;

public class ClientError implements Translatable {
    private Source source;
    private String key;
    private String actual;
    private List<String> expected;
    private String reason;

    public enum Source {
        HEADER, BODY, URL
    }

    public ClientError() {
    }

    public ClientError(Source source, String key, String actual, List<String> expected) {
        this.source = source;
        this.key = key;
        this.actual = actual;
        this.expected = expected;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public List<String> getExpected() {
        return expected;
    }

    public void setExpected(List<String> expected) {
        this.expected = expected;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
