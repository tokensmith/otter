package net.tokensmith.otter.security.csrf;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.security.config.SecurityAppFactory;
import net.tokensmith.otter.security.csrf.exception.CsrfException;
import net.tokensmith.otter.security.entity.ChallengeToken;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.*;


public class DoubleSubmitCSRFTest {

    private DoubleSubmitCSRF subject;

    @Before
    public void setUp() {
        SecurityAppFactory appFactory = new SecurityAppFactory();
        subject = appFactory.doubleSubmitCSRF();

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
        ChallengeToken challengeToken = new ChallengeToken("challenge-token", "cookie-noise");
        Cookie cookie = subject.makeCsrfCookie("CSRF", challengeToken, true, -1, true);

        ChallengeToken formChallengeToken = new ChallengeToken("challenge-token", "form-noise");
        ByteArrayOutputStream formValueJwt = subject.toJwt(formChallengeToken);

        Boolean actual = subject.doTokensMatch(cookie.getValue(), formValueJwt.toString());
        assertThat(actual, is(true));
    }

    @Test
    public void doTokensMatchWhenTokensAreDifferentShouldBeFalse() throws Exception {
        // use the software to test it :)
        ChallengeToken challengeToken = new ChallengeToken("challenge-token", "noise");
        Cookie cookie = subject.makeCsrfCookie("CSRF", challengeToken, true, -1, true);

        ChallengeToken formChallengeToken = new ChallengeToken("form-challenge-token", "form-noise");
        ByteArrayOutputStream formValueJwt = subject.toJwt(formChallengeToken);

        Boolean actual = subject.doTokensMatch(cookie.getValue(), formValueJwt.toString());
        assertThat(actual, is(false));
    }

    @Test
    public void doTokensMatchWhenNoiseAreIdenticalShouldBeFalse() throws Exception {
        // use the software to test it :)
        ChallengeToken challengeToken = new ChallengeToken("challenge-token", "noise");
        Cookie cookie = subject.makeCsrfCookie("CSRF", challengeToken, true, -1, true);

        ChallengeToken formChallengeToken = new ChallengeToken("form-challenge-token", "noise");
        ByteArrayOutputStream formValueJwt = subject.toJwt(formChallengeToken);

        Boolean actual = subject.doTokensMatch(cookie.getValue(), formValueJwt.toString());
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
    public void csrfToJwtShouldReturnJwt() throws Exception {
        SymmetricKey key = FixtureFactory.signKey("key-1");
        String compactJwtForCSRF = FixtureFactory.compactJwtForCSRF(key, "challenge-token");

        JsonWebToken actual = subject.csrfToJwt(compactJwtForCSRF);

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
    public void csrfToJwtShouldShouldThrowCsrfException() throws Exception {
        String mangledEncodedCsrfJwt = "foo.foo.foo";

        CsrfException actual = null;
        try {
            subject.csrfToJwt(mangledEncodedCsrfJwt);
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
        ChallengeToken challengeToken = new ChallengeToken("challenge-token", "noise");
        Cookie actual = subject.makeCsrfCookie("CSRF", challengeToken, true, -1, true);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getName(), is("CSRF"));
        assertThat(actual.isSecure(), is(true));
        assertThat(actual.getMaxAge(), is(-1));
        assertThat(actual.isHttpOnly(), is(true));

        // might as well use csrfCookieValueToJwt to validate the cookie value.

        JsonWebToken csrfJwt = subject.csrfToJwt(actual.getValue());
        CsrfClaims csrfClaims = (CsrfClaims) csrfJwt.getClaims();

        assertThat(csrfClaims.getChallengeToken(), is(notNullValue()));
        assertThat(csrfClaims.getChallengeToken(), is("challenge-token"));
        assertThat(csrfClaims.getNoise(), is("noise"));
        assertThat(csrfClaims.getIssuedAt(), is(notNullValue()));
        assertThat(csrfClaims.getIssuedAt().isPresent(), is(true));
        assertThat(csrfClaims.getIssuedAt().get(), is(lessThanOrEqualTo(OffsetDateTime.now().toEpochSecond())));
    }
}