package net.tokensmith.otter.security.builder;


import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.config.SecurityAppFactory;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;
import net.tokensmith.otter.security.csrf.between.html.CheckCSRF;
import net.tokensmith.otter.security.csrf.between.html.PrepareCSRF;
import net.tokensmith.otter.security.session.util.Decrypt;
import net.tokensmith.otter.security.session.between.html.DecryptSession;
import net.tokensmith.otter.security.session.between.html.EncryptSession;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class BetweenBuilder<S, U> {
    private static SecurityAppFactory securityAppFactory = new SecurityAppFactory();

    // #186: review how these cookie names are used.
    private static String CSRF_NAME = "csrfToken";
    private static String SESSION_NAME = "session";
    public static final String COULD_NOT_ACCESS_SESSION_CTORS = "Could not access session copy constructor";

    private TranslatorAppFactory appFactory;

    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts;

    // csrf
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private CookieConfig csrfCookieConfig;

    // session
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;
    private CookieConfig sessionCookieConfig;

    private Class<S> sessionClass;
    private ObjectReader sessionObjectReader;
    private Constructor<S> sessionCtor;

    private List<Between<S,U>> before = new ArrayList<>();
    private List<Between<S,U>> after = new ArrayList<>();

    public BetweenBuilder<S, U> routerAppFactory(TranslatorAppFactory appFactory) {
        this.appFactory = appFactory;
        return this;
    }

    public BetweenBuilder<S, U> onHalts(Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        this.onHalts = onHalts;
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

    public BetweenBuilder<S, U> csrfCookieConfig(CookieConfig csrfCookieConfig) {
        this.csrfCookieConfig = csrfCookieConfig;
        return this;
    }

    public BetweenBuilder<S, U> sessionCookieConfig(CookieConfig sessionCookieConfig) {
        this.sessionCookieConfig = sessionCookieConfig;
        return this;
    }

    public BetweenBuilder<S, U> csrfPrepare() {
        DoubleSubmitCSRF doubleSubmitCSRF = securityAppFactory.doubleSubmitCSRF(signKey, rotationSignKeys);

        Between<S,U> prepareCSRF = new PrepareCSRF<S, U>(csrfCookieConfig, doubleSubmitCSRF);
        before.add(prepareCSRF);

        return this;
    }

    public BetweenBuilder<S, U> csrfProtect() {
        DoubleSubmitCSRF doubleSubmitCSRF = securityAppFactory.doubleSubmitCSRF(signKey, rotationSignKeys);
        Between<S,U> checkCSRF = new CheckCSRF<S, U>(CSRF_NAME, CSRF_NAME, doubleSubmitCSRF, onHalts.get(Halt.CSRF));
        before.add(checkCSRF);

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

    public BetweenBuilder<S, U> sessionClass(Class<S> sessionClass) {
        this.sessionClass = sessionClass;
        this.sessionObjectReader =  appFactory.objectReader().forType(sessionClass);
        return this;
    }

    public BetweenBuilder<S, U> session() throws SessionCtorException {
        try {
            sessionCtor = sessionClass.getConstructor(sessionClass);
        } catch (NoSuchMethodException e) {
            throw new SessionCtorException(COULD_NOT_ACCESS_SESSION_CTORS, e);
        }

        Decrypt<S> decrypt = securityAppFactory.decrypt(sessionObjectReader, encKey, rotationEncKeys);

        Between<S,U> decryptSession = new DecryptSession<S, U>(sessionCtor, SESSION_NAME, onHalts.get(Halt.SESSION), true, decrypt);
        before.add(decryptSession);

        Between<S,U> encryptSession = new EncryptSession<S, U>(sessionCookieConfig, encKey, appFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }

    public BetweenBuilder<S, U> optionalSession() throws SessionCtorException {
        try {
            sessionCtor = sessionClass.getConstructor(sessionClass);
        } catch (NoSuchMethodException e) {
            throw new SessionCtorException(COULD_NOT_ACCESS_SESSION_CTORS, e);
        }

        Decrypt<S> decrypt = securityAppFactory.decrypt(sessionObjectReader, encKey, rotationEncKeys);

        Between<S,U> decryptSession = new DecryptSession<S, U>(sessionCtor, SESSION_NAME, onHalts.get(Halt.SESSION), false, decrypt);
        before.add(decryptSession);

        Between<S,U> encryptSession = new EncryptSession<S, U>(sessionCookieConfig, encKey, appFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }

    public Betweens<S,U> build() {
        return new Betweens<S,U>(before, after);
    }
}
