package net.tokensmith.hello.controller;

import net.tokensmith.otter.controller.entity.StatusCode;
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
public class RunTimeExceptionResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    public String getURI() {
        return BASE_URI.toString() + "exception";
    }

    @Test
    public void getShouldReturn500() throws Exception {

        String helloURI = getURI();

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .addHeader("Content-Type", "text/html")
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.SERVER_ERROR.getCode()));

        // make sure jsp was executed.
        assertThat(response.getResponseBody().contains("<div id=\"message\">Server Error</div>"), is(true));

    }
}