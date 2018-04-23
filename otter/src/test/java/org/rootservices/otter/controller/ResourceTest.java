package org.rootservices.otter.controller;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import suite.UnitTest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@Category(UnitTest.class)
public class ResourceTest {
    private Resource subject;

    @Before
    public void setUp() {
        subject = new Resource();
    }

    @Test
    public void getShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.get(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));

    }

    @Test
    public void postShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.post(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void putShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.put(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void patchShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.patch(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void deleteShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.delete(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void connectShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.connect(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void optionsShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.options(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void traceShouldNotBeImplemented() {
        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        Response actual = subject.trace(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }
}