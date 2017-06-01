package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.router.entity.Match;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class DispatcherTest {
    private Dispatcher subject;

    @Before
    public void setUp() {
        List<Route> routes = FixtureFactory.makeRoutes();
        subject = new Dispatcher();
        subject.getGet().addAll(routes);
        subject.getPost().addAll(routes);
        subject.getPatch().addAll(routes);
        subject.getPut().addAll(routes);
    }

    @Test
    public void findWhenGetAndFooShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "/api/v1/foo/" + id.toString();

        Optional<Match> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getRoute(), is(notNullValue()));
    }

    @Test
    public void findWhenFooBarShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "/api/v1/foo/" + id.toString() + "/bar";

        Optional<Match> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getRoute(), is(notNullValue()));
    }

    @Test
    public void findWhenV2ShouldReturnNull() {
        UUID id = UUID.randomUUID();
        String url = "/api/v2/foo/" + id.toString() + "/bar";

        Optional<Match> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(false));
    }

}