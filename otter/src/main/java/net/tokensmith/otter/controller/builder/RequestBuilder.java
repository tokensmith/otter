package net.tokensmith.otter.controller.builder;


import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.router.entity.Method;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * Builder to build a Otter Request.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class RequestBuilder<S, U>  {
    private Optional<Matcher> matcher;
    private Method method;
    private String scheme;
    private String authority;
    private Integer port;
    private String pathWithParams;
    private MimeType contentType;
    private MimeType accept;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, List<String>> formData;
    private Optional<byte[]> body;
    private Optional<String> csrfChallenge;
    private String ipAddress;

    public RequestBuilder<S, U> matcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
        return this;
    }

    public RequestBuilder<S, U> method(Method method) {
        this.method = method;
        return this;
    }

    public RequestBuilder<S, U> scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public RequestBuilder<S, U> port(Integer port) {
        this.port = port;
        return this;
    }

    public RequestBuilder<S, U> authority(String authority) {
        this.authority = authority;
        return this;
    }

    public RequestBuilder<S, U> scheme(Integer port) {
        this.port = port;
        return this;
    }

    public RequestBuilder<S, U> pathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
        return this;
    }

    public RequestBuilder<S, U> contentType(MimeType contentType) {
        this.contentType = contentType;
        return this;
    }

    public RequestBuilder<S, U> accept(MimeType accept) {
        this.accept = accept;
        return this;
    }

    public RequestBuilder<S, U> headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder<S, U> cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public RequestBuilder<S, U> queryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public RequestBuilder<S, U> formData(Map<String, List<String>> formData) {
        this.formData = formData;
        return this;
    }

    public RequestBuilder<S, U> body(Optional<byte[]> body) {
        this.body = body;
        return this;
    }

    public RequestBuilder<S, U> csrfChallenge(Optional<String> csrfChallenge) {
        this.csrfChallenge = csrfChallenge;
        return this;
    }

    public RequestBuilder<S, U> ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Request<S, U> build() {
        return new Request<S, U>(this.matcher, this.method, this.scheme, this.authority, this.port, this.pathWithParams, this.contentType, this.accept, this.headers, this.cookies, this.queryParams, this.formData, this.body, this.csrfChallenge, this.ipAddress);
    }
}
