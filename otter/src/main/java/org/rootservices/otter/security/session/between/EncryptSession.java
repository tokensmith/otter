package org.rootservices.otter.security.session.between;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.builder.compact.EncryptedCompactBuilder;
import org.rootservices.jwt.builder.exception.CompactException;
import org.rootservices.jwt.entity.jwe.EncryptionAlgorithm;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.security.session.between.exception.EncryptSessionException;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.exception.HaltException;

import java.io.ByteArrayOutputStream;


/**
 * Intended to be used after a resource has processed the request. This will encrypt the
 * session which will become a cookie.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class EncryptSession<S, U> implements Between<S, U> {
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
    public void process(Method method, Request<S, U> request, Response<S> response) throws HaltException {
        if (shouldEncrypt(request, response)) {
            ByteArrayOutputStream session;

            try {
                session = encrypt(response.getSession().get());
            } catch (EncryptSessionException e) {
                LOGGER.error(e.getMessage(), e);
                HaltException haltException = new HaltException(COULD_NOT_ENCRYPT_SESSION, e);
                onHalt(haltException, response);
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
        response.setStatusCode(StatusCode.SERVER_ERROR);
    }

    protected Boolean shouldEncrypt(Request<S, U> request, Response<S> response) {
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

    protected ByteArrayOutputStream encrypt(S session) throws EncryptSessionException {
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
