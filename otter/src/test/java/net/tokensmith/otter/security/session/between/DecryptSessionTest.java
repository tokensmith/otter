package net.tokensmith.otter.security.session.between;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import org.junit.Test;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.gateway.LocationTranslatorFactory;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.router.factory.BetweenFlyweight;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.security.session.between.exception.InvalidSessionException;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.security.session.between.exception.SessionDecryptException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class DecryptSessionTest {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();

    public DecryptSession<DummySession, DummyUser> subject(Boolean required) throws SessionCtorException {
        Shape shape = FixtureFactory.makeShape("1234", "5678");
        Betweens<DummySession, DummyUser> betweens;

        LocationTranslatorFactory locationTranslatorFactory = otterAppFactory.locationTranslatorFactory(shape);
        BetweenFlyweight<DummySession, DummyUser> betweenFlyweight = locationTranslatorFactory.betweenFlyweight(
                DummySession.class,
                Optional.empty(),
                Optional.empty()
        );

        if (required) {
            List<Label> labels = new ArrayList<>();
            labels.add(Label.SESSION_REQUIRED);
            betweens = betweenFlyweight.make(Method.GET, labels);
        } else {
            List<Label> labels = new ArrayList<>();
            labels.add(Label.SESSION_OPTIONAL);
            betweens = betweenFlyweight.make(Method.GET, labels);
        }
        return (DecryptSession<DummySession, DummyUser>) betweens.getBefore().get(0);
    }

    @Test
    public void processWhenRequiredShouldBeOk() throws Exception {
        DecryptSession<DummySession, DummyUser> subject = subject(Boolean.TRUE);

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.getCookies().put("session", sessionCookie);

        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        DummySession actual = (DummySession) request.getSession().get();
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is("123456789"));
        assertThat(actual.getRefreshToken(), is("101112131415"));
    }

    @Test
    public void processWhenRequiredAndNoSessionShouldHalt() throws Exception {
        DecryptSession<DummySession, DummyUser> subject = subject(Boolean.TRUE);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        HaltException actual = null;
        try {
            subject.process(Method.GET, request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), is(nullValue()));
    }

    @Test
    public void processWhenRequiredAndInvalidSessionExceptionShouldHalt() throws Exception {
        DecryptSession<DummySession, DummyUser> subject = subject(Boolean.TRUE);

        String encryptedSession = new StringBuilder()
                .append("notAJWE")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.getCookies().put("session", sessionCookie);

        Response<DummySession> response = FixtureFactory.makeResponse();

        HaltException actual = null;
        try {
            subject.process(Method.GET, request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), instanceOf(InvalidSessionException.class));
    }

    @Test
    public void processWhenNotRequiredShouldBeOk() throws Exception {
        DecryptSession<DummySession, DummyUser> subject = subject(Boolean.FALSE);

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.getCookies().put("session", sessionCookie);

        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);

        DummySession actual = (DummySession) request.getSession().get();
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccessToken(), is("123456789"));
        assertThat(actual.getRefreshToken(), is("101112131415"));
    }

    @Test
    public void processWhenNotRequiredAndNoSessionShouldBeOk() throws Exception {
        DecryptSession<DummySession, DummyUser> subject = subject(Boolean.FALSE);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        Response<DummySession> response = FixtureFactory.makeResponse();

        subject.process(Method.GET, request, response);
        assertThat(request.getSession().isPresent(), is(false));
    }
}