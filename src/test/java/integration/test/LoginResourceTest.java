package integration.test;


import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.serializer.JWTSerializer;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.security.csrf.CsrfClaims;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void getShouldReturn200() throws Exception {
        String loginURI = BASE_URI.toString() + "login";

        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(loginURI)
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

        // get the csrf value from the form.
        Pattern csrfPattern = Pattern.compile(".*\"csrfToken\" value=\"([^\"]*)\".*", Pattern.DOTALL);
        Matcher matcher = csrfPattern.matcher(response.getResponseBody());
        assertThat(matcher.matches(), is(true));
        String formCsrfValue = matcher.group(1);

        // cookie csrf value should match the form's value.
        AppFactory appFactory = new AppFactory();
        JWTSerializer jwtSerializer = appFactory.jwtSerializer();
        JsonWebToken jsonWebToken = jwtSerializer.stringToJwt(csrfCookie.getValue(), CsrfClaims.class);
        CsrfClaims claims = (CsrfClaims) jsonWebToken.getClaims();
        assertThat(claims.getChallengeToken(), is(formCsrfValue));
    }

    @Test
    public void postShouldReturn200() throws Exception {
        String loginURI = BASE_URI.toString() + "login";

        AsyncHttpClient httpClient = IntegrationTestSuite.getHttpClient();

        // this is the GET request to get the csrf cookie & form value
        ListenableFuture<Response> f = httpClient
                .prepareGet(loginURI)
                .execute();

        Response getResponse = f.get();

        // get the csrf value from the form.
        Pattern csrfPattern = Pattern.compile(".*\"csrfToken\" value=\"([^\"]*)\".*", Pattern.DOTALL);
        Matcher matcher = csrfPattern.matcher(getResponse.getResponseBody());
        assertThat(matcher.matches(), is(true));
        String formCsrfValue = matcher.group(1);

        List<Param> formData = new ArrayList<>();
        formData.add(new Param("email", "obi-wan@rootservices.org"));
        formData.add(new Param("password", "foo"));
        formData.add(new Param("csrfToken", formCsrfValue));

        // this is the POST request
        f = httpClient
                .preparePost(loginURI)
                .setFormParams(formData)
                .setCookies(getResponse.getCookies())
                .execute();

        Response postResponse = f.get();

        assertThat(postResponse.getStatusCode(), is(StatusCode.OK.getCode()));
    }
}
