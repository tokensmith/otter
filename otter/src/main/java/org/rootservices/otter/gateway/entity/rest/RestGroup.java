package org.rootservices.otter.gateway.entity.rest;


import org.rootservices.otter.controller.RestErrorResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.Map;
import java.util.Optional;

public class RestGroup<U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<U>> authRequired;
    private Optional<RestBetween<U>> authOptional;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;


    public RestGroup(String name, Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
        this.name = name;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
        this.restErrors = restErrors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<RestBetween<U>> getAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(Optional<RestBetween<U>> authRequired) {
        this.authRequired = authRequired;
    }

    public Optional<RestBetween<U>> getAuthOptional() {
        return authOptional;
    }

    public void setAuthOptional(Optional<RestBetween<U>> authOptional) {
        this.authOptional = authOptional;
    }

    public Map<StatusCode, RestError<U, ? extends Translatable>> getRestErrors() {
        return restErrors;
    }

    public void setRestErrors(Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
        this.restErrors = restErrors;
    }
}
