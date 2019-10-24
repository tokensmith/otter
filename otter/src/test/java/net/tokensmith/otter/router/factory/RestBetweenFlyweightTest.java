package net.tokensmith.otter.router.factory;


import helper.entity.*;
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
    private RestBetweenFlyweight<DummyUser> subject;
    private Optional<RestBetween<DummyUser>> authRequired;
    private Optional<RestBetween<DummyUser>> authOptional;

    @Before
    public void setUp() {
        authRequired = Optional.of(new DummyRestBetween<>());
        authOptional = Optional.of(new DummyRestBetween<>());

        subject = new RestBetweenFlyweight<DummyUser>(authRequired, authOptional);
    }


    @Test
    public void makeWhenGetAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenGetAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummyUser> actual = subject.make(Method.GET, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPostAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPostAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummyUser> actual = subject.make(Method.POST, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPutAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPutAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummyUser> actual = subject.make(Method.PUT, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenPatchAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenPatchAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummyUser> actual = subject.make(Method.PATCH, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }

    @Test
    public void makeWhenDeleteAndAuthRequired() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_REQUIRED);

        RestBetweens<DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authRequired.get()));
    }

    @Test
    public void makeWhenDeleteAndAuthOptional() {
        List<Label> labels = new ArrayList<>();
        labels.add(Label.AUTH_OPTIONAL);

        RestBetweens<DummyUser> actual = subject.make(Method.DELETE, labels);

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getAfter().size(), is(0));
        assertThat(actual.getBefore().get(0), is(authOptional.get()));
    }
}