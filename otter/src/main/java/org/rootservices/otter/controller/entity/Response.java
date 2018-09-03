package org.rootservices.otter.controller.entity;



import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;

public class Response<T> {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String,Cookie> cookies;
    private Optional<ByteArrayOutputStream> payload;
    private Optional<String> template;
    private Optional<Object> presenter;
    private Optional<T> session = Optional.empty();

    public Response() {
    }

    public Response(StatusCode statusCode, Map<String, String> headers, Map<String, Cookie> cookies, Optional<ByteArrayOutputStream> payload, Optional<String> template, Optional<Object> presenter) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookies = cookies;
        this.payload = payload;
        this.template = template;
        this.presenter = presenter;
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

    public Optional<T> getSession() {
        return session;
    }

    public void setSession(Optional<T> session) {
        this.session = session;
    }
}
