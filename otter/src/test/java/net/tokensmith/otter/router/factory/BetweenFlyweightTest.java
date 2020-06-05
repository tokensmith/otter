package net.tokensmith.otter.router.factory;

import helper.FixtureFactory;
import helper.entity.DummyBetween;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.security.builder.entity.Betweens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BetweenFlyweightTest {
    private BetweenFlyweight<DummySession, DummyUser> subject;
    private Betweens<DummySession, DummyUser> csrfPrepare;
    private Betweens<DummySession, DummyUser> csrfProtect;
    private Betweens<DummySession, DummyUser> sessionRequired;
    private Betweens<DummySession, DummyUser> sessionOptional;
    private Betweens<DummySession, DummyUser> authRequired;
    private Betweens<DummySession, DummyUser> authOptional;

    @Before
    public void setUp() {
        csrfPrepare = FixtureFactory.makeBetweens();
        csrfProtect = FixtureFactory.makeBetweens();
        sessionRequired = FixtureFactory.makeBetweens();
        sessionOptional = FixtureFactory.makeBetweens();
        authRequired = FixtureFactory.makeBetweens();
        authOptional = FixtureFactory.makeBetweens();

        Map<Label, List<Between<DummySession, DummyUser>>> labelBefore = new HashMap<>();
        Map<Label, List<Between<DummySession, DummyUser>>> labelAfter = new HashMap<>();

        labelBefore.put(Label.CSRF_PREPARE, csrfPrepare.getBefore());
        labelBefore.put(Label.CSRF_PROTECT, csrfProtect.getBefore());
        labelBefore.put(Label.SESSION_OPTIONAL, sessionOptional.getBefore());
        labelBefore.put(Label.SESSION_REQUIRED, sessionRequired.getBefore());
        labelBefore.put(Label.AUTH_OPTIONAL, authOptional.getBefore());
        labelBefore.put(Label.AUTH_REQUIRED, authRequired.getBefore());

        labelAfter.put(Label.SESSION_OPTIONAL, sessionOptional.getAfter());
        labelAfter.put(Label.SESSION_REQUIRED, sessionRequired.getAfter());
        labelAfter.put(Label.AUTH_OPTIONAL, authOptional.getAfter());
        labelAfter.put(Label.AUTH_REQUIRED, authRequired.getAfter());

        List<Between<DummySession, DummyUser>> befores = new ArrayList<>();
        befores.add(new DummyBetween<>());

        List<Between<DummySession, DummyUser>> afters = new ArrayList<>();
        afters.add(new DummyBetween<>());

        subject = new BetweenFlyweight<>(labelBefore, labelAfter, befores, afters);
    }

    @Test
    public void makeWhenGetAndCSRF() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PREPARE);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
    }

    @Test
    public void makeWhenGetAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));

    }

    @Test
    public void makeWhenGetAndCsrfAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PREPARE);
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PREPARE);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PREPARE);
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PREPARE);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfPrepare.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCSRF() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
    }

    @Test
    public void makeWhenPostAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));

    }

    @Test
    public void makeWhenPostAndCsrfAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
    }

    @Test
    public void makeWhenPutAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenPutAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenPatchAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenPatchAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenDeleteAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenDeleteAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        Betweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
    }
}
