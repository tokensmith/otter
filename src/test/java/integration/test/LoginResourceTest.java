package integration.test;


import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.StatusCode;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;
import suite.UnitTest;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class LoginResourceTest {
    private static URI BASE_URI;

    @BeforeClass
    public static void beforeClass() {
        BASE_URI = IntegrationTestSuite.getServletContainerURI();
    }

    @Test
    public void getShouldReturn404() throws Exception {
        String helloURI = BASE_URI.toString() + "login";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(helloURI)
                .execute();

        Response response = f.get();

        assertThat(response.getStatusCode(), is(StatusCode.OK.getCode()));

        // there should be a csrf cookie
        Cookie csrfCookie = null;
        for(Cookie cookie: response.getCookies()){
            if("csrf".equals(cookie.getName())) {
                csrfCookie = cookie;
                break;
            }
        }
        assertThat(csrfCookie, is(notNullValue()));

        // there should be a csrf challenge in the form
        String csrfInput =  "<input id=\"csrfToken\" type=\"hidden\" name=\"csrfToken\" value=\"";
        assertThat(response.getResponseBody().contains(csrfInput), is(true));
    }
}
