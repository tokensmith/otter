package net.tokensmith.otter.server;

import org.apache.tomcat.util.descriptor.web.ErrorPage;

import java.util.List;

public class HttpServerConfig {
    private String documentRoot;
    private int port;
    private String requestLog;
    private Class clazz;
    private List<String> gzipMimeTypes;
    private List<ErrorPage> errorPages;

    public HttpServerConfig(String documentRoot, int port, String requestLog, Class clazz, List<String> gzipMimeTypes, List<ErrorPage> errorPages) {
        this.documentRoot = documentRoot;
        this.port = port;
        this.requestLog = requestLog;
        this.clazz = clazz;
        this.gzipMimeTypes = gzipMimeTypes;
        this.errorPages = errorPages;
    }

    public String getDocumentRoot() {
        return documentRoot;
    }

    public void setDocumentRoot(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLog) {
        this.requestLog = requestLog;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public List<String> getGzipMimeTypes() {
        return gzipMimeTypes;
    }

    public void setGzipMimeTypes(List<String> gzipMimeTypes) {
        this.gzipMimeTypes = gzipMimeTypes;
    }

    public List<ErrorPage> getErrorPages() {
        return errorPages;
    }

    public void setErrorPages(List<ErrorPage> errorPages) {
        this.errorPages = errorPages;
    }
}