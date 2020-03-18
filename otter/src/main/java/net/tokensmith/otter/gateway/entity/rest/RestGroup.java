package net.tokensmith.otter.gateway.entity.rest;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.translatable.Translatable;

import java.util.Map;
import java.util.Optional;


public class RestGroup<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;
    private Optional<RestBetween<S, U>> authRequired;
    private Optional<RestBetween<S, U>> authOptional;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    // for engine to handle errors
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors;


    public RestGroup(String name, Class<S> sessionClazz, Optional<RestBetween<S, U>> authRequired, Optional<RestBetween<S, U>> authOptional, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors) {
        this.name = name;
        this.sessionClazz = sessionClazz;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
        this.restErrors = restErrors;
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

    public Optional<RestBetween<S, U>> getAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(Optional<RestBetween<S, U>> authRequired) {
        this.authRequired = authRequired;
    }

    public Optional<RestBetween<S, U>> getAuthOptional() {
        return authOptional;
    }

    public void setAuthOptional(Optional<RestBetween<S, U>> authOptional) {
        this.authOptional = authOptional;
    }

    public Map<StatusCode, RestError<U, ? extends Translatable>> getRestErrors() {
        return restErrors;
    }

    public void setRestErrors(Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
        this.restErrors = restErrors;
    }

    public Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }
}
