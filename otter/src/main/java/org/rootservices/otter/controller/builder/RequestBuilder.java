package org.rootservices.otter.controller.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * Builder to build a Otter Request.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 * @param <P> Payload object, used for rest requests.
 */
public class RequestBuilder<S, U, P>  {
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
    private Optional<P> payload = Optional.empty();

    public RequestBuilder<S, U, P> matcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
        return this;
    }

    public RequestBuilder<S, U, P> method(Method method) {
        this.method = method;
        return this;
    }

    public RequestBuilder<S, U, P> pathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
        return this;
    }

    public RequestBuilder<S, U, P> contentType(MimeType contentType) {
        this.contentType = contentType;
        return this;
    }

    public RequestBuilder<S, U, P> headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder<S, U, P> cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public RequestBuilder<S, U, P> queryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public RequestBuilder<S, U, P> formData(Map<String, List<String>> formData) {
        this.formData = formData;
        return this;
    }

    public RequestBuilder<S, U, P> body(Optional<byte[]> body) {
        this.body = body;
        return this;
    }

    public RequestBuilder<S, U, P> csrfChallenge(Optional<String> csrfChallenge) {
        this.csrfChallenge = csrfChallenge;
        return this;
    }

    public RequestBuilder<S, U, P> ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public RequestBuilder<S, U, P> payload(P payload) {
        this.payload = Optional.of(payload);
        return this;
    }

    public Request<S, U, P> build() {
        return new Request<S, U, P>(this.matcher, this.method, this.pathWithParams, this.contentType, this.headers, this.cookies, this.queryParams, this.formData, this.body, this.csrfChallenge, this.ipAddress, this.payload);
    }
}
