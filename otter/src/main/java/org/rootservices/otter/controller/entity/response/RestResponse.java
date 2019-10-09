package org.rootservices.otter.controller.entity.response;

import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.Map;
import java.util.Optional;

public class RestResponse<P> {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<P> payload = Optional.empty();
    private Optional<byte[]> rawPayload = Optional.empty();

    public RestResponse() {
    }

    public RestResponse(StatusCode statusCode, Map<String, String> headers, Map<String, Cookie> cookies, Optional<P> payload) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookies = cookies;
        this.payload = payload;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
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

    public Optional<P> getPayload() {
        return payload;
    }

    public void setPayload(Optional<P> payload) {
        this.payload = payload;
    }

    public Optional<byte[]> getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(Optional<byte[]> rawPayload) {
        this.rawPayload = rawPayload;
    }
}
