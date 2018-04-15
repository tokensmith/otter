package org.rootservices.otter.security.session.between;

import helper.FixtureFactory;
import helper.entity.DummySession;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.jwt.exception.InvalidJWT;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.security.session.between.exception.InvalidSessionException;
import org.rootservices.otter.router.entity.Method;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class DecryptSessionTest {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();
    private DecryptSession<DummySession> subject;

    @Before
    public void setUp() {
        subject = new DecryptDummySession(
                "session",
                otterAppFactory.jwtAppFactory(),
                FixtureFactory.encKey("1234"),
                FixtureFactory.encRotationKey("5678"),
                otterAppFactory.objectMapper()
        );
    }

    @Test
    public void processShouldBeOk() throws Exception {

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        Request request = FixtureFactory.makeRequest();
        request.getCookies().put("session", sessionCookie);

        Response response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        DummySession actual = (DummySession) request.getSession().get();
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is("123456789"));
        assertThat(actual.getRefreshToken(), is("101112131415"));
    }


    @Test
    public void decryptShouldBeOk() throws Exception {

        String encryptedSession = new StringBuilder()
            .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
            .append(".")
            .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
            .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
            .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
            .toString();

        DummySession session = subject.decrypt(encryptedSession);

        assertThat(session, is(notNullValue()));
        assertThat(session.getAccessToken(), is("123456789"));
        assertThat(session.getRefreshToken(), is("101112131415"));
    }

    @Test
    public void decryptWhenJsonToJwtExceptionShouldThrowInvalidJWTException() throws Exception {

        String encryptedSession = new StringBuilder()
                .append("notAJWE")
                .toString();

        InvalidSessionException actual = null;
        try {
            subject.decrypt(encryptedSession);
        } catch(InvalidSessionException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(instanceOf(InvalidJWT.class)));
    }
}