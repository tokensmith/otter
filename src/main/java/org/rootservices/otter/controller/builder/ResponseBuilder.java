package org.rootservices.otter.controller.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.*;

public class ResponseBuilder {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<String> payload;
    private Optional<String> template;
    private Optional<Object> presenter;

    public ResponseBuilder() {}

    public ResponseBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public ResponseBuilder cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public ResponseBuilder payload(Optional<String> payload) {
        this.payload = payload;
        return this;
    }

    public ResponseBuilder template(Optional<String> template) {
        this.template = template;
        return this;
    }

    public ResponseBuilder presenter(Optional<Object> presenter) {
        this.presenter = presenter;
        return this;
    }

    public ResponseBuilder statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseBuilder ok() {
        this.statusCode = StatusCode.OK;
        return this;
    }

    public ResponseBuilder notFound() {
        this.statusCode = StatusCode.NOT_FOUND;
        return this;
    }

    public ResponseBuilder notImplemented() {
        this.statusCode = StatusCode.NOT_IMPLEMENTED;
        return this;
    }

    public ResponseBuilder badRequest() {
        this.statusCode = StatusCode.BAD_REQUEST;
        return this;
    }

    public ResponseBuilder unAuthorized() {
        this.statusCode = StatusCode.UNAUTHORIZED;
        return this;
    }

    public ResponseBuilder serverError() {
        this.statusCode = StatusCode.SERVER_ERROR;
        return this;
    }

    public Response build() {
        return new Response(this.statusCode, this.headers, this.cookies, this.payload, this.template, this.presenter);
    }
}
