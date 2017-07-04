package org.rootservices.otter.controller.entity;


import org.rootservices.otter.router.entity.Method;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class Request {
    private Optional<Matcher> matcher;
    private Method method;
    private String pathWithParams;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, List<String>> formData;
    private Optional<String> body;
    private Optional<String> csrfChallenge;

    public Request() {}

    public Request(Optional<Matcher> matcher, Method method, String pathWithParams, Map<String, String> headers, Map<String, Cookie> cookies, Map<String, List<String>> queryParams, Map<String, List<String>> formData, Optional<String> body, Optional<String> csrfChallenge) {
        this.matcher = matcher;
        this.method = method;
        this.pathWithParams = pathWithParams;
        this.headers = headers;
        this.cookies = cookies;
        this.queryParams = queryParams;
        this.formData = formData;
        this.body = body;
        this.csrfChallenge = csrfChallenge;
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

    public Map<String, List<String>> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, List<String>> formData) {
        this.formData = formData;
    }

    public Optional<String> getBody() {
        return body;
    }

    public void setBody(Optional<String> body) {
        this.body = body;
    }

    public Optional<String> getCsrfChallenge() {
        return csrfChallenge;
    }

    public void setCsrfChallenge(Optional<String> csrfChallenge) {
        this.csrfChallenge = csrfChallenge;
    }
}
