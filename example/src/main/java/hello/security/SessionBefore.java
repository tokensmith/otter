package hello.security;

import com.fasterxml.jackson.databind.ObjectReader;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.security.session.between.DecryptSession;

import java.util.Map;

public class SessionBefore extends DecryptSession<TokenSession> {

    public SessionBefore(String sessionCookieName, JwtAppFactory jwtAppFactory, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys, ObjectReader objectReader) {
        super(TokenSession.class, sessionCookieName, jwtAppFactory, preferredKey, rotationKeys, objectReader);
    }

    @Override
    protected TokenSession copy(TokenSession from) {
        return new TokenSession(from);
    }
}
