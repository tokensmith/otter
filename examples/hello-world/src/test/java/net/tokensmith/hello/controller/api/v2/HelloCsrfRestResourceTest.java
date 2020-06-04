package net.tokensmith.hello.controller.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import helper.FixtureFactory;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.hello.model.Hello;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class HelloCsrfRestResourceTest {

    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    public String getUri() {
        return BASE_URI.toString() + "rest/v2/csrf/hello";
    }

    @Test
    public void getShouldReturn200() throws Exception {
        Cookie csrfCookie = FixtureFactory.csrfCookie();
        String csrfHeader = FixtureFactory.csrfHeader();

        String helloURI = getUri();

        // reset http client b/c it holds onto cookies that should
        // be deleted.
        IntegrationTestSuite.configureHttpClient();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader("X-CSRF", csrfHeader)
                .addCookie(csrfCookie)
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
    public void getWhenNoCsrfCookieShouldReturnForbidden() throws Exception {
        String csrfHeader = FixtureFactory.csrfHeader();
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader("X-CSRF", csrfHeader)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN.getCode()));
    }

    @Test
    public void getWhenNoCsrfHeaderShouldReturnForbidden() throws Exception {
        Cookie csrfCookie = FixtureFactory.csrfCookie();
        String helloURI = getUri();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.JSON_UTF_8.getValue())
                .addCookie(csrfCookie)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.FORBIDDEN.getCode()));
    }

}