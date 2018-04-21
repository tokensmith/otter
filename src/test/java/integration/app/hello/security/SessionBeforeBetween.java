package integration.app.hello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.security.session.between.DecryptSession;

import java.util.Map;

public class SessionBeforeBetween extends DecryptSession<TokenSession> {

    public SessionBeforeBetween(String sessionCookieName, JwtAppFactory jwtAppFactory, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys, ObjectMapper objectMapper) {
        super(sessionCookieName, jwtAppFactory, preferredKey, rotationKeys, objectMapper);
    }
}
