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

    public DoubleSubmitCSRF(AppFactory jwtFactory, RandomString randomString, SymmetricKey preferredSignKey, Map<String, SymmetricKey> rotationSignKeys) {
        this.jwtFactory = jwtFactory;
        this.randomString = randomString;
        this.preferredSignKey = preferredSignKey;
        this.rotationSignKeys = rotationSignKeys;
    }

    protected Boolean doTokensMatch(String encodedCsrfCookieValue, String csrfFormValue) {

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

    protected JsonWebToken csrfCookieValueToJwt(String encodedCsrfCookieValue) throws CsrfException {
        JWTSerializer jwtSerializer = jwtFactory.jwtSerializer();

        JsonWebToken jsonWebToken;
        try {
            jsonWebToken = jwtSerializer.stringToJwt(encodedCsrfCookieValue, CsrfClaims.class);
        } catch (JsonToJwtException e) {
            throw new CsrfException("", e);
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
            throw new CsrfException("", e);
        } catch (InvalidAlgorithmException e) {
            throw new CsrfException("", e);
        }

        return verifySignature.run(csrfJwt);
    }

    protected Cookie makeCsrfCookie(String name, Boolean secure, int maxAge) throws CsrfException {
        String challengeToken = randomString.run();
        Optional<Long> issuedAt = Optional.of(OffsetDateTime.now().toEpochSecond());

        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken);
        csrfClaims.setIssuedAt(issuedAt);

        SecureJwtEncoder secureJwtEncoder = null;
        try {
            secureJwtEncoder = jwtFactory.secureJwtEncoder(Algorithm.HS256, preferredSignKey);
        } catch (InvalidAlgorithmException e) {
            throw new CsrfException("", e);
        } catch (InvalidJsonWebKeyException e) {
            throw new CsrfException("", e);
        }

        String encodedJwt = null;
        try {
            encodedJwt = secureJwtEncoder.encode(csrfClaims);
        } catch (JwtToJsonException e) {
            throw new CsrfException("", e);
        }

        Cookie csrfCookie = new Cookie();
        csrfCookie.setSecure(secure);
        csrfCookie.setName(name);
        csrfCookie.setMaxAge(maxAge);
        csrfCookie.setValue(encodedJwt);

        return csrfCookie;
    }
}
