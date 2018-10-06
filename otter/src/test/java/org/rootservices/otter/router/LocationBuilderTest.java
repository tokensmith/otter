package org.rootservices.otter.router;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class LocationBuilderTest {
    private LocationBuilder<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        subject = new LocationBuilder<DummySession, DummyUser>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Location<DummySession, DummyUser> actual = subject.path(regex).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        FakeResource resource = new FakeResource();

        Location<DummySession, DummyUser> actual = subject.resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getBefore().size(), is(0));
        assertThat(actual.getRoute().getAfter().size(), is(0));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));

        assertThat(actual.getRouteRunner(), is(notNullValue()));

        assertThat(actual.getErrorRouteRunners(), is(notNullValue()));
        assertThat(actual.getErrorRouteRunners().size(), is(0));
    }

    @Test
    public void pathAndResourceAndContentTypesShouldBeOK() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Location<DummySession, DummyUser> actual = subject.path(regex)
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

    @Test
    public void pathAndResourceAndContentTypeShouldBeOK() {
        String regex = "/foo/(.*)";
        MimeType json = new MimeTypeBuilder().json().build();
        FakeResource resource = new FakeResource();

        Location<DummySession, DummyUser> actual = subject.path(regex)
                .contentType(json)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getRoute(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(notNullValue()));
        assertThat(actual.getRoute().getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(1));

        assertThat(actual.getErrorRoutes(), is(notNullValue()));
        assertThat(actual.getErrorRoutes().size(), is(0));
    }

    @Test
    public void errorResourceShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        FakeResource errorResource = new FakeResource();

        Location<DummySession, DummyUser> actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .errorResource(StatusCode.NOT_FOUND, errorResource)
                .build();

        assertThat(actual.getErrorRoutes().size(), is(1));
        assertThat(actual.getErrorRoutes().get(StatusCode.NOT_FOUND).getResource(), is(errorResource));

        Route<DummySession, DummyUser> actualErrorRoute = actual.getErrorRoutes().get(StatusCode.NOT_FOUND);

        assertThat(actualErrorRoute.getBefore(), is(notNullValue()));
        assertThat(actualErrorRoute.getBefore().size(), is(0));

        assertThat(actualErrorRoute.getAfter(), is(notNullValue()));
        assertThat(actualErrorRoute.getAfter().size(), is(0));
    }

    @Test
    public void errorRoutesShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Location<DummySession, DummyUser> actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .errorRoutes(errorRoutes)
                .build();

        assertThat(actual.getErrorRoutes().size(), is(3));
        assertThat(actual.getErrorRoutes(), is(errorRoutes));
    }

    @Test
    public void errorRouteRunnerShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        FakeResource errorResource = new FakeResource();

        Location<DummySession, DummyUser> actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .errorRouteRunner(StatusCode.NOT_FOUND, errorResource)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(1));
        assertThat(actual.getErrorRouteRunners().get(StatusCode.NOT_FOUND), is(notNullValue()));

        Route<DummySession, DummyUser> actualErrorRoute = actual.getErrorRoutes().get(StatusCode.NOT_FOUND);
    }

    @Test
    public void errorRouteRunnersShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = FixtureFactory.makeErrorRoutes();

        Location<DummySession, DummyUser> actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .errorRouteRunners(errorRoutes)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(3));
    }

}