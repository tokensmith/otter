package net.tokensmith.otter.security.builder;

import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;
import net.tokensmith.otter.security.csrf.between.rest.RestCheckCSRF;
import net.tokensmith.otter.security.session.between.rest.RestReadSession;
import net.tokensmith.otter.security.session.util.Decrypt;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RestBetweenBuilder<S, U> {
    private static String CSRF_NAME = "csrfToken";
    private static String CSRF_HDR_NAME = "X-CSRF";
    private static String SESSION_NAME = "session";

    private TranslatorAppFactory appFactory;

    // csrf
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private StatusCode csrfFailStatusCode;
    private CookieConfig csrfCookieConfig;

    // session
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;
    private StatusCode sessionFailStatusCode;
    private CookieConfig sessionCookieConfig;

    private Class<S> sessionClazz;
    private ObjectReader sessionObjectReader;

    private List<RestBetween<S,U>> before = new ArrayList<>();
    private List<RestBetween<S,U>> after = new ArrayList<>();

    public RestBetweenBuilder<S, U> routerAppFactory(TranslatorAppFactory appFactory) {
        this.appFactory = appFactory;
        return this;
    }

    public RestBetweenBuilder<S, U> signKey(SymmetricKey signKey) {
        this.signKey = signKey;
        return this;
    }

    public RestBetweenBuilder<S, U> rotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
        return this;
    }

    public RestBetweenBuilder<S, U> csrfCookieConfig(CookieConfig csrfCookieConfig) {
        this.csrfCookieConfig = csrfCookieConfig;
        return this;
    }

    public RestBetweenBuilder<S, U> encKey(SymmetricKey encKey) {
        this.encKey = encKey;
        return this;
    }

    public RestBetweenBuilder<S, U> rotationEncKeys(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
        return this;
    }

    public RestBetweenBuilder<S, U> sessionCookieConfig(CookieConfig sessionCookieConfig) {
        this.sessionCookieConfig = sessionCookieConfig;
        return this;
    }

    public RestBetweenBuilder<S, U> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        this.sessionObjectReader =  appFactory.objectReader().forType(sessionClazz);
        return this;
    }

    public RestBetweenBuilder<S, U> sessionFailStatusCode(StatusCode sessionFailStatusCode) {
        this.sessionFailStatusCode = sessionFailStatusCode;
        return this;
    }

    public RestBetweenBuilder<S, U> session() {

        Decrypt<S> decrypt = new Decrypt<S>(new JwtAppFactory(), sessionObjectReader, encKey, rotationEncKeys);
        RestBetween<S, U> decryptSession = new RestReadSession<S, U>(SESSION_NAME, true, sessionFailStatusCode, decrypt);
        before.add(decryptSession);

        return this;
    }

    public RestBetweenBuilder<S, U> optionalSession() {

        Decrypt<S> decrypt = new Decrypt<S>(new JwtAppFactory(), sessionObjectReader, encKey, rotationEncKeys);
        RestBetween<S, U> decryptSession = new RestReadSession<S, U>(SESSION_NAME, false, sessionFailStatusCode, decrypt);
        before.add(decryptSession);

        return this;
    }

    public RestBetweenBuilder<S, U> csrfFailStatusCode(StatusCode csrfFailStatusCode) {
        this.csrfFailStatusCode = csrfFailStatusCode;
        return this;
    }

    public RestBetweenBuilder<S, U> csrfProtect() {
        DoubleSubmitCSRF doubleSubmitCSRF = new DoubleSubmitCSRF(new JwtAppFactory(), new RandomString(), signKey, rotationSignKeys);

        RestBetween<S,U> checkCSRF = new RestCheckCSRF<>(CSRF_NAME, CSRF_HDR_NAME, doubleSubmitCSRF, csrfFailStatusCode);
        before.add(checkCSRF);

        return this;
    }

    public RestBetweens<S, U> build() {
        return new RestBetweens<S,U>(before, after);
    }
}
