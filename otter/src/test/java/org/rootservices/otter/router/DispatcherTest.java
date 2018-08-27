package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.router.entity.MatchedCoordinate;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Coordinate;


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
        List<Coordinate<DummySession, DummyUser>> getCoordinates = FixtureFactory.makeCoordinates("get");
        subject.getGet().addAll(getCoordinates);

        List<Coordinate<DummySession, DummyUser>> postCoordinates = FixtureFactory.makeCoordinates("post");
        subject.getPost().addAll(postCoordinates);

        List<Coordinate<DummySession, DummyUser>> patchCoordinates = FixtureFactory.makeCoordinates("patch");
        subject.getPatch().addAll(patchCoordinates);

        List<Coordinate<DummySession, DummyUser>> putCoordinates = FixtureFactory.makeCoordinates("put");
        subject.getPut().addAll(putCoordinates);

        List<Coordinate<DummySession, DummyUser>> deleteCoordinates = FixtureFactory.makeCoordinates("delete");
        subject.getDelete().addAll(deleteCoordinates);

        List<Coordinate<DummySession, DummyUser>> connectCoordinates = FixtureFactory.makeCoordinates("connect");
        subject.getConnect().addAll(connectCoordinates);

        List<Coordinate<DummySession, DummyUser>> optionCoordinates = FixtureFactory.makeCoordinates("option");
        subject.getOptions().addAll(optionCoordinates);

        List<Coordinate<DummySession, DummyUser>> traceCoordinates = FixtureFactory.makeCoordinates("trace");
        subject.getTrace().addAll(traceCoordinates);

        List<Coordinate<DummySession, DummyUser>> headCoordinates = FixtureFactory.makeCoordinates("head");
        subject.getHead().addAll(headCoordinates);
    }

    @Test
    public void findWhenGetAndBaseContextShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "get" + id.toString();

        Optional<MatchedCoordinate<DummySession, DummyUser>> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getCoordinate(), is(notNullValue()));
    }

    @Test
    public void findWhenGetAndBarShouldReturnMatch() {
        UUID id = UUID.randomUUID();
        String url = "get" + id.toString() + "/bar";


        Optional<MatchedCoordinate<DummySession, DummyUser>> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(true));

        assertThat(actual.get().getMatcher(), is(notNullValue()));
        assertThat(actual.get().getMatcher().groupCount(), is(1));
        assertThat(actual.get().getMatcher().group(0), is(url));
        assertThat(actual.get().getMatcher().group(1), is(id.toString()));
        assertThat(actual.get().getCoordinate(), is(notNullValue()));
    }

    @Test
    public void findWhenGetAndV2ShouldReturnNull() {
        UUID id = UUID.randomUUID();
        String url = "/get/v2/" + id.toString() + "/bar";

        Optional<MatchedCoordinate<DummySession, DummyUser>> actual = subject.find(Method.GET, url);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.isPresent(), is(false));
    }

}