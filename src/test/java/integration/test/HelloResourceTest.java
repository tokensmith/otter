package integration.test;


import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.StatusCode;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class HelloResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    @Test
    public void getShouldReturn200() throws Exception {

        String helloURI = BASE_URI.toString() + "hello";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + helloURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.OK.getCode()));

        // make sure jsp was executed.
        assertThat(response.getResponseBody().contains("<div id=\"hello\">Hello World</>"), is(true));

    }

}