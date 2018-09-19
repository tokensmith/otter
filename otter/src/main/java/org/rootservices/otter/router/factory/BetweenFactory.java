package org.rootservices.otter.router.factory;


import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.ArrayList;
import java.util.List;


public class BetweenFactory<S, U> {
    private Betweens<S, U> csrfPrepare;
    private Betweens<S, U> csrfProtect;
    private Betweens<S, U> sessionRequired;
    private Betweens<S, U> sessionOptional;

    public BetweenFactory(Betweens<S, U> csrfPrepare, Betweens<S, U> csrfProtect, Betweens<S, U> sessionRequired, Betweens<S, U> sessionOptional) {
        this.csrfPrepare = csrfPrepare;
        this.csrfProtect = csrfProtect;
        this.sessionRequired = sessionRequired;
        this.sessionOptional = sessionOptional;
    }

    public Betweens<S, U> make(Method method, List<Label> labels) {
        Betweens<S, U> betweens = new Betweens<S, U>(new ArrayList<>(), new ArrayList<>());

        if (Method.GET.equals(method)) {
            betweens = makeGet(labels);
        } else if (Method.POST.equals(method)) {
            betweens = makePost(labels);
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
        return betweens;
    }
}
