package net.tokensmith.hello.controller.api.v2;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.tokensmith.hello.model.Hello;
import net.tokensmith.otter.controller.entity.Cause;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.error.rest.NotFoundRestResource;
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
        return BASE_URI.toString() + "rest/v2/hello";
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
    public void postShouldReturn400() throws Exception {
        String helloURI = getUri();

        ObjectMapper om = appFactory.objectMapper();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody("foo: bar")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST.getCode()));
        ClientError clientError = om.reader().forType(ClientError.class).readValue(response.getResponseBody());

        assertThat(clientError, is(notNullValue()));
    }

    @Test
    public void postWhenInvalidValueShouldReturn400() throws Exception {
        String helloURI = getUri();

        ObjectMapper om = appFactory.objectMapper();
        Hello hello = new Hello();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .setBody(om.writeValueAsString(hello))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.BAD_REQUEST.getCode()));
        ClientError clientError = om.reader().forType(ClientError.class).readValue(response.getResponseBody());

        assertThat(clientError, is(notNullValue()));

        Cause cause = clientError.getCauses().get(0);
        assertThat(cause.getSource(), is(Cause.Source.BODY));
        assertThat(cause.getKey(), is("message"));
        assertThat(cause.getActual(), is(nullValue()));
        assertThat(cause.getExpected(), is(notNullValue()));
        assertThat(cause.getExpected().size(), is(0));
        assertThat(cause.getReason(), is("must not be null"));
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

        Cause cause = clientError.getCauses().get(0);
        assertThat(cause.getSource(), is(Cause.Source.HEADER));
        assertThat(cause.getKey(), is(Header.CONTENT_TYPE.toString()));
        assertThat(cause.getActual(), is("application/xml; charset=utf-8;"));
        assertThat(cause.getExpected(), is(notNullValue()));
        assertThat(cause.getExpected().size(), is(1));
        assertThat(cause.getExpected().get(0), is("application/json; charset=utf-8;"));
        assertThat(cause.getReason(), is(nullValue()));
    }

    @Test
    public void getShouldReturn404() throws Exception {
        String helloURI = BASE_URI.toString() + "rest/v2/notFound";

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

        Cause cause = clientError.getCauses().get(0);
        assertThat(cause.getSource(), is(Cause.Source.URL));
        assertThat(cause.getKey(), is(nullValue()));
        assertThat(cause.getActual(), is("/rest/v2/notFound"));
        assertThat(cause.getExpected(), is(notNullValue()));
        assertThat(cause.getExpected().size(), is(0));
        assertThat(cause.getReason(), is(NotFoundRestResource.REASON));
    }
}
