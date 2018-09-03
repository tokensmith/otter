package org.rootservices.otter.controller;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class ResourceTest {
    private Resource<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        subject = new Resource<DummySession, DummyUser>();
    }

    @Test
    public void getShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.get(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));

    }

    @Test
    public void postShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.post(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void putShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.put(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void patchShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.patch(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void deleteShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.delete(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void connectShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.connect(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void optionsShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.options(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void traceShouldNotBeImplemented() {
        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        Response<DummySession> actual = subject.trace(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }
}