package org.rootservices.otter.security.csrf;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.security.csrf.exception.CsrfException;
import suite.UnitTest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.*;


@Category(UnitTest.class)
public class DoubleSubmitCSRFTest {

    private DoubleSubmitCSRF subject;

    @Before
    public void setUp() {
        OtterAppFactory otterFactory = new OtterAppFactory();
        subject = otterFactory.doubleSubmitCSRF();

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");

        Map<String, SymmetricKey> rotationSignKeys = new HashMap<>();
        rotationSignKeys.put("rotation-key-1", FixtureFactory.signKey("rotation-key-1"));
        rotationSignKeys.put("rotation-key-2", FixtureFactory.signKey("rotation-key-2"));

        subject.setPreferredSignKey(preferredSignKey);
        subject.setRotationSignKeys(rotationSignKeys);
    }

    @Test
    public void doTokensMatchShouldBeOk() throws Exception {
        // use the software to test it :)
        String challengeToken = "challenge-token";
        Cookie cookie = subject.makeCsrfCookie("CSRF", challengeToken, true, -1);

        Boolean actual = subject.doTokensMatch(cookie.getValue(), challengeToken);
        assertThat(actual, is(true));
    }

    @Test
    public void doTokensMatchShouldBeFalse() throws Exception {
        // use the software to test it :)
        String challengeToken = "challenge-token";
        Cookie cookie = subject.makeCsrfCookie("CSRF", challengeToken, true, -1);

        Boolean actual = subject.doTokensMatch(cookie.getValue(), "challenge-token-1");
        assertThat(actual, is(false));
    }

    @Test
    public void getSignKeyShouldBePreferred() {
        SymmetricKey actual = subject.getSignKey("preferred-key");

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKeyId().isPresent(), is(true));
        assertThat(actual.getKeyId().get(), is("preferred-key"));
    }

    @Test
    public void getSignKeyShouldBeRotation() {
        SymmetricKey actual = subject.getSignKey("rotation-key-1");

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getKeyId().isPresent(), is(true));
        assertThat(actual.getKeyId().get(), is("rotation-key-1"));
    }

    @Test
    public void csrfCookieValueToJwtShouldReturnJwt() throws Exception {
        SymmetricKey key = FixtureFactory.signKey("key-1");
        String compactJwtForCSRF = FixtureFactory.compactJwtForCSRF(key, "challenge-token");

        JsonWebToken actual = subject.csrfCookieValueToJwt(compactJwtForCSRF);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getHeader(), is(notNullValue()));
        assertThat(actual.getHeader().getKeyId(), is(notNullValue()));
        assertThat(actual.getHeader().getKeyId().isPresent(), is(true));
        assertThat(actual.getHeader().getKeyId().get(), is("key-1"));
        assertThat(actual.getClaims(), is(notNullValue()));
        assertThat(actual.getClaims(), instanceOf(CsrfClaims.class));
        assertThat(actual.getSignature(), is(notNullValue()));
    }

    @Test
    public void csrfCookieValueToJwtShouldShouldThrowCsrfException() throws Exception {
        String mangledEncodedCsrfJwt = "foo.foo.foo";

        CsrfException actual = null;
        try {
            subject.csrfCookieValueToJwt(mangledEncodedCsrfJwt);
        } catch (CsrfException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCause(), instanceOf(JsonToJwtException.class));
    }

    @Test
    public void verifyCsrfCookieSignatureShouldBeTrue() throws Exception {
        SymmetricKey key = FixtureFactory.signKey("key-1");
        String compactJwtForCSRF = FixtureFactory.compactJwtForCSRF(key, "challenge-token");
        JsonWebToken csrfJwt = FixtureFactory.csrfJwt(compactJwtForCSRF);

        Boolean actual = subject.verifyCsrfCookieSignature(csrfJwt, key);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(true));
    }

    @Test
    public void verifyCsrfCookieSignatureShouldBeFalse() throws Exception {
        SymmetricKey key = FixtureFactory.signKey("key-1");
        String compactJwtForCSRF = FixtureFactory.compactJwtForCSRF(key, "challenge-token");
        JsonWebToken csrfJwt = FixtureFactory.csrfJwt(compactJwtForCSRF);

        SymmetricKey key2 = FixtureFactory.signKey("key-2");
        key2.setKey("key-2-value");

        Boolean actual = subject.verifyCsrfCookieSignature(csrfJwt, key2);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(false));
    }

    @Test
    public void makeChallengeTokenShouldBeOk() {
        String actual = subject.makeChallengeToken();

        assertThat(actual, is(notNullValue()));
    }

    @Test
    public void makeCsrfCookieShouldBeOk() throws Exception {
        Cookie actual = subject.makeCsrfCookie("CSRF", "challenge-token", true, -1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("CSRF"));
        assertThat(actual.isSecure(), is(true));
        assertThat(actual.getMaxAge(), is(-1));

        // might as well use csrfCookieValueToJwt to validate the cookie value.

        JsonWebToken csrfJwt = subject.csrfCookieValueToJwt(actual.getValue());
        CsrfClaims csrfClaims = (CsrfClaims) csrfJwt.getClaims();

        assertThat(csrfClaims.getChallengeToken(), is(notNullValue()));
        assertThat(csrfClaims.getChallengeToken(), is("challenge-token"));
        assertThat(csrfClaims.getIssuedAt(), is(notNullValue()));
        assertThat(csrfClaims.getIssuedAt().isPresent(), is(true));
        assertThat(csrfClaims.getIssuedAt().get(), is(lessThanOrEqualTo(OffsetDateTime.now().toEpochSecond())));
    }
}