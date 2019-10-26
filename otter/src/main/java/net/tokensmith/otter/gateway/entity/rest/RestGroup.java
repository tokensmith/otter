package net.tokensmith.otter.gateway.entity.rest;


import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.translatable.Translatable;

import java.util.Map;
import java.util.Optional;


public class RestGroup<U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<U>> authRequired;
    private Optional<RestBetween<U>> authOptional;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    // for engine to handle errors
    private Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors;


    public RestGroup(String name, Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors) {
        this.name = name;
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

    public Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }
}
