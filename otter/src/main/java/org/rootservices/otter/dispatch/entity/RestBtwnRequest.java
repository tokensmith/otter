package org.rootservices.otter.dispatch.entity;

import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class RestBtwnRequest<U> {
    private Optional<Matcher> matcher;
    private Method method;
    private String pathWithParams;
    private MimeType contentType;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, List<String>> formData;
    private Optional<byte[]> body;
    private String ipAddress;
    private Optional<U> user;

    public RestBtwnRequest() {}

    public RestBtwnRequest(Optional<Matcher> matcher, Method method, String pathWithParams, MimeType contentType, Map<String, String> headers, Map<String, Cookie> cookies, Map<String, List<String>> queryParams, Map<String, List<String>> formData, Optional<byte[]> body, String ipAddress) {
        this.matcher = matcher;
        this.method = method;
        this.pathWithParams = pathWithParams;
        this.contentType = contentType;
        this.headers = headers;
        this.cookies = cookies;
        this.queryParams = queryParams;
        this.formData = formData;
        this.body = body;
        this.ipAddress = ipAddress;
    }

    public Optional<Matcher> getMatcher() {
        return matcher;
    }

    public void setMatcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathWithParams() {
        return pathWithParams;
    }

    public void setPathWithParams(String pathWithParams) {
        this.pathWithParams = pathWithParams;
    }

    public MimeType getContentType() {
        return contentType;
    }

    public void setContentType(MimeType contentType) {
        this.contentType = contentType;
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

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, List<String>> getFormData() {
        return formData;
    }

    public void setFormData(Map<String, List<String>> formData) {
        this.formData = formData;
    }

    public Optional<byte[]> getBody() {
        return body;
    }

    public void setBody(Optional<byte[]> body) {
        this.body = body;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Optional<U> getUser() {
        return user;
    }

    public void setUser(Optional<U> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(method).append(" ").append(pathWithParams).toString();
    }
}