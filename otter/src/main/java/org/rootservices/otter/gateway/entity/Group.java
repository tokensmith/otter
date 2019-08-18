package org.rootservices.otter.gateway.entity;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.between.Between;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;


public class Group<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;
    private Optional<Between<S, U>> authRequired;
    private Optional<Between<S, U>> authOptional;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;

    public Group(String name, Class<S> sessionClazz, Optional<Between<S, U>> authRequired, Optional<Between<S, U>> authOptional, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors) {
        this.name = name;
        this.sessionClazz = sessionClazz;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
        this.errorResources = errorResources;
        this.dispatchErrors = dispatchErrors;
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

    public Optional<Between<S, U>> getAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(Optional<Between<S, U>> authRequired) {
        this.authRequired = authRequired;
    }

    public Optional<Between<S, U>> getAuthOptional() {
        return authOptional;
    }

    public void setAuthOptional(Optional<Between<S, U>> authOptional) {
        this.authOptional = authOptional;
    }

    public Map<StatusCode, Resource<S, U>> getErrorResources() {
        return errorResources;
    }

    public void setErrorResources(Map<StatusCode, Resource<S, U>> errorResources) {
        this.errorResources = errorResources;
    }

    public Map<StatusCode, ErrorTarget<S, U>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, ErrorTarget<S, U>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }
}
