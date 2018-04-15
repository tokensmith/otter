package org.rootservices.otter.security.session.between;

import com.fasterxml.jackson.databind.ObjectMapper;
import helper.entity.DummySession;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;

import java.util.Map;

public class DecryptDummySession extends DecryptSession<DummySession> {
    public DecryptDummySession(String sessionCookieName, JwtAppFactory jwtAppFactory, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys, ObjectMapper objectMapper) {
        super(sessionCookieName, jwtAppFactory, preferredKey, rotationKeys, objectMapper);
    }
}