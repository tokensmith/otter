package net.tokensmith.otter.security.session.util;


import com.fasterxml.jackson.databind.ObjectReader;
import helper.FixtureFactory;
import helper.entity.model.DummySession;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.security.session.exception.InvalidSessionException;
import net.tokensmith.otter.security.session.exception.SessionDecryptException;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DecryptTest {

    public Decrypt<DummySession> subject() {
        // this is some reverse engineer foo from BetweenBuilder.
        Shape shape = FixtureFactory.makeShape("1234", "5678");
        ObjectReader sessionObjectReader = new TranslatorAppFactory().objectReader().forType(DummySession.class);
        return new Decrypt<>(new JwtAppFactory(), sessionObjectReader, shape.getEncKey(), shape.getRotationEncKeys());
    }


    @Test
    public void decryptWhenRequiredShouldBeOk() throws Exception {

        Decrypt<DummySession> subject = subject();

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
    public void decryptWhenRequiredAndJsonToJwtExceptionShouldThrowInvalidJWTException() throws Exception {

        Decrypt<DummySession> subject = subject();

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

    @Test
    public void decryptWhenBadKeyShouldThrowSessionDecryptException() throws Exception {
        Decrypt<DummySession> subject = subject();

        SymmetricKey veryBadKey = FixtureFactory.encKey("1234");
        veryBadKey.setKey("MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--1234567");

        subject.setPreferredKey(veryBadKey);

        String encryptedSession = new StringBuilder()
                .append("eyJhbGciOiJkaXIiLCJraWQiOiIxMjM0IiwiZW5jIjoiQTI1NkdDTSJ9.")
                .append(".")
                .append("AkRUVwJboJnzM5Pt0uqK-Ju15_YSn8x0DCrxDcKUszdQei2Fa7hYxENHJytWK1iMfl4lmcMb-fVTCnUC_bBa1abfeJ1NWWzRNwPEc-zhXvFV2-255lJe8EZYSSwE7cDf.")
                .append("pvvpZcAtxSFpzjqmgJEjh6oJLAoRAWv9WAQJ6BY08TDLpqZATSP4f4RPLMc8g7ArdMIJQI2coRBDjSg.")
                .append("Z4eCgEJ-RIfWX1jKYeP5Bw")
                .toString();

        SessionDecryptException actual = null;
        try {
            subject.decrypt(encryptedSession);
        } catch (SessionDecryptException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
    }
}