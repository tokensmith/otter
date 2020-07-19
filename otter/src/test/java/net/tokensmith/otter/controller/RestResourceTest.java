package net.tokensmith.otter.controller;

import helper.FixtureFactory;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RestResourceTest {
    RestResource<DummyUser, DummyPayload> subject;

    @Before
    public void setUp() throws Exception {
        subject = new RestResource<>();
    }

    @Test
    public void getShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.get(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));

    }

    @Test
    public void postShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.post(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void putShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.put(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void patchShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.patch(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void deleteShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.delete(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void connectShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.connect(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void optionsShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.options(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }

    @Test
    public void traceShouldNotBeImplemented() {
        RestRequest<DummyUser, DummyPayload> request = FixtureFactory.makeRestRequest();
        RestResponse<DummyPayload> response = FixtureFactory.makeRestResponse();

        RestResponse<DummyPayload> actual = subject.trace(request, response);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.NOT_IMPLEMENTED));
    }
}