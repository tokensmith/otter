package org.rootservices.hello.controller.api.v3;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.hello.controller.api.v3.model.BadRequestPayload;
import org.rootservices.hello.model.Hello;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.ClientError;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.translator.config.TranslatorAppFactory;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class HelloRestResourceTest {
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
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


        ObjectMapper om = appFactory.objectMapper();
        Hello hello = om.readValue(response.getResponseBody(), Hello.class);

        assertThat(hello, is(notNullValue()));
        assertThat(hello.getMessage(), is(notNullValue()));
        assertThat(hello.getMessage(), is("Hello, Obi-Wan Kenobi"));
    }

    @Test
    public void getWhenH2ShouldReturn200() throws Exception {
        String helloURI = getUri();

        HttpClient httpClient = IntegrationTestSuite.getHttpClient2();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(helloURI))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json; charset=utf-8;")
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        assertThat(response.statusCode(), is(StatusCode.OK.getCode()));
        assertThat(response.version(), is(HttpClient.Version.HTTP_2));

        ObjectMapper om = appFactory.objectMapper();
        Hello hello = om.readValue(response.body(), Hello.class);

        assertThat(hello, is(notNullValue()));
        assertThat(hello.getMessage(), is(notNullValue()));
        assertThat(hello.getMessage(), is("Hello, Obi-Wan Kenobi"));
    }

    @Test
    public void postShouldReturn201() throws Exception {
        String helloURI = getUri();

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

        ObjectMapper om = appFactory.objectMapper();
        BadRequestPayload actual = om.readValue(response.getResponseBody(), BadRequestPayload.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMessage(), is("bad request"));
        assertThat(actual.getKey(), is(nullValue()));
        assertThat(actual.getReason(), is("The payload could not be parsed."));
    }

    @Test
    public void getWhenWrongContentTypeShouldReturnDefault415() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/xml; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.UNSUPPORTED_MEDIA_TYPE.getCode()));
        ObjectMapper om = appFactory.objectMapper();
        ClientError clientError = om.readValue(response.getResponseBody(), ClientError.class);
        assertThat(clientError, is(notNullValue()));
        assertThat(clientError.getSource(), is(ClientError.Source.HEADER));
        assertThat(clientError.getKey(), is(Header.CONTENT_TYPE.toString()));
        assertThat(clientError.getActual(), is("application/xml; charset=utf-8;"));
        assertThat(clientError.getExpected(), is(notNullValue()));
        assertThat(clientError.getExpected().size(), is(1));
        assertThat(clientError.getExpected().get(0), is("application/json; charset=utf-8;"));
        assertThat(clientError.getReason(), is(nullValue()));
    }

    @Test
    public void getShouldReturn404() throws Exception {
        String helloURI = BASE_URI.toString() + "rest/v3/notFound";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "application/json; charset=utf-8;")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));

        ObjectMapper om = appFactory.objectMapper();
        ClientError clientError = om.readValue(response.getResponseBody(), ClientError.class);
        assertThat(clientError, is(notNullValue()));
        assertThat(clientError.getSource(), is(ClientError.Source.URL));
        assertThat(clientError.getKey(), is(nullValue()));
        assertThat(clientError.getActual(), is("/rest/v3/notFound"));
        assertThat(clientError.getExpected(), is(notNullValue()));
        assertThat(clientError.getExpected().size(), is(0));
        assertThat(clientError.getReason(), is(nullValue()));
    }
}
