package net.tokensmith.hello.controller.api.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tokensmith.hello.controller.api.v3.model.ServerErrorPayload;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        TranslatorAppFactory appFactory = new TranslatorAppFactory();
        ObjectMapper om = appFactory.objectMapper();
        return om.readValue(from, ServerErrorPayload.class);
    }

    @Test
    public void getShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is("An internal server error occurred."));
    }

    @Test
    public void postShouldBeServerError() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        ServerErrorPayload payload = to(response.getResponseBodyAsBytes());
        assertThat(payload.getMessage(), is("An internal server error occurred."));
    }
}