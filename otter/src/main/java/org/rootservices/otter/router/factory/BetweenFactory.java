package org.rootservices.otter.router.factory;


import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BetweenFactory<S, U, P> {
    private Betweens<S, U, P> csrfPrepare;
    private Betweens<S, U, P> csrfProtect;
    private Betweens<S, U, P> sessionRequired;
    private Betweens<S, U, P> sessionOptional;

    private Optional<Between<S, U, P>> authRequired;
    private Optional<Between<S, U, P>> authOptional;

    public BetweenFactory(Betweens<S, U, P> csrfPrepare, Betweens<S, U, P> csrfProtect, Betweens<S, U, P> sessionRequired, Betweens<S, U, P> sessionOptional, Optional<Between<S, U, P>> authRequired, Optional<Between<S, U, P>> authOptional) {
        this.csrfPrepare = csrfPrepare;
        this.csrfProtect = csrfProtect;
        this.sessionRequired = sessionRequired;
        this.sessionOptional = sessionOptional;
        this.authRequired = authRequired;
        this.authOptional = authOptional;
    }

    public Betweens<S, U, P> make(Method method, List<Label> labels) {
        Betweens<S, U, P> betweens = new Betweens<S, U, P>(new ArrayList<>(), new ArrayList<>());

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

    public Betweens<S, U, P> makeGet(List<Label> labels) {
        Betweens<S, U, P> betweens = new Betweens<S, U, P>(new ArrayList<>(), new ArrayList<>());
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

    public Betweens<S, U, P> makePost(List<Label> labels) {
        Betweens<S, U, P> betweens = new Betweens<S, U, P>(new ArrayList<>(), new ArrayList<>());
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

    public Betweens<S, U, P> makePut(List<Label> labels) {
        Betweens<S, U, P> betweens = new Betweens<S, U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public Betweens<S, U, P> makePatch(List<Label> labels) {
        Betweens<S, U, P> betweens = new Betweens<S, U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    public Betweens<S, U, P> makeDelete(List<Label> labels) {
        Betweens<S, U, P> betweens = new Betweens<S, U, P>(new ArrayList<>(), new ArrayList<>());
        authentication(labels, betweens);
        return betweens;
    }

    protected void authentication(List<Label> labels, Betweens<S, U, P> betweens) {
        if (labels.contains(Label.AUTH_OPTIONAL) && authOptional.isPresent()) {
            betweens.getBefore().add(authOptional.get());
        }
        if (labels.contains(Label.AUTH_REQUIRED) && authRequired.isPresent()) {
            betweens.getBefore().add(authRequired.get());
        }
    }
}
