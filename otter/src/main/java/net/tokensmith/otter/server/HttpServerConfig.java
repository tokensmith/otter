package net.tokensmith.otter.server;

import net.tokensmith.otter.servlet.EntryFilter;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import jakarta.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

public class HttpServerConfig {
    private String documentRoot;
    private int port;
    private String requestLog;
    private Class clazz;
    private List<String> gzipMimeTypes;
    private List<ErrorPage> errorPages;
    private Class<? extends Filter> filterClass;

    public HttpServerConfig(String documentRoot, int port, String requestLog, Class clazz, List<String> gzipMimeTypes, List<ErrorPage> errorPages, Class<? extends Filter> filterClass) {
        this.documentRoot = documentRoot;
        this.port = port;
        this.requestLog = requestLog;
        this.clazz = clazz;
        this.gzipMimeTypes = gzipMimeTypes;
        this.errorPages = errorPages;
        this.filterClass = filterClass;
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

    public Class<? extends Filter> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    public static class Builder {
        private String documentRoot;
        private Class clazz;
        private int port;
        private String requestLog;
        private List<String> gzipMimeTypes = new ArrayList<>();
        private List<ErrorPage> errorPages = new ArrayList<>();
        private Class<? extends Filter> filterClass = EntryFilter.class;

        public Builder documentRoot(String documentRoot) {
            this.documentRoot = documentRoot;
            return this;
        }

        public Builder clazz(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder requestLog(String requestLog) {
            this.requestLog = requestLog;
            return this;
        }

        public Builder gzipMimeTypes(List<String> gzipMimeTypes) {
            this.gzipMimeTypes = gzipMimeTypes;
            return this;
        }

        public Builder errorPages(List<ErrorPage> errorPages) {
            this.errorPages = errorPages;
            return this;
        }

        public Builder filterClass(Class<? extends Filter> filterClass) {
            this.filterClass = filterClass;
            return this;
        }

        public HttpServerConfig build() {
            return new HttpServerConfig(documentRoot, port, requestLog, clazz, gzipMimeTypes, errorPages, filterClass);
        }
    }
}
