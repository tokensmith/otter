package net.tokensmith.otter.controller.entity;

import java.util.ArrayList;
import java.util.List;

public class Cause {
    private Cause.Source source;
    private String key;
    private String actual;
    private List<String> expected;
    private String reason;

    public enum Source {
        HEADER, BODY, URL
    }

    public Cause() {
    }

    public Cause(Cause.Source source, String key, String actual, List<String> expected, String reason) {
        this.source = source;
        this.key = key;
        this.actual = actual;
        this.expected = expected;
        this.reason = reason;
    }

    public Cause.Source getSource() {
        return source;
    }

    public void setSource(Cause.Source source) {
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

    public static class Builder {
        private Cause.Source source;
        private String key;
        private String actual;
        private List<String> expected = new ArrayList<>();
        private String reason;

        public enum Source {
            HEADER, BODY, URL
        }

        public Builder source(Cause.Source source) {
            this.source = source;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder actual(String actual) {
            this.actual = actual;
            return this;
        }

        public Builder expected(List<String> expected) {
            this.expected = expected;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Cause build() {
            return new Cause(source, key, actual, expected, reason);
        }
    }
}
