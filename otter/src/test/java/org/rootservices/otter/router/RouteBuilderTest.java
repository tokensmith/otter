package org.rootservices.otter.router;

import helper.entity.FakeResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.router.entity.Route;
import suite.UnitTest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@Category(UnitTest.class)
public class RouteBuilderTest {
    private RouteBuilder subject;

    @Before
    public void setUp() {
        subject = new RouteBuilder();
    }

    @Test
    public void pathShouldBeOK() {
        String regex = "/foo/(.*)";

        Route actual = subject.path(regex).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

    @Test
    public void resourceShouldBeOK() {
        FakeResource resource = new FakeResource();

        Route actual = subject.resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResource(), is(notNullValue()));
        assertThat(actual.getResource(), is(resource));
    }

    @Test
    public void pathAndResourceShouldBeOK() {
        String regex = "/foo/(.*)";
        FakeResource resource = new FakeResource();

        Route actual = subject.path(regex).resource(resource).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getResource(), is(notNullValue()));
        assertThat(actual.getResource(), is(resource));
        assertThat(actual.getPattern(), is(notNullValue()));
        assertThat(actual.getPattern().pattern(), is(regex));
    }

}