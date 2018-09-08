package org.rootservices.otter.security.builder;


import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BetweenBuilder<S, U> {
    private static String CSRF_NAME = "csrfToken";
    private static String SESSION_NAME = "session";

    private OtterAppFactory<S, U> otterAppFactory;
    private Boolean secure;
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;

    private List<Between<S,U>> before = new ArrayList<>();
    private List<Between<S,U>> after = new ArrayList<>();

    public BetweenBuilder<S, U> otterFactory(OtterAppFactory<S,U> otterAppFactory) {
        this.otterAppFactory = otterAppFactory;
        return this;
    }

    public BetweenBuilder<S, U> secure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public BetweenBuilder<S, U> signKey(SymmetricKey signKey) {
        this.signKey = signKey;
        return this;
    }

    public BetweenBuilder<S, U> rotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
        return this;
    }

    public BetweenBuilder<S, U> csrf() {
        CookieConfig csrfCookieConfig = new CookieConfig(CSRF_NAME, secure, -1);
        DoubleSubmitCSRF doubleSubmitCSRF = new DoubleSubmitCSRF(new JwtAppFactory(), new RandomString(), signKey, rotationSignKeys);

        Between<S,U> checkCSRF = new CheckCSRF<S, U>(CSRF_NAME, CSRF_NAME, doubleSubmitCSRF);
        before.add(checkCSRF);

        Between<S,U> prepareCSRF = new PrepareCSRF<S, U>(csrfCookieConfig, doubleSubmitCSRF);
        after.add(prepareCSRF);

        return this;
    }

    public BetweenBuilder<S, U> encKey(SymmetricKey encKey) {
        this.encKey = encKey;
        return this;
    }

    public BetweenBuilder<S, U> rotationEncKey(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
        return this;
    }

    public BetweenBuilder<S, U> session(Class<S> clazz) {
        CookieConfig sessionCookieConfig = new CookieConfig(SESSION_NAME, secure, -1);

        Between<S,U> decryptSession = new DecryptSession<S, U>(clazz, SESSION_NAME, new JwtAppFactory(), encKey, rotationEncKeys, otterAppFactory.objectReader(), true);
        before.add(decryptSession);

        Between<S,U> encryptSession = new EncryptSession<S, U>(sessionCookieConfig, encKey, otterAppFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }


    public Betweens<S,U> build() {
        return new Betweens<S,U>(before, after);
    }
}
