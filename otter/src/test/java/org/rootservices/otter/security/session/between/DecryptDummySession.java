package org.rootservices.otter.security.session.between;

import com.fasterxml.jackson.databind.ObjectReader;
import helper.entity.DummySession;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;

import java.util.Map;

public class DecryptDummySession extends DecryptSession<DummySession> {
    public DecryptDummySession(String sessionCookieName, JwtAppFactory jwtAppFactory, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys, ObjectReader objectReader) {
        super(DummySession.class, sessionCookieName, jwtAppFactory, preferredKey, rotationKeys, objectReader);
    }

    @Override
    protected DummySession copy(DummySession from) {
        return new DummySession(from);
    }
}
