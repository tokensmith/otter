package org.rootservices.otter.controller.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.response.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class ResponseBuilder<T> {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<byte[]> payload;
    private Optional<String> template;
    private Optional<Object> presenter;

    public ResponseBuilder() {}

    public ResponseBuilder<T> headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public ResponseBuilder<T> cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public ResponseBuilder<T> payload(Optional<byte[]> payload) {
        this.payload = payload;
        return this;
    }

    public ResponseBuilder<T> template(Optional<String> template) {
        this.template = template;
        return this;
    }

    public ResponseBuilder<T> presenter(Optional<Object> presenter) {
        this.presenter = presenter;
        return this;
    }

    public ResponseBuilder<T> statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseBuilder<T> ok() {
        this.statusCode = StatusCode.OK;
        return this;
    }

    public ResponseBuilder<T> notFound() {
        this.statusCode = StatusCode.NOT_FOUND;
        return this;
    }

    public ResponseBuilder<T> notImplemented() {
        this.statusCode = StatusCode.NOT_IMPLEMENTED;
        return this;
    }

    public ResponseBuilder<T> badRequest() {
        this.statusCode = StatusCode.BAD_REQUEST;
        return this;
    }

    public ResponseBuilder<T> unAuthorized() {
        this.statusCode = StatusCode.UNAUTHORIZED;
        return this;
    }

    public ResponseBuilder<T> serverError() {
        this.statusCode = StatusCode.SERVER_ERROR;
        return this;
    }

    public Response<T> build() {
        return new Response<T>(this.statusCode, this.headers, this.cookies, this.payload, this.template, this.presenter);
    }
}
