package net.tokensmith.otter.dispatch.entity;

import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;

import java.util.Map;
import java.util.Optional;

public class RestBtwnResponse {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<byte[]> payload;
    private Optional<byte[]> rawPayload;

    public RestBtwnResponse() {
    }

    public RestBtwnResponse(StatusCode statusCode, Map<String, String> headers, Map<String, Cookie> cookies, Optional<byte[]> payload, Optional<byte[]> rawPayload) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookies = cookies;
        this.payload = payload;
        this.rawPayload = payload;
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

    public Optional<byte[]> getPayload() {
        return payload;
    }

    public void setPayload(Optional<byte[]> payload) {
        this.payload = payload;
    }

    public Optional<byte[]> getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(Optional<byte[]> rawPayload) {
        this.rawPayload = rawPayload;
    }
}
