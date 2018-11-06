package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.fake.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class LocationBuilderTest {
    private LocationBuilder<DummySession, DummyUser, EmptyPayload> subject;

    @Before
    public void setUp() {
        subject = new LocationBuilder<DummySession, DummyUser, EmptyPayload>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Location actual = subject.path(regex).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        FakeResource resource = new FakeResource();

        Location actual = subject.resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getRouteRunner(), is(notNullValue()));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypesShouldBeOK() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Location actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypeShouldBeOK() {
        String regex = "/foo/(.*)";
        MimeType json = new MimeTypeBuilder().json().build();
        FakeResource resource = new FakeResource();

        Location actual = subject.path(regex)
                .contentType(json)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRouteRunner(), is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(1));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void errorRouteRunnerShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        FakeResource errorResource = new FakeResource();

        Location actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .errorRouteRunner(StatusCode.NOT_FOUND, errorResource)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(1));
        assertThat(actual.getErrorRouteRunners().get(StatusCode.NOT_FOUND), is(notNullValue()));
    }

    @Test
    public void errorRouteRunnersShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Map<StatusCode, Route<DummySession, DummyUser, EmptyPayload>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Location actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .errorRouteRunners(errorRoutes)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(3));
    }

}