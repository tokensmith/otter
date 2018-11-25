package org.rootservices.otter.router.factory;

import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestBetweenFlyweight<U, P> {
    private Optional<RestBetween<U, P>> authRequired;
    private Optional<RestBetween<U, P>> authOptional;

    public RestBetweenFlyweight(Optional<RestBetween<U, P>> authRequired, Optional<RestBetween<U, P>> authOptional) {
        this.authRequired = authRequired;
        this.authOptional = authOptional;
    }

    public RestBetweens<U, P> make(Method method, List<Label> labels) {
        RestBetweens<U, P> betweens = new RestBetweens<U, P>(new ArrayList<>(), new ArrayList<>());

        if (Method.GET.equals(method)) {
            betweens = makeGet(labels);
        } else if (Method.POST.equals(method)) {
            betweens = makePost(labels);
        } else if (Method.PUT.equals(method)) {
            betweens = makePut(labels);
        } else if (Method.PATCH.equals(method)) {
            betweens = makePatch(labels);
        } else if (Method.DELETE.equals(method)) {
            betweens = makeDelete(labels);
        }
        return betweens;

    }

    public RestBetweens<U, P> makeGet(List<Label> labels) {
        RestBetweens<U, P> betweens = new RestBetweens<U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U, P> makePost(List<Label> labels) {
        RestBetweens<U, P> betweens = new RestBetweens<U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U, P> makePut(List<Label> labels) {
        RestBetweens<U, P> betweens = new RestBetweens<U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U, P> makePatch(List<Label> labels) {
        RestBetweens<U, P> betweens = new RestBetweens<U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U, P> makeDelete(List<Label> labels) {
        RestBetweens<U, P> betweens = new RestBetweens<U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }
    
    protected void authentication(List<Label> labels, RestBetweens<U, P> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && authOptional.isPresent()) {
            betweens.getBefore().add(authOptional.get());
        }
        if (labels.contains(Label.AUTH_REQUIRED) && authRequired.isPresent()) {
            betweens.getBefore().add(authRequired.get());
        }
    }
}
