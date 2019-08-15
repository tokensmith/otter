package org.rootservices.hello.controller;


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
                .addHeader("Content-Type", "text/html")
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.NOT_FOUND.getCode()));
    }
}
