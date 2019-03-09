package suite;


import hello.controller.*;
import hello.controller.api.v1.HelloLegacyRestResourceTest;
import hello.controller.api.v2.HelloRestResourceTest;
import hello.controller.api.v3.BrokenRestResourceTest;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.server.container.ServletContainerFactory;

import java.net.URI;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@RunWith(Categories.class)
@Categories.IncludeCategory(ServletContainerTest.class)
@Suite.SuiteClasses(value = {
        HelloLegacyRestResourceTest.class,
        HelloRestResourceTest.class,
        hello.controller.api.v3.HelloRestResourceTest.class,
        BrokenRestResourceTest.class,
        NotFoundResourceTest.class,
        HelloResourceTest.class,
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

        String webAppLocation = "/src/test/java/integration/app/webapp";

        // servletContainer = servletContainerFactory.makeServletContainer(DOCUMENT_ROOT, HelloResource.class, webAppLocation, RANDOM_PORT, REQUEST_LOG);
        servletContainer = servletContainerFactory.makeServletContainer(DOCUMENT_ROOT, HelloResource.class, RANDOM_PORT, REQUEST_LOG);
        servletContainer.start();

        servletContainerURI = servletContainer.getURI();

        httpClient = asyncHttpClient(new DefaultAsyncHttpClientConfig.Builder().setCookieStore(null).build());
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
}
