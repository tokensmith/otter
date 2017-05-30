package org.rootservices.otter.controller.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import java.util.*;

public class ResponseBuilder {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<String> body;
    private Optional<String> template;
    private Optional<Object> presenter;

    public ResponseBuilder() {}

    public ResponseBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public ResponseBuilder setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public ResponseBuilder setBody(Optional<String> body) {
        this.body = body;
        return this;
    }

    public ResponseBuilder setTemplate(Optional<String> template) {
        this.template = template;
        return this;
    }

    public ResponseBuilder setPresenter(Optional<Object> presenter) {
        this.presenter = presenter;
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

    public Response build() {
        return new Response(this.statusCode, this.headers, this.cookies, this.body, this.template, this.presenter);
    }
}
