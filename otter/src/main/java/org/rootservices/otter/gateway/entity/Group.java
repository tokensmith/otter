package org.rootservices.otter.gateway.entity;

import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.translatable.Translatable;

import java.util.Optional;


public class Group<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private String name;
    private Class<S> sessionClazz;
    private Optional<Between<S, U, P>> authRequired;
    private Optional<Between<S, U, P>> authOptional;

    public Group(String name, Class<S> sessionClazz, Optional<Between<S, U, P>> authRequired, Optional<Between<S, U, P>> authOptional) {
        this.name = name;
        this.sessionClazz = sessionClazz;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
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

    public Optional<Between<S, U, P>> getAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(Optional<Between<S, U, P>> authRequired) {
        this.authRequired = authRequired;
    }

    public Optional<Between<S, U, P>> getAuthOptional() {
        return authOptional;
    }

    public void setAuthOptional(Optional<Between<S, U, P>> authOptional) {
        this.authOptional = authOptional;
    }
}
