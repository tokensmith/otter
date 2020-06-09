package net.tokensmith.otter.controller.entity;


import java.util.Objects;

public class Cookie {
    private String name;
    private String value;
    private String comment;
    private String domain;
    private int maxAge = -1;
    private String path;
    private boolean secure;
    private int version = 0;
    private boolean httpOnly;

    public Cookie(String name, String value, String comment, String domain, int maxAge, String path, boolean secure, int version, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.comment = comment;
        this.domain = domain;
        this.maxAge = maxAge;
        this.path = path;
        this.secure = secure;
        this.version = version;
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cookie cookie = (Cookie) o;
        return maxAge == cookie.maxAge &&
                secure == cookie.secure &&
                version == cookie.version &&
                httpOnly == cookie.httpOnly &&
                Objects.equals(name, cookie.name) &&
                Objects.equals(value, cookie.value) &&
                Objects.equals(comment, cookie.comment) &&
                Objects.equals(domain, cookie.domain) &&
                Objects.equals(path, cookie.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, comment, domain, maxAge, path, secure, version, httpOnly);
    }

    public static class Builder {
        private String name;
        private String value;
        private String comment;
        private String domain;
        private int maxAge = -1;
        private String path;
        private boolean secure;
        private int version = 0;
        private boolean httpOnly;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder maxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Builder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public Cookie build() {
            return new Cookie(name, value, comment, domain, maxAge, path, secure, version, httpOnly);
        }
    }
}
