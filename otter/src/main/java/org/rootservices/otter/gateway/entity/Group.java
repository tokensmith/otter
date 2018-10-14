package org.rootservices.otter.gateway.entity;

public class Group<S> {
    private String name;
    private Class<S> sessionClazz;

    public Group(String name, Class<S> sessionClazz) {
        this.name = name;
        this.sessionClazz = sessionClazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<S> getSessionClazz() {
        return sessionClazz;
    }

    public void setSessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
    }
}
