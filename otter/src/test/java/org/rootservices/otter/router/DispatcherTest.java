package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Location;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class DispatcherTest {
    private Dispatcher<DummySession, DummyUser> subject;

    @Before
    public void setUp() {

        subject = new Dispatcher<DummySession, DummyUser>();
        List<Location<DummySession, DummyUser>> getLocations = FixtureFactory.makeLocations("get");
        subject.getGet().addAll(getLocations);

        List<Location<DummySession, DummyUser>> postLocations = FixtureFactory.makeLocations("post");
        subject.getPost().addAll(postLocations);

        List<Location<DummySession, DummyUser>> patchLocations = FixtureFactory.makeLocations("patch");
        subject.getPatch().addAll(patchLocations);

        List<Location<DummySession, DummyUser>> putLocations = FixtureFactory.makeLocations("put");
        subject.getPut().addAll(putLocations);

        List<Location<DummySession, DummyUser>> deleteLocations = FixtureFactory.makeLocations("delete");
        subject.getDelete().addAll(deleteLocations);

        List<Location<DummySession, DummyUser>> connectLocations = FixtureFactory.makeLocations("connect");
        subject.getConnect().addAll(connectLocations);

        List<Location<DummySession, DummyUser>> optionLocations = FixtureFactory.makeLocations("option");
        subject.getOptions().addAll(optionLocations);

        List<Location<DummySession, DummyUser>> traceLocations = FixtureFactory.makeLocations("trace");
        subject.getTrace().addAll(traceLocations);

        List<Location<DummySession, DummyUser>> headLocations = FixtureFactory.makeLocations("head");
        subject.getHead().addAll(headLocations);
    }

    @Test
    public void findWhenGetAndBaseContextShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "get" + id.toString();

        Optional<MatchedLocation<DummySession, DummyUser>> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getLocation(), is(notNullValue()));
    }

    @Test
    public void findWhenGetAndBarShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "get" + id.toString() + "/bar";


        Optional<MatchedLocation<DummySession, DummyUser>> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getLocation(), is(notNullValue()));
    }

    @Test
    public void findWhenGetAndV2ShouldReturnNull() {
        UUID id = UUID.randomUUID();
        String url = "/get/v2/" + id.toString() + "/bar";

        Optional<MatchedLocation<DummySession, DummyUser>> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(false));
    }

}