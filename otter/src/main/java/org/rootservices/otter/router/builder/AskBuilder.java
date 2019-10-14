package org.rootservices.otter.router.builder;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.io.Ask;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class AskBuilder {
    private Optional<Matcher> matcher;
    private List<MimeType> possibleContentTypes;
    private List<MimeType> possibleAccepts;
    private Method method;
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

    public AskBuilder matcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
        return this;
    }

    public AskBuilder possibleContentTypes(List<MimeType> possibleContentTypes) {
        this.possibleContentTypes = possibleContentTypes;
        return this;
    }

    public AskBuilder possibleAccepts(List<MimeType> possibleAccepts) {
        this.possibleAccepts = possibleAccepts;
        return this;
    }

    public AskBuilder method(Method method) {
        this.method = method;
        return this;
    }

    public AskBuilder pathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
        return this;
    }

    public AskBuilder contentType(MimeType contentType) {
        this.contentType = contentType;
        return this;
    }

    public AskBuilder accept(MimeType accept) {
        this.accept = accept;
        return this;
    }

    public AskBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public AskBuilder cookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public AskBuilder queryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public AskBuilder formData(Map<String, List<String>> formData) {
        this.formData = formData;
        return this;
    }

    public AskBuilder body(Optional<byte[]> body) {
        this.body = body;
        return this;
    }

    public AskBuilder csrfChallenge(Optional<String> csrfChallenge) {
        this.csrfChallenge = csrfChallenge;
        return this;
    }

    public AskBuilder ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Ask build() {
        return new Ask(this.matcher, this.possibleContentTypes, this.possibleAccepts, this.method, this.pathWithParams, this.contentType, this.accept, this.headers, this.cookies, this.queryParams, this.formData, this.body, this.csrfChallenge, this.ipAddress);
    }
}
