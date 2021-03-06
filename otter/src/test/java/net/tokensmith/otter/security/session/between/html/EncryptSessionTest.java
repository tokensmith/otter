package net.tokensmith.otter.security.session.between.html;


import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.session.exception.EncryptSessionException;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class EncryptSessionTest {
    private TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private EncryptSession<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        CookieConfig cookieConfig = new CookieConfig("session", true, -1, true);
        subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                FixtureFactory.encKey("1234"),
                appFactory.objectWriter()
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

        CookieConfig cookieConfig = new CookieConfig("session", true, -1, true);
        EncryptSession<DummySession, DummyUser> subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                veryBadKey,
                appFactory.objectWriter()
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
        CookieConfig cookieConfig = new CookieConfig("session", true, -1, true);
        EncryptSession<DummySession, DummyUser> subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                FixtureFactory.encKey("1234"),
                appFactory.objectWriter()
        );

        SymmetricKey encKey = FixtureFactory.encKey("1000");
        subject.setPreferredKey(encKey);

        SymmetricKey actual = subject.getPreferredKey();
        assertThat(actual, is(encKey));
    }

    @Test
    public void setCookieConfig() {
        CookieConfig cookieConfig = new CookieConfig("session", true, -1, true);
        EncryptSession<DummySession, DummyUser> subject = new EncryptSession<DummySession, DummyUser>(
                cookieConfig,
                FixtureFactory.encKey("1234"),
                appFactory.objectWriter()
        );

        CookieConfig sessionCookieConfig = new CookieConfig("session_store", true, -1, true);
        subject.setCookieConfig(sessionCookieConfig);

        CookieConfig actual = subject.getCookieConfig();
        assertThat(actual, is(sessionCookieConfig));
    }
}