package org.rootservices.hello.controller.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.ServerError;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.error.ServerErrorRestResource;
import org.rootservices.otter.translator.config.TranslatorAppFactory;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class BrokenRestResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    public String getUri() {
        return BASE_URI.toString() + "rest/v2/broken";
    }

    protected ServerError to(byte[] from) throws Exception {
        TranslatorAppFactory appFactory = new TranslatorAppFactory();
        ObjectMapper om = appFactory.objectMapper();
        return om.readValue(from, ServerError.class);
    }

    @Test
    public void getShouldBeDefaultServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerError payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is(ServerErrorRestResource.RESPONSE_MESSAGE));
    }

    @Test
    public void postShouldBeDefaultServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerError payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is(ServerErrorRestResource.RESPONSE_MESSAGE));
    }

    @Test
    public void putShouldBeDefaultServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerError payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is(ServerErrorRestResource.RESPONSE_MESSAGE));
    }

    @Test
    public void deleteShouldBeDefaultServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerError payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is(ServerErrorRestResource.RESPONSE_MESSAGE));
    }

    @Test
    public void patchShouldBeDefaultServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerError payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is(ServerErrorRestResource.RESPONSE_MESSAGE));
    }
}