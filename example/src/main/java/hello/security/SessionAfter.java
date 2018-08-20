package hello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.Base64;

public class SessionAfter extends EncryptSession<TokenSession> {
    public SessionAfter(JwtAppFactory jwtAppFactory, Base64.Decoder decoder, ObjectMapper objectMapper) {
        super(jwtAppFactory, decoder, objectMapper);
    }

    public SessionAfter(CookieConfig cookieConfig, JwtAppFactory jwtAppFactory, Base64.Decoder decoder, SymmetricKey preferredKey, ObjectMapper objectMapper) {
        super(cookieConfig, jwtAppFactory, decoder, preferredKey, objectMapper);
    }
}
