package net.tokensmith.otter.router.factory;

import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestBetweenFlyweight<S, U> {

    private Map<Label, List<RestBetween<S, U>>> labelBefore;
    private Map<Label, List<RestBetween<S, U>>> labelAfter;

    private List<RestBetween<S, U>> befores;
    private List<RestBetween<S, U>> afters;

    public RestBetweenFlyweight(Map<Label, List<RestBetween<S, U>>> labelBefore, Map<Label, List<RestBetween<S, U>>> labelAfter, List<RestBetween<S, U>> befores, List<RestBetween<S, U>> afters) {
        this.labelBefore = labelBefore;
        this.labelAfter = labelAfter;
        this.befores = befores;
        this.afters = afters;
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
        betweens.getBefore().addAll(befores);
        betweens.getAfter().addAll(afters);
        return betweens;

    }

    public RestBetweens<S, U> makeGet(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        csrfProtect(labels, betweens);
        session(labels, betweens);
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makePost(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        csrfProtect(labels, betweens);
        session(labels, betweens);
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makePut(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        csrfProtect(labels, betweens);
        session(labels, betweens);
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makePatch(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        csrfProtect(labels, betweens);
        session(labels, betweens);
        authentication(labels, betweens);
        return betweens;
    }

    public RestBetweens<S, U> makeDelete(List<Label> labels) {
        RestBetweens<S, U> betweens = new RestBetweens<S, U>(new ArrayList<>(), new ArrayList<>());
        csrfProtect(labels, betweens);
        session(labels, betweens);
        authentication(labels, betweens);
        return betweens;
    }

    protected void session(List<Label> labels, RestBetweens<S, U> betweens) {
        if (labels.contains(Label.SESSION_OPTIONAL)) {
            betweens.getBefore().addAll(labelBefore.get(Label.SESSION_OPTIONAL));
            betweens.getAfter().addAll(labelAfter.get(Label.SESSION_OPTIONAL));
        }

        if (labels.contains(Label.SESSION_REQUIRED)) {
            betweens.getBefore().addAll(labelBefore.get(Label.SESSION_REQUIRED));
            betweens.getAfter().addAll(labelAfter.get(Label.SESSION_REQUIRED));
        }
    }

    protected void csrfProtect(List<Label> labels, RestBetweens<S, U> betweens) {
        // 188: unit test missing
        if (labels.contains(Label.CSRF_PROTECT)) {
            betweens.getBefore().addAll(labelBefore.get(Label.CSRF_PROTECT));
        }
    }

    protected void authentication(List<Label> labels, RestBetweens<S, U> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && Objects.nonNull(labelBefore.get(Label.AUTH_OPTIONAL)) && labelBefore.get(Label.AUTH_OPTIONAL).size() > 0) {
            betweens.getBefore().addAll(labelBefore.get(Label.AUTH_OPTIONAL));
        }
        if (labels.contains(Label.AUTH_REQUIRED) && Objects.nonNull(labelBefore.get(Label.AUTH_REQUIRED)) && labelBefore.get(Label.AUTH_REQUIRED).size() > 0) {
            betweens.getBefore().addAll(labelBefore.get(Label.AUTH_REQUIRED));
        }
    }
}
