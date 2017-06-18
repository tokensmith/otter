package org.rootservices.otter.controller.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.header.AuthScheme;
import org.rootservices.otter.router.entity.Method;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class RequestBuilder {
    private Optional<Matcher> matcher;
    private Method method;
    private String pathWithParams;
    private Optional<AuthScheme> authScheme;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, String> formData;
    private BufferedReader body;

    public RequestBuilder matcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
        return this;
    }

    public RequestBuilder method(Method method) {
        this.method = method;
        return this;
    }

    public RequestBuilder pathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
        return this;
    }

    public RequestBuilder authScheme(Optional<AuthScheme> authScheme) {
        this.authScheme = authScheme;
        return this;
    }

    public RequestBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public RequestBuilder queryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public RequestBuilder formData(Map<String, String> formData) {
        this.formData = formData;
        return this;
    }

    public RequestBuilder body(BufferedReader body) {
        this.body = body;
        return this;
    }

    public Request build() {
        return new Request(this.matcher, this.method, this.pathWithParams, this.authScheme, this.headers, this.cookies, this.queryParams, this.formData, this.body);
    }
}
