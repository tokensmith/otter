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
public class NotFoundResourceTest {
    private static URI documentRoot;

    @BeforeClass
    public static void beforeClass() {
        documentRoot = IntegrationTestSuite.getServletContainerURI();
    }

    @Test
    public void getWhenPathIsNotMappedShouldReturn404() throws Exception {
        String notFoundURI = documentRoot.toString() + "notFound";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(notFoundURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));
    }
}
