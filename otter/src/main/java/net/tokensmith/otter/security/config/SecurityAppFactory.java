package net.tokensmith.otter.security.config;

import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;
import net.tokensmith.otter.security.session.util.Decrypt;

import java.util.Map;

public class SecurityAppFactory {

    public JwtAppFactory jwtAppFactory() {
        return new JwtAppFactory();
    }

    public DoubleSubmitCSRF doubleSubmitCSRF(SymmetricKey signKey, Map<String, SymmetricKey> rotationSignKeys) {
        return new DoubleSubmitCSRF(jwtAppFactory(), new RandomString(), signKey, rotationSignKeys);
    }

    public <S> Decrypt<S> decrypt(ObjectReader sessionObjectReader, SymmetricKey encKey, Map<String, SymmetricKey> rotationEncKeys) {
        return new Decrypt<S>(jwtAppFactory(), sessionObjectReader, encKey, rotationEncKeys);
    }
}
