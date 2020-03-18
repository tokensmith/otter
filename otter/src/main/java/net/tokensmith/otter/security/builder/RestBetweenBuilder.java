package net.tokensmith.otter.security.builder;

import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.security.session.between.RestReadSession;
import net.tokensmith.otter.security.session.between.util.Decrypt;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RestBetweenBuilder<S, U> {

    private static String SESSION_NAME = "session";

    private TranslatorAppFactory appFactory;
    private Boolean secure;
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private StatusCode csrfFailStatusCode;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;
    private StatusCode sessionFailStatusCode;

    private Class<S> sessionClazz;
    private ObjectReader sessionObjectReader;

    private List<RestBetween<S,U>> before = new ArrayList<>();
    private List<RestBetween<S,U>> after = new ArrayList<>();

    public RestBetweenBuilder<S, U> routerAppFactory(TranslatorAppFactory appFactory) {
        this.appFactory = appFactory;
        return this;
    }

    public RestBetweenBuilder<S, U> secure(Boolean secure) {
        this.secure = secure;
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

    public RestBetweenBuilder<S, U> encKey(SymmetricKey encKey) {
        this.encKey = encKey;
        return this;
    }

    public RestBetweenBuilder<S, U> rotationEncKeys(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
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

    public RestBetweens<S, U> build() {
        return new RestBetweens<S,U>(before, after);
    }
}
