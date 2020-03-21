package net.tokensmith.otter.security.builder;


import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.builder.entity.Betweens;
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

public class BetweenBuilder<S, U> {
    private static String CSRF_NAME = "csrfToken";
    private static String SESSION_NAME = "session";
    public static final String COULD_NOT_ACCESS_SESSION_CTORS = "Could not access session copy constructor";

    private TranslatorAppFactory appFactory;
    private Boolean secure;
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private StatusCode csrfFailStatusCode;
    private Optional<String> csrfFailTemplate;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;
    private StatusCode sessionFailStatusCode;
    private Optional<String> sessionFailTemplate;

    private Class<S> sessionClass;
    private ObjectReader sessionObjectReader;
    private Constructor<S> sessionCtor;

    private List<Between<S,U>> before = new ArrayList<>();
    private List<Between<S,U>> after = new ArrayList<>();

    public BetweenBuilder<S, U> routerAppFactory(TranslatorAppFactory appFactory) {
        this.appFactory = appFactory;
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

    public BetweenBuilder<S, U> sessionFailTemplate(Optional<String> sessionFailTemplate) {
        this.sessionFailTemplate = sessionFailTemplate;
        return this;
    }

    public BetweenBuilder<S, U> sessionFailStatusCode(StatusCode sessionFailStatusCode) {
        this.sessionFailStatusCode = sessionFailStatusCode;
        return this;
    }

    public BetweenBuilder<S, U> csrfPrepare() {
        CookieConfig csrfCookieConfig = new CookieConfig(CSRF_NAME, secure, -1, true);
        DoubleSubmitCSRF doubleSubmitCSRF = new DoubleSubmitCSRF(new JwtAppFactory(), new RandomString(), signKey, rotationSignKeys);

        Between<S,U> prepareCSRF = new PrepareCSRF<S, U>(csrfCookieConfig, doubleSubmitCSRF);
        before.add(prepareCSRF);

        return this;
    }

    public BetweenBuilder<S, U> csrfProtect() {
        DoubleSubmitCSRF doubleSubmitCSRF = new DoubleSubmitCSRF(new JwtAppFactory(), new RandomString(), signKey, rotationSignKeys);

        Between<S,U> checkCSRF = new CheckCSRF<S, U>(CSRF_NAME, CSRF_NAME, doubleSubmitCSRF, csrfFailStatusCode, csrfFailTemplate);
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

    public BetweenBuilder<S, U> csrfFailTemplate(Optional<String> csrfFailTemplate) {
        this.csrfFailTemplate = csrfFailTemplate;
        return this;
    }

    public BetweenBuilder<S, U> csrfFailStatusCode(StatusCode csrfFailStatusCode) {
        this.csrfFailStatusCode = csrfFailStatusCode;
        return this;
    }

    public BetweenBuilder<S, U> sessionClass(Class<S> sessionClass) {
        this.sessionClass = sessionClass;
        this.sessionObjectReader =  appFactory.objectReader().forType(sessionClass);
        return this;
    }

    public BetweenBuilder<S, U> session() throws SessionCtorException {
        CookieConfig sessionCookieConfig = new CookieConfig(SESSION_NAME, secure, -1, true);

        try {
            sessionCtor = sessionClass.getConstructor(sessionClass);
        } catch (NoSuchMethodException e) {
            throw new SessionCtorException(COULD_NOT_ACCESS_SESSION_CTORS, e);
        }

        Decrypt<S> decrypt = new Decrypt<S>(new JwtAppFactory(), sessionObjectReader, encKey, rotationEncKeys);
        Between<S,U> decryptSession = new DecryptSession<S, U>(sessionCtor, SESSION_NAME, sessionFailStatusCode, sessionFailTemplate, true, decrypt);
        before.add(decryptSession);

        Between<S,U> encryptSession = new EncryptSession<S, U>(sessionCookieConfig, encKey, appFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }

    public BetweenBuilder<S, U> optionalSession() throws SessionCtorException {
        CookieConfig sessionCookieConfig = new CookieConfig(SESSION_NAME, secure, -1, true);

        try {
            sessionCtor = sessionClass.getConstructor(sessionClass);
        } catch (NoSuchMethodException e) {
            throw new SessionCtorException(COULD_NOT_ACCESS_SESSION_CTORS, e);
        }

        Decrypt<S> decrypt = new Decrypt<S>(new JwtAppFactory(), sessionObjectReader, encKey, rotationEncKeys);
        Between<S,U> decryptSession = new DecryptSession<S, U>(sessionCtor, SESSION_NAME, sessionFailStatusCode, sessionFailTemplate, false, decrypt);
        before.add(decryptSession);

        Between<S,U> encryptSession = new EncryptSession<S, U>(sessionCookieConfig, encKey, appFactory.objectWriter());
        after.add(encryptSession);

        return this;
    }

    public Betweens<S,U> build() {
        return new Betweens<S,U>(before, after);
    }
}
