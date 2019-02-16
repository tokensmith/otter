package org.rootservices.otter.router.factory;

import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestBetweenFlyweight<U> {
    private Optional<RestBetween<U>> authRequired;
    private Optional<RestBetween<U>> authOptional;

    public RestBetweenFlyweight(Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional) {
        this.authRequired = authRequired;
        this.authOptional = authOptional;
    }

    public RestBetweens<U> make(Method method, List<Label> labels) {
        RestBetweens<U> betweens = new RestBetweens<U>(new ArrayList<>(), new ArrayList<>());

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

    public RestBetweens<U> makeGet(List<Label> labels) {
        RestBetweens<U> betweens = new RestBetweens<U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U> makePost(List<Label> labels) {
        RestBetweens<U> betweens = new RestBetweens<U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U> makePut(List<Label> labels) {
        RestBetweens<U> betweens = new RestBetweens<U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U> makePatch(List<Label> labels) {
        RestBetweens<U> betweens = new RestBetweens<U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<U> makeDelete(List<Label> labels) {
        RestBetweens<U> betweens = new RestBetweens<U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }
    
    protected void authentication(List<Label> labels, RestBetweens<U> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && authOptional.isPresent()) {
            betweens.getBefore().add(authOptional.get());
        }
        if (labels.contains(Label.AUTH_REQUIRED) && authRequired.isPresent()) {
            betweens.getBefore().add(authRequired.get());
        }
    }
}
