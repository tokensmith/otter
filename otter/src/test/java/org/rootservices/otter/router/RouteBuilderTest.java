package org.rootservices.otter.router;

import helper.entity.DummySession;
import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.Session;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class RouteBuilderTest {
    private RouteBuilder<DummySession> subject;

    @Before
    public void setUp() {
        subject = new RouteBuilder<DummySession>();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Route<DummySession> actual = subject.path(regex).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        FakeResource resource = new FakeResource();

        Route<DummySession> actual = subject.resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResource(), is(notNullValue()));
        assertThat(actual.getResource(), is(resource));
    }

    @Test
    public void pathAndResourceAndContentTypesShouldBeOK() {
        String regex = "/foo/(.*)";
        List<MimeType> contentTypes = new ArrayList<>();
        FakeResource resource = new FakeResource();

        Route actual = subject.path(regex)
                .contentTypes(contentTypes)
                .resource(resource)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResource(), is(notNullValue()));
        assertThat(actual.getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
        assertThat(actual.getContentTypes(), is(notNullValue()));
        assertThat(actual.getContentTypes().size(), is(0));
    }

}