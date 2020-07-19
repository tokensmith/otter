package net.tokensmith.hello.controller;


import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.header.ContentType;
import net.tokensmith.otter.controller.header.Header;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class GoodByeResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    @Test
    public void getShouldReturn200() throws Exception {

        String helloURI = BASE_URI.toString() + "goodbye";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.HTML_UTF_8.getValue())
                .addHeader(Header.ACCEPT.getValue(), ContentType.HTML_UTF_8.getValue())
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.OK.getCode()));

        // make sure jsp was executed.
        assertThat(response.getResponseBody().contains("<div id=\"good-bye\">Good Bye</>"), is(true));

    }

    @Test
    public void getWhenNoContentTypeShouldReturn415() throws Exception {

        String helloURI = BASE_URI.toString() + "goodbye";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.UNSUPPORTED_MEDIA_TYPE.getCode()));

        // make sure jsp was executed.
        assertThat(response.getResponseBody().contains("<div id=\"message\">Opps, we could process the request. It was an UnSupported Media Type.</div>"), is(true));
    }

    @Test
    public void getWhenNoContentTypeShouldReturn406() throws Exception {

        String helloURI = BASE_URI.toString() + "goodbye";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader(Header.CONTENT_TYPE.getValue(), ContentType.HTML_UTF_8.getValue())
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.NOT_ACCEPTABLE.getCode()));

        // make sure jsp was executed.
        assertThat(response.getResponseBody().contains("<div id=\"message\">Opps, we could process the request. It was because the browser sent the wrong accept header.</div>"), is(true));
    }

}