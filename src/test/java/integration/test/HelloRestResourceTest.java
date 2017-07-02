package integration.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import integration.app.hello.config.AppConfig;
import integration.app.hello.model.Hello;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.translator.JsonTranslator;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class HelloRestResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    @Test
    public void getShouldReturn200() throws Exception {
        String helloURI = BASE_URI.toString() + "rest/hello";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.OK.getCode()));

        AppFactory appFactory = new AppFactory();
        ObjectMapper om = appFactory.objectMapper();
        Hello hello = om.readValue(response.getResponseBody(), Hello.class);

        assertThat(hello, is(notNullValue()));
        assertThat(hello.getMessage(), is(notNullValue()));
        assertThat(hello.getMessage(), is("Hello World"));
    }

    @Test
    public void postShouldReturn201() throws Exception {
        String helloURI = BASE_URI.toString() + "rest/hello";

        AppFactory appFactory = new AppFactory();
        ObjectMapper om = appFactory.objectMapper();
        Hello hello = new Hello("Hello World");

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .preparePost(helloURI)
                .setBody(om.writeValueAsString(hello))
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.CREATED.getCode()));

        assertThat(hello, is(notNullValue()));
        assertThat(hello.getMessage(), is(notNullValue()));
        assertThat(hello.getMessage(), is("Hello World"));
    }
}