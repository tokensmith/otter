package net.tokensmith.otter.security.cookie;

import net.tokensmith.jwt.builder.compact.SecureCompactBuilder;
import net.tokensmith.jwt.builder.exception.CompactException;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwt.Claims;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.exception.SignatureException;
import net.tokensmith.jwt.jws.verifier.VerifySignature;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.security.cookie.either.CookieError;
import net.tokensmith.otter.security.cookie.either.ReadError;
import net.tokensmith.otter.security.cookie.either.ReadEither;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CookieSigner implements CookieSecurity {
    protected static Logger LOGGER = LoggerFactory.getLogger(CookieSigner.class);
    public static final String SIGNATURE_INVALID = "Signature Invalid";

    private JwtAppFactory jwtAppFactory;
    private Map<String, SymmetricKey> keys;
    private Map<String, String> preferredKeys; // [cookie-name][preferred-key-id]

    public CookieSigner(JwtAppFactory jwtAppFactory, Map<String, SymmetricKey> keys, Map<String, String> preferredKeys) {
        this.jwtAppFactory = jwtAppFactory;
        this.keys = keys;
        this.preferredKeys = preferredKeys;
    }

    @Override
    public <T extends Claims> Cookie make(CookieConfig cookieConfig, T claims) throws CookieJwtException {

        String preferredKeyId = preferredKeys.get(cookieConfig.getName());
        SymmetricKey preferredKey = getKey(preferredKeyId);

        SecureCompactBuilder compactBuilder = new SecureCompactBuilder();
        ByteArrayOutputStream compactJwt;
        try {
            compactJwt = compactBuilder.alg(Algorithm.HS256)
                    .key(preferredKey)
                    .claims(claims)
                    .build();
        } catch (CompactException e) {
            throw new CookieJwtException("Could not serialize to compact jwt", e);
        }

        return new Cookie.Builder()
                .secure(cookieConfig.getSecure())
                .name(cookieConfig.getName())
                .maxAge(cookieConfig.getAge())
                .value(compactJwt.toString())
                .httpOnly(cookieConfig.getHttpOnly())
                .build();
    }

    @Override
    public <T extends Claims> ReadEither<T> read(String value, Class<T> claimClazz) {
        ReadEither.Builder<T> readEither = new ReadEither.Builder<>();

        JsonWebToken<T> jwt;
        try {
            jwt = toJwt(value, claimClazz);
        } catch (CookieJwtException e) {
            LOGGER.debug(e.getMessage(), e);
            ReadError<T> error = new ReadError.Builder<T>()
                .cause(e)
                .cookieError(CookieError.JWT_INVALID)
                .build();
            readEither.right(error);
            return readEither.build();
        }

        if (jwt.getHeader().getKeyId().isPresent()) {
            SymmetricKey signKey = getKey(jwt.getHeader().getKeyId().get());

            // protect NPE
            if (Objects.isNull(signKey)) {
                LOGGER.debug("do not have key id: {}", jwt.getHeader().getKeyId().get());
                ReadError<T> error = new ReadError.Builder<T>()
                        .claims(Optional.of(jwt.getClaims()))
                        .cookieError(CookieError.SIGNATURE_ERROR)
                        .build();
                readEither.right(error);
                return readEither.build();
            }

            Boolean signatureValid;
            try {
                signatureValid = verifySignature(jwt, signKey);
            } catch (CookieJwtException e) {
                LOGGER.debug(e.getMessage(), e);
                ReadError<T> error = new ReadError.Builder<T>()
                    .claims(Optional.of(jwt.getClaims()))
                    .cookieError(CookieError.SIGNATURE_ERROR)
                    .cause(e)
                    .build();
                readEither.right(error);
                return readEither.build();
            }

            if (!signatureValid) {
                LOGGER.debug(SIGNATURE_INVALID);
                ReadError<T> error = new ReadError.Builder<T>()
                    .claims(Optional.of(jwt.getClaims()))
                    .cookieError(CookieError.SIGNATURE_INVALID)
                    .build();
                readEither.right(error);
                return readEither.build();
            }
        } else {
            ReadError<T> error = new ReadError.Builder<T>()
                .claims(Optional.of(jwt.getClaims()))
                .cookieError(CookieError.NO_KEY_ID)
                .build();
            readEither.right(error);
            return readEither.build();
        }

        readEither.left(jwt.getClaims());
        return readEither.build();
    }

    @Override
    public SymmetricKey getKey(String keyId) {
        return keys.get(keyId);
    }

    protected <T extends Claims> JsonWebToken<T> toJwt(String encodedValue, Class<T> claimsClazz) throws CookieJwtException {
        JwtSerde jwtSerde = jwtAppFactory.jwtSerde();

        JsonWebToken<T> jsonWebToken;
        try {
            jsonWebToken = jwtSerde.stringToJwt(encodedValue, claimsClazz);
        } catch (JsonToJwtException | InvalidJWT e) {
            throw new CookieJwtException("Could not deserialize CSRF JWT to pojo", e);
        }

        return jsonWebToken;
    }

    protected <T extends Claims> Boolean verifySignature(JsonWebToken<T> csrfJwt, SymmetricKey signKey) throws CookieJwtException {

        VerifySignature verifySignature;
        try {
            verifySignature = jwtAppFactory.verifySignature(
                    csrfJwt.getHeader().getAlgorithm(), signKey
            );
        } catch (SignatureException e) {
            throw new CookieJwtException("Could not verify signature", e);
        }

        return verifySignature.run(csrfJwt);
    }
}
