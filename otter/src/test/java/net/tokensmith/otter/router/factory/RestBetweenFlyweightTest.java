package net.tokensmith.otter.router.factory;


import helper.FixtureFactory;
import helper.entity.DummyRestBetween;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RestBetweenFlyweightTest {
    private RestBetweenFlyweight<DummySession, DummyUser> subject;
    private RestBetweens<DummySession, DummyUser> sessionRequired;
    private RestBetweens<DummySession, DummyUser> sessionOptional;
    private RestBetweens<DummySession, DummyUser> csrfProtect;
    private RestBetweens<DummySession, DummyUser> authRequired;
    private RestBetweens<DummySession, DummyUser> authOptional;
    private DummyRestBetween<DummySession, DummyUser> before;
    private DummyRestBetween<DummySession, DummyUser> after;

    @Before
    public void setUp() {
        sessionRequired = FixtureFactory.makeRestBetweens();
        sessionOptional = FixtureFactory.makeRestBetweens();
        csrfProtect = FixtureFactory.makeRestBetweens();
        authRequired = FixtureFactory.makeRestBetweens();
        authOptional = FixtureFactory.makeRestBetweens();

        Map<Label, List<RestBetween<DummySession, DummyUser>>> labelBefore = new HashMap<>();
        Map<Label, List<RestBetween<DummySession, DummyUser>>> labelAfter = new HashMap<>();

        labelBefore.put(Label.CSRF_PROTECT, csrfProtect.getBefore());
        labelBefore.put(Label.SESSION_OPTIONAL, sessionOptional.getBefore());
        labelBefore.put(Label.SESSION_REQUIRED, sessionRequired.getBefore());
        labelBefore.put(Label.AUTH_OPTIONAL, authOptional.getBefore());
        labelBefore.put(Label.AUTH_REQUIRED, authRequired.getBefore());

        labelAfter.put(Label.SESSION_OPTIONAL, sessionOptional.getAfter());
        labelAfter.put(Label.SESSION_REQUIRED, sessionRequired.getAfter());
        labelAfter.put(Label.AUTH_OPTIONAL, authOptional.getAfter());
        labelAfter.put(Label.AUTH_REQUIRED, authRequired.getAfter());

        List<RestBetween<DummySession, DummyUser>> befores = new ArrayList<>();
        before = new DummyRestBetween<>();
        befores.add(before);

        List<RestBetween<DummySession, DummyUser>> afters = new ArrayList<>();
        after = new DummyRestBetween<>();
        afters.add(after);

        subject = new RestBetweenFlyweight<DummySession, DummyUser>(labelBefore, labelAfter, befores, afters);
    }

    @Test
    public void makeWhenGetAndCsrf() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenGetAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenGetAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenGetAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenGetAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenGetAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenGetAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenGetAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPostAndCsrf() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPostAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPostAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPostAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPostAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPostAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(3));

        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPostAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPostAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPutAndCsrf() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }


    @Test
    public void makeWhenPutAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPutAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPutAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPutAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPutAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenPutAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPutAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPutAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPatchAndCsrf() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }


    @Test
    public void makeWhenPatchAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPatchAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPatchAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPatchAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenPatchAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPatchAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPatchAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenPatchAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenDeleteAndCsrf() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }


    @Test
    public void makeWhenDeleteAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenDeleteAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }


    @Test
    public void makeWhenDeleteAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(before));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenDeleteAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getBefore().get(0), is(authOptional.getBefore().get(0)));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(after));
    }

    @Test
    public void makeWhenDeleteAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenDeleteAndCsrfAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionOptional.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenDeleteAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(3));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }

    @Test
    public void makeWhenDeleteAndCsrfAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.CSRF_PROTECT);
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(4));
        assertThat(actual.getBefore().get(0), is(csrfProtect.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(2), is(authRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(3), is(before));

        assertThat(actual.getAfter().size(), is(2));
        assertThat(actual.getAfter().get(0), is(sessionRequired.getAfter().get(0)));
        assertThat(actual.getAfter().get(1), is(after));
    }
}