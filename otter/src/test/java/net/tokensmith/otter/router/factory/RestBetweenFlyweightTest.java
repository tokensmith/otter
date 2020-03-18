package net.tokensmith.otter.router.factory;


import helper.FixtureFactory;
import helper.entity.*;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Before;
import org.junit.Test;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.builder.entity.RestBetweens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RestBetweenFlyweightTest {
    private RestBetweenFlyweight<DummySession, DummyUser> subject;
    private RestBetweens<DummySession, DummyUser> sessionRequired;
    private RestBetweens<DummySession, DummyUser> sessionOptional;
    private Optional<RestBetween<DummySession, DummyUser>> authRequired;
    private Optional<RestBetween<DummySession, DummyUser>> authOptional;

    @Before
    public void setUp() {
        sessionRequired = FixtureFactory.makeRestBetweens();
        sessionOptional = FixtureFactory.makeRestBetweens();
        authRequired = Optional.of(new DummyRestBetween<>());
        authOptional = Optional.of(new DummyRestBetween<>());

        subject = new RestBetweenFlyweight<DummySession, DummyUser>(
                sessionRequired,
                sessionOptional,
                authRequired,
                authOptional);
    }

    @Test
    public void makeWhenGetAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenGetAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenGetAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenGetAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenGetAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.get()));
    }

    @Test
    public void makeWhenGetAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.get()));
    }

    @Test
    public void makeWhenPostAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenPostAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenPostAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPostAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPostAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.get()));
    }

    @Test
    public void makeWhenPostAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.get()));
    }

    @Test
    public void makeWhenPutAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenPutAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenPutAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPutAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPutAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.get()));
    }

    @Test
    public void makeWhenPutAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.get()));
    }

    @Test
    public void makeWhenPatchAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenPatchAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
    }

    @Test
    public void makeWhenPatchAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPatchAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPatchAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.get()));
    }

    @Test
    public void makeWhenPatchAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.get()));
    }

    @Test
    public void makeWhenDeleteAndSessionOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
    }

    @Test
    public void makeWhenDeleteAndSessionRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
    }


    @Test
    public void makeWhenDeleteAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenDeleteAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenDeleteAndSessionOptionalAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_OPTIONAL);
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionOptional.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authOptional.get()));
    }

    @Test
    public void makeWhenDeleteAndSessionRequiredAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.SESSION_REQUIRED);
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummySession, DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(2));
        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getBefore().get(0), is(sessionRequired.getBefore().get(0)));
        assertThat(actual.getBefore().get(1), is(authRequired.get()));
    }
}