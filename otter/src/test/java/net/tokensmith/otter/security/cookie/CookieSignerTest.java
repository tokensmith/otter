package net.tokensmith.otter.security.cookie;

import helper.FixtureFactory;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.entity.jwt.header.TokenType;
import net.tokensmith.jwt.exception.SignatureException;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.security.cookie.either.CookieError;
import net.tokensmith.otter.security.cookie.either.ReadEither;
import net.tokensmith.otter.security.cookie.either.ReadError;
import net.tokensmith.otter.security.csrf.CsrfClaims;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class CookieSignerTest {
    private JwtAppFactory jwtAppFactory;
    private Map<String, SymmetricKey> keys;
    private Map<String, String> cookieToKey;
    private CookieSecurity subject;

    @Before
    public void setUp() throws Exception {
        // keys to sign with.
        keys = Map.ofEntries(
            entry("123", FixtureFactory.signKey("123")),
            entry("456", FixtureFactory.signKey("456")),
            entry("789", FixtureFactory.signKey("789")),
            entry("bad-key", FixtureFactory.signKey("789"))
        );

        // cookie to keys .. preferred key mapping.
        cookieToKey = new HashMap<String, String>() {{
            put("redirect","123");
            put("csrf","456");
        }};

        jwtAppFactory = new JwtAppFactory();

        subject = new CookieSigner(jwtAppFactory, keys, cookieToKey);
    }

    @Test
    public void makeShouldSign() throws Exception {
        CsrfClaims claims = new CsrfClaims();
        Optional<Long> iat = Optional.of(Instant.now().toEpochMilli());
        String token = "challenge-token";

        claims.setIssuedAt(iat);
        claims.setChallengeToken(token);

        CookieConfig config = new CookieConfig.Builder()
                .age(CookieConfig.SESSION)
                .httpOnly(true)
                .secure(true)
                .name("csrf")
                .build();

        Cookie actual = subject.make(config, claims);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMaxAge(), is(-1));
        assertThat(actual.isHttpOnly(), is(true));
        assertThat(actual.isSecure(), is(true));
        assertThat(actual.getName(), is("csrf"));
        assertThat(actual.getDomain(), is(nullValue()));
        assertThat(actual.getVersion(), is(0));
        assertThat(actual.getComment(), is(nullValue()));

        assertThat(actual.getValue(), is(notNullValue()));

        JsonWebToken<CsrfClaims> actualJwt = FixtureFactory.csrfJwt(actual.getValue());

        // header assertions
        assertThat(actualJwt.getHeader(), is(notNullValue()));
        assertTrue(actualJwt.getHeader().getKeyId().isPresent());
        assertThat(actualJwt.getHeader().getKeyId().get(), is("456"));
        assertThat(actualJwt.getHeader().getAlgorithm(), is(Algorithm.HS256));
        assertTrue(actualJwt.getHeader().getType().isPresent());
        assertThat(actualJwt.getHeader().getType().get(), is(TokenType.JWT));

        // claim assertions
        CsrfClaims actualClaim = actualJwt.getClaims();
        assertThat(actualClaim, is(notNullValue()));
        assertThat(actualClaim.getChallengeToken(), is(token));
        assertTrue(actualClaim.getIssuedAt().isPresent());
        assertThat(actualClaim.getIssuedAt().get(), is(iat.get()));

        // signature assertion
        VerifySignature verifySignature = null;
        try {
            verifySignature = jwtAppFactory.verifySignature(
                actualJwt.getHeader().getAlgorithm(), keys.get("123")
            );
        } catch (SignatureException e) {
            fail("Unable to verify signature of cookie");
        }

        assertTrue("Signature of jwt is not correct", verifySignature.run(actualJwt));
    }
    
    @Test
    public void readShouldPass() {
        String jwt = FixtureFactory.compactJwtForCSRF(
            keys.get("123"), "challenge-token"
        );

        ReadEither<CsrfClaims> actual = subject.read(jwt, CsrfClaims.class);

        assertFalse(actual.getLeft().isPresent());
        assertTrue(actual.getRight().isPresent());

        CsrfClaims actualClaims = actual.getRight().get();
        assertThat(actualClaims.getChallengeToken(), is("challenge-token"));
    }

    @Test
    public void readWhenNotAJwtShouldFail() {
        String jwt = "not-a-jwt";

        ReadEither<CsrfClaims> actual = subject.read(jwt, CsrfClaims.class);

        assertTrue(actual.getLeft().isPresent());
        assertFalse(actual.getRight().isPresent());

        ReadError<CsrfClaims> actualError = actual.getLeft().get();
        assertThat(actualError.getCookieError(), is(CookieError.JWT_INVALID));
        assertFalse(actualError.getClaims().isPresent());
        assertThat(actualError.getCause(), is(instanceOf(CookieJwtException.class)));
    }

    @Test
    public void readWhenSignatureFailsShouldBeSignatureInvalid() throws Exception {
        SymmetricKey key = FixtureFactory.generateSignKey("123");
        String jwt = FixtureFactory.compactJwtForCSRF(
                key, "challenge-token"
        );

        ReadEither<CsrfClaims> actual = subject.read(jwt, CsrfClaims.class);

        assertTrue(actual.getLeft().isPresent());
        assertFalse(actual.getRight().isPresent());

        ReadError<CsrfClaims> actualError = actual.getLeft().get();
        assertThat(actualError.getCookieError(), is(CookieError.SIGNATURE_INVALID));

        assertTrue(actualError.getClaims().isPresent());
        CsrfClaims actualClaims = actualError.getClaims().get();
        assertThat(actualClaims.getChallengeToken(), is("challenge-token"));
        assertThat(actualError.getCause(), is(nullValue()));
    }

    @Test
    public void readWhenKeyNotFoundShouldBeSignatureError() {

        SymmetricKey notFoundKey = FixtureFactory.signKey("not-found");
        // make the jwt with a good key that was not passed into CookieSigner
        String jwt = FixtureFactory.compactJwtForCSRF(
                notFoundKey, "challenge-token"
        );

        ReadEither<CsrfClaims> actual = subject.read(jwt, CsrfClaims.class);

        assertTrue(actual.getLeft().isPresent());
        assertFalse(actual.getRight().isPresent());

        ReadError<CsrfClaims> actualError = actual.getLeft().get();
        assertThat(actualError.getCookieError(), is(CookieError.SIGNATURE_ERROR));
        assertTrue(actualError.getClaims().isPresent());
        CsrfClaims actualClaims = actualError.getClaims().get();
        assertThat(actualClaims.getChallengeToken(), is("challenge-token"));
        assertThat(actualError.getCause(), is(nullValue()));
    }

    @Test
    public void readWhenBadKeyValueShouldBeSignatureError() {

        // make the jwt with a good key.
        String jwt = FixtureFactory.compactJwtForCSRF(
                keys.get("123"), "challenge-token"
        );

        // send in a bad key to the subject.
        Map<String, SymmetricKey> keys = Map.ofEntries(
                entry("123", FixtureFactory.badSignKey("123")) // <-- bad key.
        );
        CookieSecurity subject = new CookieSigner(jwtAppFactory, keys, cookieToKey);
        ReadEither<CsrfClaims> actual = subject.read(jwt, CsrfClaims.class);

        assertTrue(actual.getLeft().isPresent());
        assertFalse(actual.getRight().isPresent());

        ReadError<CsrfClaims> actualError = actual.getLeft().get();
        assertThat(actualError.getCookieError(), is(CookieError.SIGNATURE_ERROR));
        assertTrue(actualError.getClaims().isPresent());
        CsrfClaims actualClaims = actualError.getClaims().get();
        assertThat(actualClaims.getChallengeToken(), is("challenge-token"));
        assertThat(actualError.getCause(), is(instanceOf(CookieJwtException.class)));
    }

    @Test
    public void readWhenJwtHasNoKeyIdShouldBeNoKeyId() {
        String jwt = FixtureFactory.compactUnSecureJwtForCSRF("challenge-toke");

        ReadEither<CsrfClaims> actual = subject.read(jwt, CsrfClaims.class);

        assertTrue(actual.getLeft().isPresent());
        assertFalse(actual.getRight().isPresent());

        ReadError<CsrfClaims> actualError = actual.getLeft().get();
        assertThat(actualError.getCookieError(), is(CookieError.NO_KEY_ID));
        assertTrue(actualError.getClaims().isPresent());
        // claims assertions
        assertThat(actualError.getCause(), is(nullValue()));
    }
}