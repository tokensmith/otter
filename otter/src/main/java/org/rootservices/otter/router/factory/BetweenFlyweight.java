package org.rootservices.otter.router.factory;


import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.between.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BetweenFlyweight<S, U> {
    private Betweens<S, U> csrfPrepare;
    private Betweens<S, U> csrfProtect;
    private Betweens<S, U> sessionRequired;
    private Betweens<S, U> sessionOptional;

    private Optional<Between<S, U>> authRequired;
    private Optional<Between<S, U>> authOptional;

    public BetweenFlyweight(Betweens<S, U> csrfPrepare, Betweens<S, U> csrfProtect, Betweens<S, U> sessionRequired, Betweens<S, U> sessionOptional, Optional<Between<S, U>> authRequired, Optional<Between<S, U>> authOptional) {
        this.csrfPrepare = csrfPrepare;
        this.csrfProtect = csrfProtect;
        this.sessionRequired = sessionRequired;
        this.sessionOptional = sessionOptional;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
    }

    public Betweens<S, U> make(Method method, List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());

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

    public Betweens<S, U> makeGet(List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());
        if(labels.contains(Label.CSRF)) {
            betweens.getBefore().addAll(csrfPrepare.getBefore());
        }
        if (labels.contains(Label.SESSION_OPTIONAL)) {
            betweens.getBefore().addAll(sessionOptional.getBefore());
            betweens.getAfter().addAll(sessionOptional.getAfter());
        }
        if (labels.contains(Label.SESSION_REQUIRED)) {
            betweens.getBefore().addAll(sessionRequired.getBefore());
            betweens.getAfter().addAll(sessionRequired.getAfter());
        }
        authentication(labels, betweens);
        return betweens;
    }

    public Betweens<S, U> makePost(List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());
        if(labels.contains(Label.CSRF)) {
            betweens.getBefore().addAll(csrfProtect.getBefore());
        }
        if (labels.contains(Label.SESSION_OPTIONAL)) {
            betweens.getBefore().addAll(sessionOptional.getBefore());
            betweens.getAfter().addAll(sessionOptional.getAfter());
        }
        if (labels.contains(Label.SESSION_REQUIRED)) {
            betweens.getBefore().addAll(sessionRequired.getBefore());
            betweens.getAfter().addAll(sessionRequired.getAfter());
        }
        authentication(labels, betweens);
        return betweens;
    }

    public Betweens<S, U> makePut(List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public Betweens<S, U> makePatch(List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public Betweens<S, U> makeDelete(List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    protected void authentication(List<Label> labels, Betweens<S, U> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && authOptional.isPresent()) {
            betweens.getBefore().add(authOptional.get());
        }
        if (labels.contains(Label.AUTH_REQUIRED) && authRequired.isPresent()) {
            betweens.getBefore().add(authRequired.get());
        }
    }
}
