package org.rootservices.otter.security.builder;


import com.fasterxml.jackson.databind.ObjectReader;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.security.exception.SessionCtorException;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BetweenBuilder<S, U, P> {
    private static String CSRF_NAME = "csrfToken";
    private static String SESSION_NAME = "session";
    public static final String COULD_NOT_ACCESS_SESSION_CTORS = "Could not access session copy constructor";

    private OtterAppFactory otterAppFactory;
    private Boolean secure;
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Class<S> sessionClass;
    private ObjectReader sessionObjectReader;
    private Constructor<S> sessionCtor;

    private List<Between<S, U, P>> before = new ArrayList<>();
    private List<Between<S, U, P>> after = new ArrayList<>();

    public BetweenBuilder<S, U, P> otterFactory(OtterAppFactory otterAppFactory) {
        this.otterAppFactory = otterAppFactory;
        return this;
    }

    public BetweenBuilder<S, U, P> secure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public BetweenBuilder<S, U, P> signKey(SymmetricKey signKey) {
        this.signKey = signKey;
        return this;
    }

    public BetweenBuilder<S, U, P> rotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
        return this;
    }

    public BetweenBuilder<S, U, P> csrfPrepare() {
        CookieConfig csrfCookieConfig = new CookieConfig(CSRF_NAME, secure, -1);
        DoubleSubmitCSRF doubleSubmitCSRF = new DoubleSubmitCSRF(new JwtAppFactory(), new RandomString(), signKey, rotationSignKeys);

        Between<S, U, P> prepareCSRF = new PrepareCSRF<S, U, P>(csrfCookieConfig, doubleSubmitCSRF);
        before.add(prepareCSRF);

        return this;
    }

    public BetweenBuilder<S, U, P> csrfProtect() {
        DoubleSubmitCSRF doubleSubmitCSRF = new DoubleSubmitCSRF(new JwtAppFactory(), new RandomString(), signKey, rotationSignKeys);

        Between<S, U, P> checkCSRF = new CheckCSRF<S, U, P>(CSRF_NAME, CSRF_NAME, doubleSubmitCSRF);
        before.add(checkCSRF);

        return this;
    }


    public BetweenBuilder<S, U, P> encKey(SymmetricKey encKey) {
        this.encKey = encKey;
        return this;
    }

    public BetweenBuilder<S, U, P> rotationEncKey(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
        return this;
    }

    public BetweenBuilder<S, U, P> sessionClass(Class<S> sessionClass) {
        this.sessionClass = sessionClass;
        this.sessionObjectReader =  otterAppFactory.objectReader().forType(sessionClass);
        return this;
    }

    public BetweenBuilder<S, U, P> session() throws SessionCtorException {
        CookieConfig sessionCookieConfig = new CookieConfig(SESSION_NAME, secure, -1);

        try {
            sessionCtor = sessionClass.getConstructor(sessionClass);
        } catch (NoSuchMethodException e) {
            throw new SessionCtorException(COULD_NOT_ACCESS_SESSION_CTORS, e);
        }

        Between<S,U,P> decryptSession = new DecryptSession<S, U, P>(sessionCtor, SESSION_NAME, new JwtAppFactory(), encKey, rotationEncKeys, sessionObjectReader, true);
        before.add(decryptSession);

        Between<S,U,P> encryptSession = new EncryptSession<S, U, P>(sessionCookieConfig, encKey, otterAppFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }

    public BetweenBuilder<S, U, P> optionalSession() throws SessionCtorException {
        CookieConfig sessionCookieConfig = new CookieConfig(SESSION_NAME, secure, -1);

        try {
            sessionCtor = sessionClass.getConstructor(sessionClass);
        } catch (NoSuchMethodException e) {
            throw new SessionCtorException(COULD_NOT_ACCESS_SESSION_CTORS, e);
        }

        Between<S, U, P> decryptSession = new DecryptSession<S, U, P>(sessionCtor, SESSION_NAME, new JwtAppFactory(), encKey, rotationEncKeys, sessionObjectReader, false);
        before.add(decryptSession);

        Between<S, U, P> encryptSession = new EncryptSession<S, U, P>(sessionCookieConfig, encKey, otterAppFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }

    public Betweens<S, U, P> build() {
        return new Betweens<S, U, P>(before, after);
    }
}
