package net.tokensmith.otter.router.factory;

import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestBetweenFlyweight<S extends DefaultSession, U> {
    private Optional<RestBetween<S, U>> authRequired;
    private Optional<RestBetween<S, U>> authOptional;

    public RestBetweenFlyweight(Optional<RestBetween<S, U>> authRequired, Optional<RestBetween<S, U>> authOptional) {
        this.authRequired = authRequired;
        this.authOptional = authOptional;
    }

    public RestBetweens<S, U> make(Method method, List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());

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

    public RestBetweens<S, U> makeGet(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makePost(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makePut(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makePatch(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makeDelete(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }
    
    protected void authentication(List<Label> labels, RestBetweens<S, U> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && authOptional.isPresent()) {
            betweens.getBefore().add(authOptional.get());
        }
        if (labels.contains(Label.AUTH_REQUIRED) && authRequired.isPresent()) {
            betweens.getBefore().add(authRequired.get());
        }
    }
}
