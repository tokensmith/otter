package net.tokensmith.otter.security.config;

import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;

public class SecurityAppFactory {

    public JwtAppFactory jwtAppFactory() {
        return new JwtAppFactory();
    }

    public DoubleSubmitCSRF doubleSubmitCSRF() {
        return new DoubleSubmitCSRF(jwtAppFactory(), new RandomString());
    }
}
