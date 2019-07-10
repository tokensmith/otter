package org.rootservices.hello.controller.api.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.hello.controller.api.v3.model.ServerErrorPayload;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Category(ServletContainerTest.class)
public class BrokenRestResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    public String getUri() {
        return BASE_URI.toString() + "rest/v3/broken";
    }

    protected ServerErrorPayload to(byte[] from) throws Exception {
        OtterAppFactory appFactory = new OtterAppFactory();
        ObjectMapper om = appFactory.objectMapper();
        return om.readValue(from, ServerErrorPayload.class);
    }

    @Test
    public void getShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        // ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        // assertThat(payload.getMessage(), is("An internal server error occurred."));
    }

    @Test
    public void postShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is("An internal server error occurred."));
    }

    @Test
    public void putShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is("An internal server error occurred."));
    }

    @Test
    public void deleteShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is("An internal server error occurred."));
    }

    @Test
    public void patchShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is("An internal server error occurred."));
    }
}