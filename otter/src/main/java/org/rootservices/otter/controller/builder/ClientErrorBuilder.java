package org.rootservices.otter.controller.builder;

import org.rootservices.otter.controller.entity.ClientError;

import java.util.ArrayList;
import java.util.List;

public class ClientErrorBuilder {
    private ClientError.Source source;
    private String key;
    private String actual;
    private List<String> expected = new ArrayList<>();
    private String reason;


    public ClientErrorBuilder source(ClientError.Source source) {
        this.source = source;
        return this;
    }

    public ClientErrorBuilder key(String key) {
        this.key = key;
        return this;
    }

    public ClientErrorBuilder actual(String actual) {
        this.actual = actual;
        return this;
    }

    public ClientErrorBuilder expected(List<String> expected) {
        this.expected = expected;
        return this;
    }

    public ClientErrorBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    public ClientError build() {
        return new ClientError(source, key, actual, expected, reason);
    }
}
