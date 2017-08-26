package org.rootservices.otter.security.csrf;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.SecureJwtEncoder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.JWTSerializer;
import org.rootservices.jwt.serializer.exception.JsonToJwtException;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;
import org.rootservices.jwt.signature.verifier.VerifySignature;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

public class DoubleSubmitCSRF {
    protected static Logger logger = LogManager.getLogger(DoubleSubmitCSRF.class);

    private AppFactory jwtFactory;
    private RandomString randomString;
    private SymmetricKey preferredSignKey;
    private Map<String, SymmetricKey> rotationSignKeys;

    private static String VERIFY_MSG = "Could not verify signature";
    private static String DECODE_JWT = "Could not decode CSRF JWT to POJO";
    private static String ENCODE_JWT = "Could not create CSRF JWT";

    public DoubleSubmitCSRF(AppFactory jwtFactory, RandomString randomString) {
        this.jwtFactory = jwtFactory;
        this.randomString = randomString;
    }

    public DoubleSubmitCSRF(AppFactory jwtFactory, RandomString randomString, SymmetricKey preferredSignKey, Map<String, SymmetricKey> rotationSignKeys) {
        this.jwtFactory = jwtFactory;
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
        JWTSerializer jwtSerializer = jwtFactory.jwtSerializer();

        JsonWebToken jsonWebToken;
        try {
            jsonWebToken = jwtSerializer.stringToJwt(encodedCsrfCookieValue, CsrfClaims.class);
        } catch (JsonToJwtException e) {
            throw new CsrfException(DECODE_JWT, e);
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
            verifySignature = jwtFactory.verifySignature(
                csrfJwt.getHeader().getAlgorithm(), signKey
            );
        } catch (InvalidJsonWebKeyException e) {
            throw new CsrfException(VERIFY_MSG, e);
        } catch (InvalidAlgorithmException e) {
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

        SecureJwtEncoder secureJwtEncoder;
        try {
            secureJwtEncoder = jwtFactory.secureJwtEncoder(Algorithm.HS256, preferredSignKey);
        } catch (InvalidAlgorithmException e) {
            throw new CsrfException(ENCODE_JWT, e);
        } catch (InvalidJsonWebKeyException e) {
            throw new CsrfException(ENCODE_JWT, e);
        }

        String encodedJwt = null;
        try {
            encodedJwt = secureJwtEncoder.encode(csrfClaims);
        } catch (JwtToJsonException e) {
            throw new CsrfException(ENCODE_JWT, e);
        }

        Cookie csrfCookie = new Cookie();
        csrfCookie.setSecure(secure);
        csrfCookie.setName(name);
        csrfCookie.setMaxAge(maxAge);
        csrfCookie.setValue(encodedJwt);

        return csrfCookie;
    }

    public void setPreferredSignKey(SymmetricKey preferredSignKey) {
        this.preferredSignKey = preferredSignKey;
    }

    public void setRotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
    }
}
