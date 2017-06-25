package org.rootservices.otter.controller.entity;


import org.rootservices.otter.controller.header.AuthScheme;
import org.rootservices.otter.router.entity.Method;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class Request {
    private Optional<Matcher> matcher;
    private Method method;
    private String pathWithParams;
    private Optional<AuthScheme> authScheme;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, String> formData;
    private Optional<BufferedReader> payload;

    public Request() {}

    public Request(Optional<Matcher> matcher, Method method, String pathWithParams, Optional<AuthScheme> authScheme, Map<String, String> headers, Map<String, Cookie> cookies, Map<String, List<String>> queryParams, Map<String, String> formData, Optional<BufferedReader> payload) {
        this.matcher = matcher;
        this.method = method;
        this.pathWithParams = pathWithParams;
        this.authScheme = authScheme;
        this.headers = headers;
        this.cookies = cookies;
        this.queryParams = queryParams;
        this.formData = formData;
        this.payload = payload;
    }

    public Optional<Matcher> getMatcher() {
        return matcher;
    }

    public void setMatcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathWithParams() {
        return pathWithParams;
    }

    public void setPathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
    }

    public Optional<AuthScheme> getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(Optional<AuthScheme> authScheme) {
        this.authScheme = authScheme;
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

    public Map<String, String> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, String> formData) {
        this.formData = formData;
    }

    public Optional<BufferedReader> getPayload() {
        return payload;
    }

    public void setPayload(Optional<BufferedReader> payload) {
        this.payload = payload;
    }
}
