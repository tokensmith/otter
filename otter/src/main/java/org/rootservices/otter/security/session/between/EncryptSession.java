package org.rootservices.otter.security.session.between;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.builder.compact.EncryptedCompactBuilder;
import org.rootservices.jwt.builder.exception.CompactException;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwe.EncryptionAlgorithm;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.entity.jwt.header.Header;
import org.rootservices.jwt.jwe.entity.JWE;
import org.rootservices.jwt.jwe.factory.exception.CipherException;
import org.rootservices.jwt.jwe.serialization.JweSerializer;
import org.rootservices.jwt.serialization.exception.EncryptException;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.security.session.between.exception.EncryptSessionException;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;

/**
 * Intended to be used after a resource has processed the request. This will encrypt the
 * session which will become a cookie.
 */
public class EncryptSession<T extends Session> implements Between<T> {
    public static final String NOT_ENCRYPTING = "Not re-encrypting session cookie";
    public static final String COULD_NOT_ENCRYPT_SESSION = "Could not encrypt session cookie";
    protected static Logger LOGGER = LogManager.getLogger(EncryptSession.class);

    private CookieConfig cookieConfig;
    private SymmetricKey preferredKey;
    private ObjectWriter objectWriter;


    public EncryptSession(CookieConfig cookieConfig, SymmetricKey preferredKey, ObjectWriter objectWriter) {
        this.cookieConfig = cookieConfig;
        this.preferredKey = preferredKey;
        this.objectWriter = objectWriter;
    }

    @Override
    public void process(Method method, Request<T> request, Response<T> response) throws HaltException {
        if (shouldEncrypt(request, response)) {
            ByteArrayOutputStream session;

            try {
                session = encrypt(response.getSession().get());
            } catch (EncryptSessionException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HaltException(COULD_NOT_ENCRYPT_SESSION, e);
            }

            Cookie cookie = new Cookie();
            cookie.setName(cookieConfig.getName());
            cookie.setMaxAge(cookieConfig.getAge());
            cookie.setSecure(cookieConfig.getSecure());
            cookie.setValue(session.toString());

            response.getCookies().put(cookieConfig.getName(), cookie);
        } else {
            LOGGER.debug(NOT_ENCRYPTING);
        }
    }

    protected Boolean shouldEncrypt(Request<T> request, Response<T> response) {
        if (request.getSession().isPresent() && response.getSession().isPresent()) {
            if ( response.getSession().get().equals(request.getSession().get()) ) {
                return false;
            }
            return true;
        } else if (response.getSession().isPresent()) {
            return true;
        }
        return false;
    }

    protected ByteArrayOutputStream encrypt(Session session) throws EncryptSessionException {
        byte[] payload;

        try {
            payload = objectWriter.writeValueAsBytes(session);
        } catch (JsonProcessingException e) {
            throw new EncryptSessionException(e.getMessage(), e);
        }

        EncryptedCompactBuilder compactBuilder = new EncryptedCompactBuilder();
        ByteArrayOutputStream compactJwe;
        try {
            compactJwe = compactBuilder.encAlg(EncryptionAlgorithm.AES_GCM_256)
                    .alg(Algorithm.DIRECT)
                    .payload(payload)
                    .cek(preferredKey)
                    .build();
        } catch (CompactException e) {
            throw new EncryptSessionException(e.getMessage(), e);
        }

        return compactJwe;
    }

    public CookieConfig getCookieConfig() {
        return cookieConfig;
    }

    public void setCookieConfig(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public SymmetricKey getPreferredKey() {
        return preferredKey;
    }

    public void setPreferredKey(SymmetricKey preferredKey) {
        this.preferredKey = preferredKey;
    }
}
