package net.tokensmith.otter.dispatch.entity;

import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.router.entity.Method;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public class RestErrorRequest<U> {
    private Optional<Matcher> matcher;
    private List<MimeType> possibleContentTypes;
    private List<MimeType> possibleAccept;
    private Method method;
    private String scheme;
    private String authority;
    private Integer port;
    private String pathWithParams;
    private MimeType contentType;
    private MimeType accept;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, List<String>> queryParams;
    private Map<String, List<String>> formData;
    private Optional<byte[]> body;
    private String ipAddress;
    private Optional<U> user;

    public Optional<Matcher> getMatcher() {
        return matcher;
    }

    public void setMatcher(Optional<Matcher> matcher) {
        this.matcher = matcher;
    }

    public List<MimeType> getPossibleContentTypes() {
        return possibleContentTypes;
    }

    public void setPossibleContentTypes(List<MimeType> possibleContentTypes) {
        this.possibleContentTypes = possibleContentTypes;
    }

    public List<MimeType> getPossibleAccept() {
        return possibleAccept;
    }

    public void setPossibleAccept(List<MimeType> possibleAccept) {
        this.possibleAccept = possibleAccept;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public MimeType getAccept() {
        return accept;
    }

    public void setAccept(MimeType accept) {
        this.accept = accept;
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
