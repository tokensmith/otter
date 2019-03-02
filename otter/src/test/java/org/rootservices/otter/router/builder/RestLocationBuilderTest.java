package org.rootservices.otter.router.builder;

import helper.FixtureFactory;
import helper.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.translatable.Translatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class RestLocationBuilderTest {
    private RestLocationBuilder<DummyUser, DummyPayload> subject;

    @Before
    public void setUp() {
        subject = new RestLocationBuilder<DummyUser, DummyPayload>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        OkRestResource resource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .restResource(resource)
                .build();

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
        OkRestResource resource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentTypes(contentTypes)
                .restResource(resource)
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
        OkRestResource resource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentType(json)
                .restResource(resource)
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
        OkRestResource resource = new OkRestResource();

        OkRestResource errorResource = new OkRestResource();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentTypes(contentTypes)
                .restResource(resource)
                .errorRouteRunner(StatusCode.NOT_FOUND, errorResource)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(1));
        assertThat(actual.getErrorRouteRunners().get(StatusCode.NOT_FOUND), is(notNullValue()));
    }

    @Test
    public void errorRouteRunnersShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        OkRestResource resource = new OkRestResource();

        Map<StatusCode, RestRoute<DummyUser, DummyPayload>> errorRoutes = FixtureFactory.makeErrorRestRoutes();

        Location actual = subject
                .payload(DummyPayload.class)
                .path(regex)
                .contentTypes(contentTypes)
                .restResource(resource)
                .errorRouteRunners(errorRoutes)
                .build();

        assertThat(actual.getErrorRouteRunners().size(), is(3));
    }

    @Test
    public void errorHandlersShouldBeOk() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        OkRestResource resource = new OkRestResource();

        Map<StatusCode, RestError<DummyUser, ? extends Translatable>> errors = new HashMap<>();
        RestError<DummyUser, ? extends Translatable> restError = new RestError<>(
                DummyErrorPayload.class, new ClientErrorRestResource()
        );
        errors.put(StatusCode.BAD_REQUEST, restError);

        Location actual = subject
            .payload(DummyPayload.class)
            .path(regex)
            .contentTypes(contentTypes)
            .restResource(resource)
            .restErrorResources(errors)
            .build();

        // no interface exposed to ensure it was built accurately.
        // this is good enough for now
        assertThat(actual, is(notNullValue()));

    }
}