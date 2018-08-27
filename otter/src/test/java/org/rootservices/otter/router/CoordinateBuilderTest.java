package org.rootservices.otter.router;

import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.builder.CoordinateBuilder;
import org.rootservices.otter.router.entity.Coordinate;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class CoordinateBuilderTest {
    private CoordinateBuilder<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        subject = new CoordinateBuilder<DummySession, DummyUser>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Coordinate<DummySession, DummyUser> actual = subject.path(regex).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        FakeResource resource = new FakeResource();

        Coordinate<DummySession, DummyUser> actual = subject.resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypesShouldBeOK() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Coordinate actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

}