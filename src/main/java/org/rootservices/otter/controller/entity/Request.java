package org.rootservices.otter.controller.entity;


import org.rootservices.otter.controller.header.AuthScheme;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class Request {
    private Optional<Matcher> matcher;
    private Optional<AuthScheme> scheme;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private BufferedReader body;

    public Optional<Matcher> getMatcher() {
        return matcher;
    }

    public void setMatcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
    }

    public Optional<AuthScheme> getScheme() {
        return scheme;
    }

    public void setScheme(Optional<AuthScheme> scheme) {
        this.scheme = scheme;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }

    public BufferedReader getBody() {
        return body;
    }

    public void setBody(BufferedReader body) {
        this.body = body;
    }
}
