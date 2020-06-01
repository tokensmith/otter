package net.tokensmith.otter.router.factory;

import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RestBetweenFlyweight<S, U> {

    private Map<Label, List<RestBetween<S, U>>> before;
    private Map<Label, List<RestBetween<S, U>>> after;

    public RestBetweenFlyweight(Map<Label, List<RestBetween<S, U>>> before, Map<Label, List<RestBetween<S, U>>> after) {
        this.before = before;
        this.after = after;
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
            betweens.getBefore().addAll(before.get(Label.SESSION_OPTIONAL));
            betweens.getAfter().addAll(after.get(Label.SESSION_OPTIONAL));
        }

        if (labels.contains(Label.SESSION_REQUIRED)) {
            betweens.getBefore().addAll(before.get(Label.SESSION_REQUIRED));
            betweens.getAfter().addAll(after.get(Label.SESSION_REQUIRED));
        }
    }

    protected void csrfProtect(List<Label> labels, RestBetweens<S, U> betweens) {
        // 188: unit test missing
        if (labels.contains(Label.CSRF_PROTECT)) {
            betweens.getBefore().addAll(before.get(Label.CSRF_PROTECT));
        }
    }

    protected void authentication(List<Label> labels, RestBetweens<S, U> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && Objects.nonNull(before.get(Label.AUTH_OPTIONAL)) && before.get(Label.AUTH_OPTIONAL).size() > 0) {
            betweens.getBefore().addAll(before.get(Label.AUTH_OPTIONAL));
        }
        if (labels.contains(Label.AUTH_REQUIRED) && Objects.nonNull(before.get(Label.AUTH_REQUIRED)) && before.get(Label.AUTH_REQUIRED).size() > 0) {
            betweens.getBefore().addAll(before.get(Label.AUTH_REQUIRED));
        }
    }
}
