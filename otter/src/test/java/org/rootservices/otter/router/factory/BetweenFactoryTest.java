package org.rootservices.otter.router.factory;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BetweenFactoryTest {
    private BetweenFactory<DummySession, DummyUser> subject;
    private Betweens<DummySession, DummyUser> csrfPrepare;
    private Betweens<DummySession, DummyUser> csrfProtect;
    private Betweens<DummySession, DummyUser> sessionRequired;
    private Betweens<DummySession, DummyUser> sessionOptional;

    @Before
    public void setUp() {
        csrfPrepare = FixtureFactory.makeBetweens();
        csrfProtect = FixtureFactory.makeBetweens();
        sessionRequired = FixtureFactory.makeBetweens();
        sessionOptional = FixtureFactory.makeBetweens();

        subject = new BetweenFactory<>(csrfPrepare, csrfProtect, sessionRequired, sessionOptional);
    }

    @Test
    public void makeWhenGetAndCSRF() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore(), is(csrfPrepare.getBefore()));
    }

    @Test
    public void makeWhenGetAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(sessionOptional.getBefore()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter(), is(sessionOptional.getAfter()));
    }

    @Test
    public void makeWhenGetAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

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

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCSRF() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(csrfProtect.getBefore()));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void makeWhenPostAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore(), is(sessionOptional.getBefore()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter(), is(sessionOptional.getAfter()));
    }

    @Test
    public void makeWhenPostAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

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

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF);
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }
}