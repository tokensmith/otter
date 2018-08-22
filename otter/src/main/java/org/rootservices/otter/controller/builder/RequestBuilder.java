package org.rootservices.otter.controller.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.session.Session;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class RequestBuilder<T extends Session>  {
    private Optional<Matcher> matcher;
    private Method method;
    private String pathWithParams;
    private MimeType contentType;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, List<String>> formData;
    private Optional<byte[]> body;
    private Optional<String> csrfChallenge;
    private String ipAddress;

    public RequestBuilder<T> matcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
        return this;
    }

    public RequestBuilder<T> method(Method method) {
        this.method = method;
        return this;
    }

    public RequestBuilder<T> pathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
        return this;
    }

    public RequestBuilder<T> contentType(MimeType contentType) {
        this.contentType = contentType;
        return this;
    }

    public RequestBuilder<T> headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder<T> cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public RequestBuilder<T> queryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public RequestBuilder<T> formData(Map<String, List<String>> formData) {
        this.formData = formData;
        return this;
    }

    public RequestBuilder<T> body(Optional<byte[]> body) {
        this.body = body;
        return this;
    }

    public RequestBuilder<T> csrfChallenge(Optional<String> csrfChallenge) {
        this.csrfChallenge = csrfChallenge;
        return this;
    }

    public RequestBuilder<T> ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Request<T> build() {
        return new Request<T>(this.matcher, this.method, this.pathWithParams, this.contentType, this.headers, this.cookies, this.queryParams, this.formData, this.body, this.csrfChallenge, this.ipAddress);
    }
}
