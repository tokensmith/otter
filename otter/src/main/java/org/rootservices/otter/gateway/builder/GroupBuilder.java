package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.gateway.entity.Group;

public class GroupBuilder<S> {
    private String name;
    private Class<S> sessionClazz;

    public GroupBuilder<S> name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder<S> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        return this;
    }

    public Group<S> build() {
        return new Group<S>(name, sessionClazz);
    }
}
