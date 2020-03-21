package suite;


import net.tokensmith.hello.controller.*;
import net.tokensmith.hello.controller.api.v2.BrokenRestResourceTest;
import net.tokensmith.hello.controller.api.v2.HelloCsrfRestResourceTest;
import net.tokensmith.hello.controller.api.v2.HelloSessionRestResourceTest;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import net.tokensmith.hello.controller.api.v2.HelloRestResourceTest;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import net.tokensmith.hello.controller.html.HelloResource;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.server.container.ServletContainer;
import net.tokensmith.otter.server.container.ServletContainerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@RunWith(Categories.class)
@Categories.IncludeCategory(ServletContainerTest.class)
@Suite.SuiteClasses(value = {
        HelloRestResourceTest.class,
        HelloSessionRestResourceTest.class,
        HelloCsrfRestResourceTest.class,
        BrokenRestResourceTest.class,
        net.tokensmith.hello.controller.api.v3.HelloRestResourceTest.class,
        net.tokensmith.hello.controller.api.v3.BrokenRestResourceTest.class,
        NotFoundResourceTest.class,
        HelloResourceTest.class,
        GoodByeResourceTest.class,
        LoginResourceTest.class,
        LoginSessionResourceTest.class,
        LoginSetSessionResourceTest.class,
        ProtectedResourceTest.class,
        RunTimeExceptionResourceTest.class,
        AssetsTest.class
})
public class IntegrationTestSuite {
    private static OtterAppFactory otterTestAppFactory;
    private static ServletContainerFactory servletContainerFactory;
    private static ServletContainer servletContainer;
    private static URI servletContainerURI;
    private static AsyncHttpClient httpClient;
    private static HttpClient httpClient2;
    private static String DOCUMENT_ROOT = "/";
    private static int RANDOM_PORT = 0;
    private static String REQUEST_LOG = "logs/jetty/jetty-test-yyyy_mm_dd.request.log";

    /**
     * Configures a servlet container then starts it.
     * Also assigns values to servletContainerURI and httpClient.
     *
     * @throws Exception
     */
    private static void configureAndStartServletContainer() throws Exception {

        otterTestAppFactory = new OtterAppFactory();
        servletContainerFactory = otterTestAppFactory.servletContainerFactory();

        List<ErrorPage> errorPages = new ArrayList<>();

        List<String> gzipMimeTypes = Arrays.asList(
                "text/html", "text/plain", "text/xml",
                "text/css", "application/javascript", "text/javascript",
                "application/json");

        servletContainer = servletContainerFactory.makeServletContainer(
                DOCUMENT_ROOT, HelloResource.class, RANDOM_PORT, REQUEST_LOG, gzipMimeTypes, errorPages
        );
        servletContainer.start();

        servletContainerURI = servletContainer.getURI();


        httpClient = asyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setCookieStore(null).build());

        httpClient2 = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    /**
     * Starts a servlet container and a spring container.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        configureAndStartServletContainer();
    }

    /**
     * Stops a servlet container
     *
     * @throws Exception
     */
    @AfterClass
    public static void afterClass() throws Exception {
        servletContainer.stop();
        httpClient.close();
    }

    /**
     * The servlet container to be used in tests.
     *
     * @return instance of ServletContainer
     */
    public static ServletContainer getServletContainer() {
        return servletContainer;
    }

    /**
     * Gets the URI of the servlet container.
     *
     * @return URI of the servlet container
     */
    public static URI getServletContainerURI() {
        return servletContainerURI;
    }

    /**
     * Gets the HTTP driver to make requests to the servlet container.
     *
     * @return instance of AsyncHttpClient
     */
    public static AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    public static HttpClient getHttpClient2() {
        return httpClient2;
    }
}
