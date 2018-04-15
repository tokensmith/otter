package org.rootservices.otter.security.csrf;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.exception.InvalidJWT;
import org.rootservices.jwt.exception.SignatureException;
import org.rootservices.jwt.jws.serialization.SecureJwtSerializer;
import org.rootservices.jwt.jws.verifier.VerifySignature;
import org.rootservices.jwt.serialization.JwtSerde;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import org.rootservices.jwt.serialization.exception.JwtToJsonException;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

public class DoubleSubmitCSRF {
    protected static Logger logger = LogManager.getLogger(DoubleSubmitCSRF.class);

    private JwtAppFactory jwtAppFactory;
    private RandomString randomString;
    private SymmetricKey preferredSignKey;
    private Map<String, SymmetricKey> rotationSignKeys;

    private static String VERIFY_MSG = "Could not verify signature";
    private static String SERIALIZE_JWT = "Could not serialize to compact jwt";
    private static String DE_SERIALIZE_JWT = "Could not deserialize CSRF JWT to pojo";

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

    public Boolean doTokensMatch(String encodedCsrfCookieValue, String csrfFormValue) {

        JsonWebToken csrfJwt;
        try {
            csrfJwt = csrfCookieValueToJwt(encodedCsrfCookieValue);
        } catch (CsrfException e) {
            logger.debug(e.getMessage(), e);
            return false;
        }

        SymmetricKey signKey = getSignKey(csrfJwt.getHeader().getKeyId().get());
        Boolean signatureValid;
        try {
            signatureValid = verifyCsrfCookieSignature(csrfJwt, signKey);
        } catch (CsrfException e) {
            logger.debug(e.getMessage(), e);
            return false;
        }

        CsrfClaims csrfClaims = (CsrfClaims) csrfJwt.getClaims();

        if (signatureValid && csrfClaims.getChallengeToken().equals(csrfFormValue)) {
            return true;
        }
        return false;
    }

    public JsonWebToken csrfCookieValueToJwt(String encodedCsrfCookieValue) throws CsrfException {
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

    public Cookie makeCsrfCookie(String name, String challengeToken, Boolean secure, int maxAge) throws CsrfException {
        Optional<Long> issuedAt = Optional.of(OffsetDateTime.now().toEpochSecond());

        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken);
        csrfClaims.setIssuedAt(issuedAt);

        SecureJwtSerializer secureJwtSerializer;
        try {
            secureJwtSerializer = jwtAppFactory.secureJwtSerializer(Algorithm.HS256, preferredSignKey);
        } catch (SignatureException e) {
            throw new CsrfException(SERIALIZE_JWT, e);
        }

        String compactJwt;
        try {
            compactJwt = secureJwtSerializer.compactJwtToString(csrfClaims);
        } catch (JwtToJsonException e) {
            throw new CsrfException(SERIALIZE_JWT, e);
        }

        Cookie csrfCookie = new Cookie();
        csrfCookie.setSecure(secure);
        csrfCookie.setName(name);
        csrfCookie.setMaxAge(maxAge);
        csrfCookie.setValue(compactJwt);

        return csrfCookie;
    }

    public void setPreferredSignKey(SymmetricKey preferredSignKey) {
        this.preferredSignKey = preferredSignKey;
    }

    public void setRotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
    }
}
