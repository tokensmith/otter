package org.rootservices.otter.router.builder;

import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.io.Answer;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;

public class AnswerBuilder {
    private StatusCode statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Optional<byte[]> payload;
    private Optional<String> template;
    private Optional<Object> presenter;

    public AnswerBuilder() {}

    public AnswerBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public AnswerBuilder cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public AnswerBuilder payload(Optional<byte[]> payload) {
        this.payload = payload;
        return this;
    }

    public AnswerBuilder template(Optional<String> template) {
        this.template = template;
        return this;
    }

    public AnswerBuilder presenter(Optional<Object> presenter) {
        this.presenter = presenter;
        return this;
    }

    public AnswerBuilder statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public AnswerBuilder ok() {
        this.statusCode = StatusCode.OK;
        return this;
    }

    public AnswerBuilder notFound() {
        this.statusCode = StatusCode.NOT_FOUND;
        return this;
    }

    public AnswerBuilder notImplemented() {
        this.statusCode = StatusCode.NOT_IMPLEMENTED;
        return this;
    }

    public AnswerBuilder badRequest() {
        this.statusCode = StatusCode.BAD_REQUEST;
        return this;
    }

    public AnswerBuilder unAuthorized() {
        this.statusCode = StatusCode.UNAUTHORIZED;
        return this;
    }

    public AnswerBuilder serverError() {
        this.statusCode = StatusCode.SERVER_ERROR;
        return this;
    }

    public Answer build() {
        return new Answer(this.statusCode, this.headers, this.cookies, this.payload, this.template, this.presenter);
    }
}
