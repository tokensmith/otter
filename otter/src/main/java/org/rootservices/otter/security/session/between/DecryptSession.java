package org.rootservices.otter.security.session.between;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.security.session.between.exception.InvalidSessionException;
import org.rootservices.otter.security.session.between.exception.SessionDecryptException;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.session.between.exception.SessionCtorException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;


/**
 * Used to encrypt a session cookie.
 */
public abstract class DecryptSession<T extends Session> implements Between<T> {
    public static final String NOT_A_JWT = "Session cookie was not a JWE: %s";
    public static final String COULD_NOT_GET_HEADER_JWE = "Session cookie did have a header member: %s";
    public static final String COULD_NOT_DESERIALIZE_JWE = "Session cookie could not be de-serialized to JSON: %s";
    public static final String COULD_NOT_DECRYPT_JWE = "Session cookie could not be decrypted: %s";
    public static final String COULD_NOT_DESERIALIZE = "decrypted payload could be deserialized to session: %s";
    public static final String INVALID_SESSION_COOKIE = "Invalid value for the session cookie";
    public static final String COOKIE_NOT_PRESENT = "session cookie not present.";
    protected static Logger LOGGER = LogManager.getLogger(DecryptSession.class);

    private Class<T> clazz;
    private String sessionCookieName;
    private JwtAppFactory jwtAppFactory;
    private SymmetricKey preferredKey;
    private Map<String, SymmetricKey> rotationKeys;
    private ObjectMapper objectMapper;

    public DecryptSession(Class<T> clazz, String sessionCookieName, JwtAppFactory jwtAppFactory, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys, ObjectMapper objectMapper) {
        this.clazz = clazz;
        this.sessionCookieName = sessionCookieName;
        this.jwtAppFactory = jwtAppFactory;
        this.preferredKey = preferredKey;
        this.rotationKeys = rotationKeys;
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Method method, Request<T> request, Response<T> response) throws HaltException {
        Optional<T> session;
        Cookie sessionCookie = request.getCookies().get(sessionCookieName);

        if (sessionCookie == null) {
            HaltException halt = new HaltException(COOKIE_NOT_PRESENT);
            onHalt(halt, response);
            throw halt;
        }

        try {
            session = Optional.of(decrypt(sessionCookie.getValue()));
        } catch (InvalidSessionException e) {
            LOGGER.error(e.getMessage(), e);
            HaltException halt = new HaltException(INVALID_SESSION_COOKIE, e);
            onHalt(halt, response);
            throw halt;
        } catch (SessionDecryptException e) {
            LOGGER.error(e.getMessage(), e);
            HaltException halt = new HaltException(INVALID_SESSION_COOKIE, e);
            onHalt(halt, response);
            throw halt;
        }

        // Copies the request session and assigns it to, response.
        // This is required because the after filter, EncryptSession, does an .equals() to
        // determine if the session should be re encrypted.
        request.setSession(session);
        T responseSession;

        responseSession = copy(session.get());
        response.setSession(Optional.of(responseSession));
    }

    /**
     * Copies T and then returns the copy.
     *
     * @param from the session to copy
     * @return an instance of T that is a copy of session
     * @throws SessionCtorException if a constructor cant be found for T
     */
    abstract protected T copy(T from);

    /**
     * This method will be called before a Halt Exception is thrown.
     * Override this method if you wish to change the behavior on the
     * response right before a Halt Exception is going to be thrown.
     * An Example would be, you may want to redirect the user to a login page.
     *
     * @param e a HaltException
     * @param response a Response
     */
    protected void onHalt(HaltException e, Response response) {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.getCookies().remove(sessionCookieName);
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
