package org.rootservices.otter.config;

public class CookieConfig {
    private String name;
    private Boolean isSecure;
    private Integer age;
    private Boolean httpOnly;

    public CookieConfig(String name, Boolean isSecure, Integer age, Boolean httpOnly) {
        this.name = name;
        this.isSecure = isSecure;
        this.age = age;
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSecure() {
        return isSecure;
    }

    public void setSecure(Boolean secure) {
        isSecure = secure;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
