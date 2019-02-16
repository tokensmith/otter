package org.rootservices.otter.gateway.entity;


import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.Optional;

public class RestGroup<U extends DefaultUser> {
    private String name;
    private Optional<RestBetween<U>> authRequired;
    private Optional<RestBetween<U>> authOptional;

    public RestGroup(String name, Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional) {
        this.name = name;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
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
}
