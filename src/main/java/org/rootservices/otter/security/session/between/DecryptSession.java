package org.rootservices.otter.security.session.between;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.exception.InvalidJWT;
import org.rootservices.jwt.jwe.entity.JWE;
import org.rootservices.jwt.jwe.factory.exception.CipherException;
import org.rootservices.jwt.jwe.serialization.JweDeserializer;
import org.rootservices.jwt.jwe.serialization.exception.KeyException;
import org.rootservices.jwt.serialization.HeaderDeserializer;
import org.rootservices.jwt.serialization.exception.DecryptException;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.security.session.between.exception.InvalidSessionException;
import org.rootservices.otter.security.session.between.exception.SessionDecryptException;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;


/**
 * Used to encrypt a session cookie.
 */
public class DecryptSession<T extends Session> implements Between {
    public static final String NOT_A_JWT = "Session cookie was not a JWE: %s";
    public static final String COULD_NOT_GET_HEADER_JWE = "Session cookie did have a header member: %s";
    public static final String COULD_NOT_DESERIALIZE_JWE = "Session cookie could not be de-serialized to JSON: %s";
    public static final String COULD_NOT_DECRYPT_JWE = "Session cookie could not be decrypted: %s";
    public static final String COULD_NOT_DESERIALIZE = "decrypted payload could be deserialized to session: %s";
    public static final String INVALID_SESSION_COOKIE = "Invalid value for the session cookie";
    public static final String COULD_NOT_DECRYPT_SESSION = "Could not decrypt the session cookie";
    protected static Logger LOGGER = LogManager.getLogger(DecryptSession.class);

    private Class clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private String sessionCookieName;
    private JwtAppFactory jwtAppFactory;
    private SymmetricKey preferredKey;
    private Map<String, SymmetricKey> rotationKeys;
    private ObjectMapper objectMapper;

    public DecryptSession(String sessionCookieName, JwtAppFactory jwtAppFactory, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys, ObjectMapper objectMapper) {
        this.sessionCookieName = sessionCookieName;
        this.jwtAppFactory = jwtAppFactory;
        this.preferredKey = preferredKey;
        this.rotationKeys = rotationKeys;
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Method method, Request request, Response response) throws HaltException {
        Optional<Session> session;
        Cookie sessionCookie = request.getCookies().get(sessionCookieName);

        try {
            session = Optional.of(decrypt(sessionCookie.getValue()));
        } catch (InvalidSessionException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HaltException(INVALID_SESSION_COOKIE, e);
        } catch (SessionDecryptException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HaltException(COULD_NOT_DECRYPT_SESSION, e);
        }

        request.setSession(session);
        response.setSession(session);
    }

    protected T decrypt(String encryptedSession) throws InvalidSessionException, SessionDecryptException {

        // extract the header to figure out what key to use as cek.
        HeaderDeserializer headerDeserializer = jwtAppFactory.headerDeserializer();
        org.rootservices.jwt.entity.jwt.header.Header sessionHeader;
        try {
            sessionHeader = headerDeserializer.toHeader(encryptedSession);
        } catch (JsonToJwtException e) {
            String msg = String.format(NOT_A_JWT, encryptedSession);
            throw new InvalidSessionException(msg, e);
        } catch (InvalidJWT e) {
            String msg = String.format(COULD_NOT_GET_HEADER_JWE, encryptedSession);
            throw new InvalidSessionException(msg, e);
        }

        // get the cek.
        SymmetricKey key = getKey(sessionHeader.getKeyId().get());

        // decrypt the session
        JweDeserializer deserializer = jwtAppFactory.jweDirectDesializer();
        JWE sessionPayload;
        try {
            sessionPayload = deserializer.stringToJWE(encryptedSession, key);
        } catch (JsonToJwtException e) {
            String msg = String.format(COULD_NOT_DESERIALIZE_JWE, encryptedSession);
            throw new InvalidSessionException(msg, e);
        } catch (DecryptException | CipherException | KeyException e) {
            String msg = String.format(COULD_NOT_DECRYPT_JWE, encryptedSession);
            throw new SessionDecryptException(msg, e);
        }

        return toSession(sessionPayload.getPayload());
    }

    protected T toSession(byte[] json) {
        T session = null;
        try {
            session = (T) objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            String msg = String.format(COULD_NOT_DESERIALIZE, new String(json, StandardCharsets.UTF_8));
            LOGGER.error(msg);
            LOGGER.error(e.getMessage(), e);
        }
        return session;
    }

    protected SymmetricKey getKey(String keyId) {
        SymmetricKey key;
        if (preferredKey.getKeyId().get().equals(keyId)) {
            key = preferredKey;
        } else {
            key = rotationKeys.get(keyId);
        }
        return key;
    }
}
