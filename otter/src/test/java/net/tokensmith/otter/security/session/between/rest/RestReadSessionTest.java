package net.tokensmith.otter.security.session.between.rest;

import com.fasterxml.jackson.databind.ObjectReader;
import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.security.session.between.rest.RestReadSession;
import net.tokensmith.otter.security.session.util.Decrypt;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.junit.Test;

import java.util.Optional;
import java.util.function.BiFunction;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class RestReadSessionTest {

    public RestReadSession<DummySession, DummyUser> subject(Boolean required) {
        // this is some reverse engineer foo from BetweenBuilder.
        Shape shape = FixtureFactory.makeShape("1234", "5678");
        ObjectReader sessionObjectReader = new TranslatorAppFactory().objectReader().forType(DummySession.class);
        Decrypt<DummySession> decrypt = new Decrypt<>(new JwtAppFactory(), sessionObjectReader, shape.getEncKey(), shape.getRotationEncKeys());

        OtterAppFactory otterAppFactory = new OtterAppFactory();
        BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse> onHalt =  otterAppFactory.defaultRestOnHalts(shape).get(Halt.SESSION);

        return new RestReadSession<>("session", required, decrypt, onHalt);
    }

    @Test
    public void processsWhenRequiredShouldBeOk() throws Exception {
        RestReadSession<DummySession, DummyUser> subject = subject(true);

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        request.getCookies().put("session", sessionCookie);

        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        subject.process(Method.GET, request, response);

        assertThat(request.getSession().isPresent(), is(true));
        assertThat(request.getSession().get().getAccessToken(), is("123456789"));
        assertThat(request.getSession().get().getRefreshToken(), is("101112131415"));
    }

    @Test
    public void readSessionWhenRequiredShouldBeOk() throws Exception {
        RestReadSession<DummySession, DummyUser> subject = subject(true);

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        request.getCookies().put("session", sessionCookie);

        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        Optional<DummySession> actual = subject.readSession(request, response);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get().getAccessToken(), is("123456789"));
        assertThat(actual.get().getRefreshToken(), is("101112131415"));
    }

    @Test
    public void readSessionWhenRequiredAndNoSessionThenThrowHalt() throws Exception {
        RestReadSession<DummySession, DummyUser> subject = subject(true);

        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        HaltException actual = null;
        try {
            subject.readSession(request, response);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(response.getStatusCode(), is(StatusCode.UNAUTHORIZED));
    }

    @Test
    public void readSessionWhenNotRequiredShouldBeOk() throws Exception {
        RestReadSession<DummySession, DummyUser> subject = subject(false);

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        Cookie sessionCookie = FixtureFactory.makeCookie("session");
        sessionCookie.setValue(encryptedSession);

        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        request.getCookies().put("session", sessionCookie);

        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        Optional<DummySession> actual = subject.readSession(request, response);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get().getAccessToken(), is("123456789"));
        assertThat(actual.get().getRefreshToken(), is("101112131415"));
    }

    @Test
    public void readSessionWhenNotRequiredAndNoSessionShouldBeOk() throws Exception {
        RestReadSession<DummySession, DummyUser> subject = subject(false);

        RestBtwnRequest<DummySession, DummyUser> request = FixtureFactory.makeRestBtwnRequest();
        RestBtwnResponse response = FixtureFactory.makeRestBtwnResponse();

        Optional<DummySession> actual = subject.readSession(request, response);

        assertThat(actual.isPresent(), is(false));
    }
}