package org.rootservices.otter.router.factory;

import helper.FixtureFactory;
import helper.entity.DummyBetween;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.*;

public class BetweenFactoryTest {
    private BetweenFactory<DummySession, DummyUser, EmptyPayload> subject;
    private Betweens<DummySession, DummyUser, EmptyPayload> csrfPrepare;
    private Betweens<DummySession, DummyUser, EmptyPayload> csrfProtect;
    private Betweens<DummySession, DummyUser, EmptyPayload> sessionRequired;
    private Betweens<DummySession, DummyUser, EmptyPayload> sessionOptional;
    private Optional<Between<DummySession, DummyUser, EmptyPayload>> authRequired;
    private Optional<Between<DummySession, DummyUser, EmptyPayload>> authOptional;

    @Before
    public void setUp() {
        csrfPrepare = FixtureFactory.makeBetweens();
        csrfProtect = FixtureFactory.makeBetweens();
        sessionRequired = FixtureFactory.makeBetweens();
        sessionOptional = FixtureFactory.makeBetweens();
        authRequired = Optional.of(new DummyBetween<>());
        authOptional = Optional.of(new DummyBetween<>());

        subject = new BetweenFactory<>(csrfPrepare, csrfProtect, sessionRequired, sessionOptional, authRequired, authOptional);
    }

    @Test
    public void makeWhenGetAndCSRF() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore(), is(csrfPrepare.getBefore()));
    }

    @Test
    public void makeWhenGetAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(sessionOptional.getBefore()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter(), is(sessionOptional.getAfter()));
    }

    @Test
    public void makeWhenGetAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(sessionRequired.getBefore()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter(), is(sessionRequired.getAfter()));

    }

    @Test
    public void makeWhenGetAndCsrfAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.get()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.get()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCSRF() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(csrfProtect.getBefore()));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void makeWhenPostAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(sessionOptional.getBefore()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter(), is(sessionOptional.getAfter()));
    }

    @Test
    public void makeWhenPostAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(sessionRequired.getBefore()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter(), is(sessionRequired.getAfter()));

    }

    @Test
    public void makeWhenPostAndCsrfAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.get()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.get()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPutAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPutAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPatchAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPatchAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenDeleteAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenDeleteAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }
}
