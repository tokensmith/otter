package org.rootservices.otter.router.entity.io;

import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.StatusCode;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;

public class Answer {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<ByteArrayOutputStream> payload;
    private Optional<String> template;
    private Optional<Object> presenter;

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

    public Optional<ByteArrayOutputStream> getPayload() {
        return payload;
    }

    public void setPayload(Optional<ByteArrayOutputStream> payload) {
        this.payload = payload;
    }

    public Optional<String> getTemplate() {
        return template;
    }

    public void setTemplate(Optional<String> template) {
        this.template = template;
    }

    public Optional<Object> getPresenter() {
        return presenter;
    }

    public void setPresenter(Optional<Object> presenter) {
        this.presenter = presenter;
    }
}
