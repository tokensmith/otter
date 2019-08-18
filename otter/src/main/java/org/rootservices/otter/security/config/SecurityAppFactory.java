package org.rootservices.otter.security.config;

import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;

public class SecurityAppFactory {

    public JwtAppFactory jwtAppFactory() {
        return new JwtAppFactory();
    }

    public DoubleSubmitCSRF doubleSubmitCSRF() {
        return new DoubleSubmitCSRF(jwtAppFactory(), new RandomString());
    }
}
