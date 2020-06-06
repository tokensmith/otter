package net.tokensmith.hello.controller;


import io.netty.handler.codec.http.cookie.Cookie;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.security.csrf.CsrfClaims;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.not;
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
                .addHeader("Content-Type", "text/html")
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + loginURI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.OK.getCode()));

        // there should be a csrf cookie
        Cookie csrfCookie = null;
        for(Cookie cookie: response.getCookies()){
            if("csrfToken".equals(cookie.name())) {
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
        JwtAppFactory jwtAppFactory = new JwtAppFactory();
        JwtSerde jwtSerializer = jwtAppFactory.jwtSerde();
        JsonWebToken cookieJwt = jwtSerializer.stringToJwt(csrfCookie.value(), CsrfClaims.class);
        CsrfClaims cookieClaims = (CsrfClaims) cookieJwt.getClaims();

        JsonWebToken formJwt = jwtSerializer.stringToJwt(formCsrfValue, CsrfClaims.class);
        CsrfClaims formClaims = (CsrfClaims) formJwt.getClaims();

        assertThat(cookieClaims.getChallengeToken(), is(formClaims.getChallengeToken()));
        assertThat(cookieClaims.getNoise(), is(not(formClaims.getNoise())));
    }

    @Test
    public void postShouldReturn200() throws Exception {
        String loginURI = BASE_URI.toString() + "login";

        AsyncHttpClient httpClient = IntegrationTestSuite.getHttpClient();

        // this is the GET request to get the csrf cookie & form value
        ListenableFuture<Response> f = httpClient
                .prepareGet(loginURI)
                .addHeader("Content-Type", "text/html")
                .execute();

        Response getResponse = f.get();

        String errorMsg = "Attempted GET " + loginURI;
        assertThat(errorMsg, getResponse.getStatusCode(), is(StatusCode.OK.getCode()));

        // get the csrf value from the form.
        Pattern csrfPattern = Pattern.compile(".*\"csrfToken\" value=\"([^\"]*)\".*", Pattern.DOTALL);
        Matcher matcher = csrfPattern.matcher(getResponse.getResponseBody());
        assertThat(matcher.matches(), is(true));
        String formCsrfValue = matcher.group(1);

        List<Param> formData = new ArrayList<>();
        formData.add(new Param("email", "obi-wan@tokensmith.net"));
        formData.add(new Param("password", "foo"));
        formData.add(new Param("csrfToken", formCsrfValue));

        // this is the POST request
        f = httpClient
                .preparePost(loginURI)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8;")
                .setFormParams(formData)
                .setCookies(getResponse.getCookies())
                .execute();

        Response postResponse = f.get();

        errorMsg = "Attempted POST " + loginURI;
        assertThat(errorMsg, postResponse.getStatusCode(), is(StatusCode.OK.getCode()));
    }

    @Test
    public void postWhenNoCSRFCookieShouldReturn403() throws Exception {
        String loginURI = BASE_URI.toString() + "login";

        List<Param> formData = new ArrayList<>();
        formData.add(new Param("email", "obi-wan@tokensmith.net"));
        formData.add(new Param("password", "foo"));
        formData.add(new Param("csrfToken", "foo"));

        AsyncHttpClient httpClient = IntegrationTestSuite.getHttpClient();

        // this is the POST request
        ListenableFuture<Response> f = httpClient
                .preparePost(loginURI)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8;")
                .setFormParams(formData)
                .execute();

        Response postResponse = f.get();

        String errorMsg = "Attempted POST " + loginURI;
        assertThat(errorMsg, postResponse.getStatusCode(), is(StatusCode.FORBIDDEN.getCode()));
    }
}
