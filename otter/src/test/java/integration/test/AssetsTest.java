package integration.test;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.StatusCode;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;


import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@Category(ServletContainerTest.class)
public class AssetsTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    @Test
    public void getJavaScriptShouldReturn200() throws Exception {

        String helloURI = BASE_URI.toString() + "assets/js/jquery-3.3.1.min.js";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted to get js assets " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.OK.getCode()));
    }

    @Test
    public void getCssShouldReturn200() throws Exception {

        String helloURI = BASE_URI.toString() + "assets/bootstrap/css/bootstrap.min.css";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted to get css assets " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.OK.getCode()));
    }
}