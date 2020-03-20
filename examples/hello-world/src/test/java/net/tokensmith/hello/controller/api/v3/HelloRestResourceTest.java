package net.tokensmith.hello.controller.api.v3;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.tokensmith.hello.controller.api.v3.model.BadRequestPayload;
import net.tokensmith.hello.model.Hello;
import net.tokensmith.otter.controller.entity.Cause;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.error.rest.NotFoundRestResource;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.zip.GZIPInputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Category(ServletContainerTest.class)
public class HelloRestResourceTest {
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private static URI BASE_URI;


    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    public String getUri() {
        return getUri("hello");
    }

    public String getUri(String context) {
        return BASE_URI.toString() + "rest/v3/" + context;
    }

    @Test
    public void getShouldReturn200() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .header(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
    public void getWhenH2AndAcceptGZipThenReturnGzipBody() throws Exception {
        String helloURI = getUri();

        HttpClient httpClient = IntegrationTestSuite.getHttpClient2();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(helloURI))
                .timeout(Duration.ofSeconds(2))
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .header(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .header("Accept-Encoding", "gzip")
                .build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        assertThat(response.statusCode(), is(StatusCode.OK.getCode()));
        assertThat(response.version(), is(HttpClient.Version.HTTP_2));
        assertThat(response.headers().firstValue("content-encoding").isPresent(), is(true));
        assertThat(response.headers().firstValue("content-encoding").get(), is("gzip"));

        // decompress the body.
        InputStream body = new GZIPInputStream(response.body());

        ObjectMapper om = appFactory.objectMapper();
        Hello hello = om.readValue(body, Hello.class);

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
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
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
    public void postWhenNoContentTypeAndBodyInvalidShouldReturn415() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody("invalid json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.UNSUPPORTED_MEDIA_TYPE.getCode()));

        ObjectMapper om = appFactory.objectMapper();
        ClientError actual = om.readValue(response.getResponseBody(), ClientError.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCauses().size(), is(1));


        assertThat(actual.getCauses().get(0).getSource(), is(Cause.Source.HEADER));
        assertThat(actual.getCauses().get(0).getKey(), is(Header.CONTENT_TYPE.toString()));
        assertThat(actual.getCauses().get(0).getActual(), is(nullValue()));
        assertThat(actual.getCauses().get(0).getExpected().size(), is(1));
        assertThat(actual.getCauses().get(0).getExpected().get(0), is(ContentType.JSON_UTF_8.getValue()));
    }

    @Test
    public void postWhenNoAcceptAndBodyInvalidShouldReturn415() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody("invalid json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_ACCEPTABLE.getCode()));

        ObjectMapper om = appFactory.objectMapper();
        ClientError actual = om.readValue(response.getResponseBody(), ClientError.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCauses().size(), is(1));
        assertThat(actual.getCauses().get(0).getSource(), is(Cause.Source.HEADER));
        assertThat(actual.getCauses().get(0).getKey(), is(Header.ACCEPT.toString()));
        assertThat(actual.getCauses().get(0).getActual(), is(nullValue()));
        assertThat(actual.getCauses().get(0).getExpected().size(), is(1));
        assertThat(actual.getCauses().get(0).getExpected().get(0), is(ContentType.JSON_UTF_8.getValue()));
    }

    @Test
    public void postWhenNotFoundAndBodyInvalidShouldReturn404() throws Exception {
        String helloURI = getUri("not-wired-up");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody("invalid json")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));

        ObjectMapper om = appFactory.objectMapper();
        ClientError actual = om.readValue(response.getResponseBody(), ClientError.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCauses().size(), is(1));

        assertThat(actual.getCauses().get(0).getSource(), is(Cause.Source.URL));
        assertThat(actual.getCauses().get(0).getActual(), is("/rest/v3/not-wired-up"));
        assertThat(actual.getCauses().get(0).getExpected().size(), is(0));
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
        assertThat(clientError.getCauses().size(), is(1));
        assertThat(clientError.getCauses().get(0).getSource(), is(Cause.Source.HEADER));
        assertThat(clientError.getCauses().get(0).getKey(), is(Header.CONTENT_TYPE.toString()));
        assertThat(clientError.getCauses().get(0).getActual(), is("application/xml; charset=utf-8;"));
        assertThat(clientError.getCauses().get(0).getExpected(), is(notNullValue()));
        assertThat(clientError.getCauses().get(0).getExpected().size(), is(1));
        assertThat(clientError.getCauses().get(0).getExpected().get(0), is("application/json; charset=utf-8;"));
        assertThat(clientError.getCauses().get(0).getReason(), is(nullValue()));
    }

    @Test
    public void getWhenNoAcceptHeaderShouldReturnDefault406() throws Exception {
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_ACCEPTABLE.getCode()));

        ObjectMapper om = appFactory.objectMapper();
        ClientError clientError = om.readValue(response.getResponseBody(), ClientError.class);
        assertThat(clientError, is(notNullValue()));
        assertThat(clientError.getCauses().size(), is(1));
        assertThat(clientError.getCauses().get(0).getSource(), is(Cause.Source.HEADER));
        assertThat(clientError.getCauses().get(0).getKey(), is(Header.ACCEPT.toString()));
        assertThat(clientError.getCauses().get(0).getActual(), is(nullValue()));
        assertThat(clientError.getCauses().get(0).getExpected(), is(notNullValue()));
        assertThat(clientError.getCauses().get(0).getExpected().size(), is(1));
        assertThat(clientError.getCauses().get(0).getExpected().get(0), is("application/json; charset=utf-8;"));
        assertThat(clientError.getCauses().get(0).getReason(), is(nullValue()));
    }

    @Test
    public void getShouldReturn404() throws Exception {
        String helloURI = BASE_URI.toString() + "rest/v3/notFound";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));

        ObjectMapper om = appFactory.objectMapper();
        ClientError clientError = om.readValue(response.getResponseBody(), ClientError.class);
        assertThat(clientError, is(notNullValue()));
        assertThat(clientError.getCauses().size(), is(1));
        assertThat(clientError.getCauses().get(0).getSource(), is(Cause.Source.URL));
        assertThat(clientError.getCauses().get(0).getKey(), is(nullValue()));
        assertThat(clientError.getCauses().get(0).getActual(), is("/rest/v3/notFound"));
        assertThat(clientError.getCauses().get(0).getExpected(), is(notNullValue()));
        assertThat(clientError.getCauses().get(0).getExpected().size(), is(0));
        assertThat(clientError.getCauses().get(0).getReason(), is(NotFoundRestResource.REASON));
    }
}
