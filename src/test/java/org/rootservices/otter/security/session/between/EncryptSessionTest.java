package org.rootservices.otter.security.session.between;

import helper.FixtureFactory;
import helper.entity.DummySession;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Method;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class EncryptSessionTest {
    private OtterAppFactory otterAppFactory = new OtterAppFactory();
    private EncryptSession subject;

    @Before
    public void setUp() {
        CookieConfig cookieConfig = new CookieConfig("session", true, -1);
        subject = new EncryptSession(
                cookieConfig,
                new JwtAppFactory(),
                otterAppFactory.urlDecoder(),
                FixtureFactory.encKey("1234"),
                otterAppFactory.objectMapper()
        );
    }

    @Test
    public void processShouldBeOk() throws Exception {
        DummySession session = new DummySession();
        session.setAccessToken("123456789");
        session.setRefreshToken("101112131415");

        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();
        response.setSession(Optional.of(session));

        subject.process(Method.GET, request, response);

        assertThat(response.getSession().isPresent(), is(true));

        Cookie actual = response.getCookies().get("session");

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("session"));
        assertThat(actual.getMaxAge(), is(-1));
        assertThat(actual.isSecure(), is(true));
        assertThat(actual.getValue(), is(notNullValue()));
    }

    @Test
    public void processWhenSessionEmptyShouldNotSetCookie() throws Exception {
        DummySession session = new DummySession();
        session.setAccessToken("123456789");
        session.setRefreshToken("101112131415");

        Request request = FixtureFactory.makeRequest();
        Response response = FixtureFactory.makeResponse();

        assertThat(response.getSession().isPresent(), is(false));

        subject.process(Method.GET, request, response);

        assertThat(response.getSession().isPresent(), is(false));

        Cookie actual = response.getCookies().get("session");
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void encryptShouldBeOk() throws Exception {
        DummySession session = new DummySession();
        session.setAccessToken("123456789");
        session.setRefreshToken("101112131415");

        ByteArrayOutputStream actual = subject.encrypt(session);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.toString().split("\\.").length, is(5));
    }
}