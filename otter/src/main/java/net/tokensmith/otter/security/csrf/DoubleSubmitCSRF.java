package net.tokensmith.otter.security.csrf;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.jwt.builder.compact.SecureCompactBuilder;
import net.tokensmith.jwt.builder.exception.CompactException;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.exception.SignatureException;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.csrf.exception.CsrfException;
import net.tokensmith.otter.security.entity.ChallengeToken;


import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

public class DoubleSubmitCSRF {
    public static final String SIGNATURE_INVALID = "Signature Invalid";
    public static final String CSRF_FAILED = "CSRF failed validation. challengeTokensMatch: {}, noiseMatch: {}";
    private static String VERIFY_MSG = "Could not verify signature";
    private static String SERIALIZE_JWT = "Could not serialize to compact jwt";
    private static String DE_SERIALIZE_JWT = "Could not deserialize CSRF JWT to pojo";
    protected static Logger LOGGER = LoggerFactory.getLogger(DoubleSubmitCSRF.class);

    private JwtAppFactory jwtAppFactory;
    private RandomString randomString;
    private SymmetricKey preferredSignKey;
    private Map<String, SymmetricKey> rotationSignKeys;

    public DoubleSubmitCSRF(JwtAppFactory jwtAppFactory, RandomString randomString) {
        this.jwtAppFactory = jwtAppFactory;
        this.randomString = randomString;
    }

    public DoubleSubmitCSRF(JwtAppFactory jwtAppFactory, RandomString randomString, SymmetricKey preferredSignKey, Map<String, SymmetricKey> rotationSignKeys) {
        this.jwtAppFactory = jwtAppFactory;
        this.randomString = randomString;
        this.preferredSignKey = preferredSignKey;
        this.rotationSignKeys = rotationSignKeys;
    }

    public Boolean doTokensMatch(String cookieValue, String formValue) {

        CsrfClaims cookieClaims;
        CsrfClaims formClaims;
        try {
            cookieClaims = toClaims(cookieValue);
            formClaims = toClaims(formValue);
        } catch (CsrfException e) {
            LOGGER.debug(e.getMessage(), e);
            return false;
        }

        Boolean challengeTokensMatch = cookieClaims.getChallengeToken().equals(formClaims.getChallengeToken());
        Boolean noiseMatch = cookieClaims.getNoise().equals(formClaims.getNoise());

        if (challengeTokensMatch && !noiseMatch) {
            return true;
        } else {
            LOGGER.debug(CSRF_FAILED, challengeTokensMatch, noiseMatch);
        }
        return false;
    }

    protected CsrfClaims toClaims(String value) throws CsrfException {
        JsonWebToken csrfJwt;
        try {
            csrfJwt = csrfToJwt(value);
        } catch (CsrfException e) {
            LOGGER.debug(e.getMessage(), e);
            throw e;
        }

        SymmetricKey signKey = getSignKey(csrfJwt.getHeader().getKeyId().get());
        Boolean signatureValid;
        try {
            signatureValid = verifyCsrfCookieSignature(csrfJwt, signKey);
        } catch (CsrfException e) {
            LOGGER.debug(e.getMessage(), e);
            throw e;
        }

        if (!signatureValid) {
            LOGGER.debug(SIGNATURE_INVALID);
            throw new CsrfException(SIGNATURE_INVALID);
        }

        CsrfClaims csrfClaims = (CsrfClaims) csrfJwt.getClaims();
        return csrfClaims;
    }

    public JsonWebToken csrfToJwt(String encodedCsrfCookieValue) throws CsrfException {
        JwtSerde jwtSerde = jwtAppFactory.jwtSerde();

        JsonWebToken jsonWebToken;
        try {
            jsonWebToken = jwtSerde.stringToJwt(encodedCsrfCookieValue, CsrfClaims.class);
        } catch (JsonToJwtException e) {
            throw new CsrfException(DE_SERIALIZE_JWT, e);
        } catch (InvalidJWT e) {
            throw new CsrfException(DE_SERIALIZE_JWT, e);
        }

        return jsonWebToken;
    }

    protected SymmetricKey getSignKey(String keyId) {
        SymmetricKey key;
        if (preferredSignKey.getKeyId().get().equals(keyId)) {
            key = preferredSignKey;
        } else {
            key = rotationSignKeys.get(keyId);
        }
        return key;
    }

    protected Boolean verifyCsrfCookieSignature(JsonWebToken csrfJwt, SymmetricKey signKey) throws CsrfException {

        VerifySignature verifySignature;
        try {
            verifySignature = jwtAppFactory.verifySignature(
                csrfJwt.getHeader().getAlgorithm(), signKey
            );
        } catch (SignatureException e) {
            throw new CsrfException(VERIFY_MSG, e);
        }

        return verifySignature.run(csrfJwt);
    }

    public String makeChallengeToken() {
        return randomString.run();
    }

    public Cookie makeCsrfCookie(String name, ChallengeToken challengeToken, Boolean secure, int maxAge, Boolean isHttpOnly) throws CsrfException {

        ByteArrayOutputStream compactJwt = toJwt(challengeToken);

        return new Cookie.Builder()
                .secure(secure)
                .name(name)
                .maxAge(maxAge)
                .value(compactJwt.toString())
                .httpOnly(isHttpOnly)
                .build();
    }

    public ByteArrayOutputStream toJwt(ChallengeToken challengeToken) throws CsrfException {
        Optional<Long> issuedAt = Optional.of(OffsetDateTime.now().toEpochSecond());

        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken.getToken());
        csrfClaims.setNoise(challengeToken.getNoise());
        csrfClaims.setIssuedAt(issuedAt);

        SecureCompactBuilder compactBuilder = new SecureCompactBuilder();

        ByteArrayOutputStream compactJwt;
        try {
            compactJwt = compactBuilder.alg(Algorithm.HS256)
                    .key(preferredSignKey)
                    .claims(csrfClaims)
                    .build();
        } catch (CompactException e) {
            throw new CsrfException(SERIALIZE_JWT, e);
        }

        return compactJwt;
    }


    public void setPreferredSignKey(SymmetricKey preferredSignKey) {
        this.preferredSignKey = preferredSignKey;
    }

    public void setRotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
    }
}
