package org.rootservices.otter.security.session.between;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
public class EncryptSession implements Between {
    public static final String NO_SESSION = "Response did not have a session. Unable to set cookie session for response";
    protected static Logger LOGGER = LogManager.getLogger(EncryptSession.class);

    private CookieConfig cookieConfig;
    private JwtAppFactory jwtAppFactory;
    private Base64.Decoder decoder;
    private SymmetricKey preferredKey;
    private ObjectMapper objectMapper;

    public EncryptSession(CookieConfig cookieConfig, JwtAppFactory jwtAppFactory, Base64.Decoder decoder, SymmetricKey preferredKey, ObjectMapper objectMapper) {
        this.cookieConfig = cookieConfig;
        this.jwtAppFactory = jwtAppFactory;
        this.decoder = decoder;
        this.preferredKey = preferredKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Method method, Request request, Response response) throws HaltException {
        if (response.getSession().isPresent()) {
            ByteArrayOutputStream session;

            try {
                session = encrypt(response.getSession().get());
            } catch (EncryptSessionException e) {
                LOGGER.error(e.getMessage(), e);
                return;
            }

            Cookie cookie = new Cookie();
            cookie.setName(cookieConfig.getName());
            cookie.setMaxAge(cookieConfig.getAge());
            cookie.setSecure(cookieConfig.getSecure());
            cookie.setValue(session.toString());

            response.getCookies().put(cookieConfig.getName(), cookie);
        } else {
            LOGGER.error(NO_SESSION);
        }
    }

    protected ByteArrayOutputStream encrypt(Session session) throws EncryptSessionException {
        byte[] payload;

        try {
            payload = objectMapper.writeValueAsBytes(session);
        } catch (JsonProcessingException e) {
            throw new EncryptSessionException(e.getMessage(), e);
        }

        JweSerializer jweSerializer = jwtAppFactory.jweDirectSerializer();

        Header header = new Header();
        header.setAlgorithm(Algorithm.DIRECT);
        header.setEncryptionAlgorithm(Optional.of(EncryptionAlgorithm.AES_GCM_256));
        header.setKeyId(preferredKey.getKeyId());

        JWE jwe = new JWE();
        jwe.setHeader(header);
        jwe.setCek(decoder.decode(preferredKey.getKey().getBytes()));
        jwe.setPayload(payload);

        ByteArrayOutputStream compactJwe;
        try {
            compactJwe = jweSerializer.JWEToCompact(jwe);
        } catch (JsonToJwtException | CipherException | EncryptException e) {
            throw new EncryptSessionException(e.getMessage(), e);
        }
        return compactJwe;
    }
}