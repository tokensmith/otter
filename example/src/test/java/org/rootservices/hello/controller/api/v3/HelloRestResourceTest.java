package org.rootservices.hello.controller.api.v3;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.hello.controller.api.v3.model.BadRequestPayload;
import org.rootservices.hello.model.Hello;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.StatusCode;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class HelloRestResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    public String getUri() {
        return BASE_URI.toString() + "rest/v3/hello";
    }

    @Test
    public void getShouldReturn200() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.OK.getCode()));

        OtterAppFactory appFactory = new OtterAppFactory();
        ObjectMapper om = appFactory.objectMapper();
        Hello hello = om.readValue(response.getResponseBody(), Hello.class);

        assertThat(hello, is(notNullValue()));
        assertThat(hello.getMessage(), is(notNullValue()));
        assertThat(hello.getMessage(), is("Hello, Obi-Wan Kenobi"));
    }

    @Test
    public void postShouldReturn201() throws Exception {
        String helloURI = getUri();

        OtterAppFactory appFactory = new OtterAppFactory();
        ObjectMapper om = appFactory.objectMapper();
        Hello hello = new Hello("Hello World");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .setBody(om.writeValueAsString(hello))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.CREATED.getCode()));

        assertThat(hello, is(notNullValue()));
        assertThat(hello.getMessage(), is(notNullValue()));
        assertThat(hello.getMessage(), is("Hello World"));
    }


    @Test
    public void postWhenBodyEmptyShouldReturn400() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST.getCode()));

        OtterAppFactory appFactory = new OtterAppFactory();
        ObjectMapper om = appFactory.objectMapper();
        BadRequestPayload actual = om.readValue(response.getResponseBody(), BadRequestPayload.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("bad request"));
        assertThat(actual.getKey(), is(nullValue()));
        assertThat(actual.getReason(), is("The payload could not be parsed."));
    }

    @Test
    public void getWhenWrongContentTypeShouldReturn415() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/xml; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.UNSUPPORTED_MEDIA_TYPE.getCode()));
    }
}
