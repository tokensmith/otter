package org.rootservices.otter.security.session.between;


import helper.FixtureFactory;
import helper.entity.DummySession;

import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.session.between.exception.EncryptSessionException;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class EncryptSessionTest {
    private OtterAppFactory otterAppFactory = new OtterAppFactory();
    private EncryptSession<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        CookieConfig cookieConfig = new CookieConfig("session", true, -1);
        subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                FixtureFactory.encKey("1234"),
                otterAppFactory.objectWriter()
        );
    }

    @Test
    public void processShouldSetSession() throws Exception {
        DummySession requestSession = new DummySession();
        requestSession.setAccessToken("123456789");
        requestSession.setRefreshToken("101112131415");

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setSession(Optional.of(requestSession));

        DummySession responseSession = new DummySession(requestSession);
        // change the response session so it will re-encrypt.
        responseSession.setAccessToken("1617181920");

        Response<DummySession> response = FixtureFactory.makeResponse();
        response.setSession(Optional.of(responseSession));

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
    public void processWhenEncryptSessionExceptionShouldHalt() throws Exception {

        // force Halt with a bad key to encrypt with.
        SymmetricKey veryBadKey = FixtureFactory.encKey("1234");
        veryBadKey.setKey("MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--");

        CookieConfig cookieConfig = new CookieConfig("session", true, -1);
        EncryptSession<DummySession, DummyUser> subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                veryBadKey,
                otterAppFactory.objectWriter()
        );

        DummySession requestSession = new DummySession();
        requestSession.setAccessToken("123456789");
        requestSession.setRefreshToken("101112131415");

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setSession(Optional.of(requestSession));

        Response<DummySession> response = FixtureFactory.makeResponse();
        DummySession responseSession = new DummySession(requestSession);
        responseSession.setAccessToken("1617181920");
        response.setSession(Optional.of(responseSession));

        HaltException actual = null;
        try {
            subject.process(Method.GET, request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), instanceOf(EncryptSessionException.class));

        assertThat(response.getSession().isPresent(), is(true));
        assertThat(response.getCookies().get("session"), is(nullValue()));
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

    @Test
    public void shouldEncryptWhenSessionsDiffShouldReturnTrue() {
        DummySession requestSession = new DummySession();
        requestSession.setAccessToken("123456789");
        requestSession.setRefreshToken("101112131415");

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setSession(Optional.of(requestSession));

        DummySession responseSession = new DummySession(requestSession);
        // change the response session so it will re-encrypt.
        responseSession.setAccessToken("1617181920");

        Response<DummySession> response = FixtureFactory.makeResponse();
        response.setSession(Optional.of(responseSession));

        Boolean actual = subject.shouldEncrypt(request, response);

        assertThat(actual, is(true));
    }

    @Test
    public void shouldEncryptWhenSessionsEqualShouldReturnFalse() {
        DummySession requestSession = new DummySession();
        requestSession.setAccessToken("123456789");
        requestSession.setRefreshToken("101112131415");

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setSession(Optional.of(requestSession));

        DummySession responseSession = new DummySession(requestSession);

        Response<DummySession> response = FixtureFactory.makeResponse();
        response.setSession(Optional.of(responseSession));

        Boolean actual = subject.shouldEncrypt(request, response);

        assertThat(actual, is(false));
    }

    @Test
    public void shouldEncryptWhenRequestSessionNotPresentAndResponseSessionPresentShouldReturnTrue() {

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();

        DummySession responseSession = new DummySession();
        responseSession.setAccessToken("123456789");
        responseSession.setRefreshToken("101112131415");

        Response<DummySession> response = FixtureFactory.makeResponse();
        response.setSession(Optional.of(responseSession));

        Boolean actual = subject.shouldEncrypt(request, response);

        assertThat(actual, is(true));
    }

    @Test
    public void shouldEncryptWhenResponseSessionNotPresentShouldReturnFalse() {
        DummySession requestSession = new DummySession();
        requestSession.setAccessToken("123456789");
        requestSession.setRefreshToken("101112131415");

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setSession(Optional.of(requestSession));

        Response<DummySession> response = FixtureFactory.makeResponse();

        Boolean actual = subject.shouldEncrypt(request, response);

        assertThat(actual, is(false));
    }

    @Test
    public void setPreferredKey() {
        CookieConfig cookieConfig = new CookieConfig("session", true, -1);
        EncryptSession<DummySession, DummyUser> subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                FixtureFactory.encKey("1234"),
                otterAppFactory.objectWriter()
        );

        SymmetricKey encKey = FixtureFactory.encKey("1000");
        subject.setPreferredKey(encKey);

        SymmetricKey actual = subject.getPreferredKey();
        assertThat(actual, is(encKey));
    }

    @Test
    public void setCookieConfig() {
        CookieConfig cookieConfig = new CookieConfig("session", true, -1);
        EncryptSession<DummySession, DummyUser> subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                FixtureFactory.encKey("1234"),
                otterAppFactory.objectWriter()
        );

        CookieConfig sessionCookieConfig = new CookieConfig("session_store", true, -1);
        subject.setCookieConfig(sessionCookieConfig);

        CookieConfig actual = subject.getCookieConfig();
        assertThat(actual, is(sessionCookieConfig));
    }
}