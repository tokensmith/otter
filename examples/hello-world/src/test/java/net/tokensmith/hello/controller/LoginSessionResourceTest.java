package net.tokensmith.hello.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import helper.FixtureFactory;
import io.netty.handler.codec.http.cookie.Cookie;
import net.tokensmith.hello.config.AppFactory;
import net.tokensmith.hello.security.TokenSession;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.jwe.entity.JWE;
import net.tokensmith.jwt.jwe.serialization.JweDeserializer;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.security.config.SecurityAppFactory;
import net.tokensmith.otter.security.csrf.CsrfClaims;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import suite.IntegrationTestSuite;
import suite.ServletContainerTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(ServletContainerTest.class)
public class LoginSessionResourceTest {
    private static String SUBJECT_URI;
    private static String SUBJECT_PATH = "login-with-session";

    @BeforeClass
    public static void beforeClass() {
        SUBJECT_URI = IntegrationTestSuite.getServletContainerURI().toString() + SUBJECT_PATH;
    }

    private Cookie getCookie(Response response, String cookieName) {
        Cookie cookie = null;
        for(Cookie c: response.getCookies()){
            if(cookieName.equals(c.name())) {
                cookie = c;
                break;
            }
        }
        return cookie;
    }

    @Test
    public void getShouldReturn200() throws Exception {
        Cookie sessionCookie = FixtureFactory.sessionCookie();
        ListenableFuture<Response> f = IntegrationTestSuite.getHttpClient()
                .prepareGet(SUBJECT_URI)
                .addHeader("Content-Type", "text/html")
                .addCookie(sessionCookie)
                .execute();

        Response response = f.get();

        String errorMsg = "Attempted GET " + SUBJECT_URI;
        assertThat(errorMsg, response.getStatusCode(), is(StatusCode.OK.getCode()));

        // there should be a csrf cookie
        Cookie csrfCookie = getCookie(response, "csrfToken");
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

        JsonWebToken<CsrfClaims> cookieJwt = jwtSerializer.stringToJwt(csrfCookie.value(), CsrfClaims.class);
        CsrfClaims cookieClaims = cookieJwt.getClaims();

        JsonWebToken<CsrfClaims> formJwt = jwtSerializer.stringToJwt(formCsrfValue, CsrfClaims.class);
        CsrfClaims formClaims = formJwt.getClaims();

        assertThat(cookieClaims.getChallengeToken(), is(formClaims.getChallengeToken()));
        assertThat(cookieClaims.getNoise(), is(not(formClaims.getNoise())));
    }

    @Test
    public void postShouldReturn200() throws Exception {

        Cookie csrfCookie = FixtureFactory.csrfCookie();
        Cookie sessionCookie = FixtureFactory.sessionCookie();
        AsyncHttpClient httpClient = IntegrationTestSuite.getHttpClient();

        // this is the GET request to get the csrf cookie & form value
        ListenableFuture<Response> f = httpClient
                .prepareGet(SUBJECT_URI)
                .addHeader("Content-Type", "text/html")
                .addCookie(sessionCookie)
                .addCookie(csrfCookie)
                .execute();

        Response getResponse = f.get();

        String errorMsg = "Attempted GET " + SUBJECT_URI;
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
                .preparePost(SUBJECT_URI)
                .setFormParams(formData)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8;")
                .setCookies(getResponse.getCookies())
                .execute();

        Response postResponse = f.get();

        errorMsg = "Attempted POST " + SUBJECT_URI;
        assertThat(errorMsg, postResponse.getStatusCode(), is(StatusCode.OK.getCode()));

        // should also have a session.
        Cookie actualSessionCookie = getCookie(getResponse, "session");

        assertThat(actualSessionCookie, is(notNullValue()));
        assertThat(actualSessionCookie.isHttpOnly(), is(true));
        assertThat(actualSessionCookie.maxAge(), is(-9223372036854775808L));
        // until the browser shutsdown.. dont ask me its teh sevlet api.
        assertThat(actualSessionCookie.name(), is("session"));


        // check session cookie value.
        AppFactory appConfig = new AppFactory();
        SymmetricKey encKey = appConfig.encKey();

        SecurityAppFactory securityAppFactory = new SecurityAppFactory();
        JwtAppFactory jwtAppFactory = securityAppFactory.jwtAppFactory();
        JweDeserializer deserializer = jwtAppFactory.jweDirectDesializer();
        JWE sessionPayload = deserializer.stringToJWE(sessionCookie.value(), encKey);

        TranslatorAppFactory translatorAppFactory = new TranslatorAppFactory();
        ObjectMapper om = translatorAppFactory.objectMapper();
        TokenSession actual = om.readValue(sessionPayload.getPayload(), TokenSession.class);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is(UUID.fromString("2cf081ed-aa7c-4141-b634-01fb56bc96bb")));
    }

    @Test
    public void postWhenNoCsrfCookieShouldReturn403() throws Exception {
        List<Param> formData = new ArrayList<>();
        formData.add(new Param("email", "obi-wan@tokensmith.net"));
        formData.add(new Param("password", "foo"));
        formData.add(new Param("csrfToken", "foo"));

        AsyncHttpClient httpClient = IntegrationTestSuite.getHttpClient();

        // this is the POST request
        ListenableFuture<Response> f = httpClient
                .preparePost(SUBJECT_URI)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8;")
                .setFormParams(formData)
                .execute();

        Response postResponse = f.get();

        String errorMsg = "Attempted POST " + SUBJECT_URI;
        assertThat(errorMsg, postResponse.getStatusCode(), is(StatusCode.FORBIDDEN.getCode()));
    }

    @Test
    public void postWhenNoSessionCookieShouldReturn401() throws Exception {
        Cookie sessionCookie = FixtureFactory.sessionCookie();
        AsyncHttpClient httpClient = IntegrationTestSuite.getHttpClient();

        // this is the GET request to get the csrf cookie & form value
        ListenableFuture<Response> f = httpClient
                .prepareGet(SUBJECT_URI)
                .addHeader("Content-Type", "text/html")
                .addCookie(sessionCookie)
                .execute();

        Response getResponse = f.get();

        String errorMsg = "Attempted GET " + SUBJECT_URI;
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

        // get the csrf cookie only.
        Cookie csrfCookie = getCookie(getResponse, "csrfToken");
        assertThat(csrfCookie, is(notNullValue()));

        // this is the POST request
        f = httpClient
                .preparePost(SUBJECT_URI)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8;")
                .setFormParams(formData)
                .addCookie(csrfCookie)
                .execute();

        Response postResponse = f.get();

        errorMsg = "Attempted POST " + SUBJECT_URI;
        assertThat(errorMsg, postResponse.getStatusCode(), is(StatusCode.UNAUTHORIZED.getCode()));
    }
}
