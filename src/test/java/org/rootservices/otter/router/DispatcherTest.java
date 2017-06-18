package org.rootservices.otter.router;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class DispatcherTest {
    private Dispatcher subject;

    @Before
    public void setUp() {

        subject = new Dispatcher();
        List<Route> getRoutes = FixtureFactory.makeRoutes("get");
        subject.getGet().addAll(getRoutes);

        List<Route> postRoutes = FixtureFactory.makeRoutes("post");
        subject.getPost().addAll(postRoutes);

        List<Route> patchRoutes = FixtureFactory.makeRoutes("patch");
        subject.getPatch().addAll(patchRoutes);

        List<Route> putRoutes = FixtureFactory.makeRoutes("put");
        subject.getPut().addAll(putRoutes);

        List<Route> deleteRoutes = FixtureFactory.makeRoutes("delete");
        subject.getDelete().addAll(deleteRoutes);

        List<Route> connectRoutes = FixtureFactory.makeRoutes("connect");
        subject.getConnect().addAll(connectRoutes);

        List<Route> optionRoutes = FixtureFactory.makeRoutes("option");
        subject.getOptions().addAll(optionRoutes);

        List<Route> traceRoutes = FixtureFactory.makeRoutes("trace");
        subject.getTrace().addAll(traceRoutes);

        List<Route> headRoutes = FixtureFactory.makeRoutes("head");
        subject.getHead().addAll(headRoutes);
    }

    @Test
    public void findWhenGetAndBaseContextShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "get" + id.toString();

        Optional<MatchedRoute> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getRoute(), is(notNullValue()));
    }

    @Test
    public void findWhenGetAndBarShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "get" + id.toString() + "/bar";

        Optional<MatchedRoute> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getRoute(), is(notNullValue()));
    }

    @Test
    public void findWhenGetAndV2ShouldReturnNull() {
        UUID id = UUID.randomUUID();
        String url = "/get/v2/" + id.toString() + "/bar";

        Optional<MatchedRoute> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(false));
    }

}