package net.tokensmith.otter.security.builder;

import com.fasterxml.jackson.databind.ObjectReader;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.security.config.SecurityAppFactory;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;
import net.tokensmith.otter.security.csrf.between.rest.RestCheckCSRF;
import net.tokensmith.otter.security.session.between.rest.RestReadSession;
import net.tokensmith.otter.security.session.util.Decrypt;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class RestBetweenBuilder<S, U> {
    private static SecurityAppFactory securityAppFactory = new SecurityAppFactory();

    private static String CSRF_HDR_NAME = "X-CSRF";

    private TranslatorAppFactory appFactory;

    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts;

    // csrf
    private SymmetricKey signKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private CookieConfig csrfCookieConfig;

    // session
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationEncKeys;
    private CookieConfig sessionCookieConfig;

    private Class<S> sessionClazz;
    private ObjectReader sessionObjectReader;

    private List<RestBetween<S,U>> before = new ArrayList<>();
    private List<RestBetween<S,U>> after = new ArrayList<>();

    public RestBetweenBuilder<S, U> routerAppFactory(TranslatorAppFactory appFactory) {
        this.appFactory = appFactory;
        return this;
    }

    public RestBetweenBuilder<S, U> onHalts(Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
        this.onHalts = onHalts;
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

    public RestBetweenBuilder<S, U> session() {

        Decrypt<S> decrypt = securityAppFactory.decrypt(sessionObjectReader, encKey, rotationEncKeys);
        RestBetween<S, U> decryptSession = new RestReadSession<S, U>(sessionCookieConfig.getName(), true, decrypt, onHalts.get(Halt.SESSION));
        before.add(decryptSession);

        return this;
    }

    public RestBetweenBuilder<S, U> optionalSession() {

        Decrypt<S> decrypt = securityAppFactory.decrypt(sessionObjectReader, encKey, rotationEncKeys);
        RestBetween<S, U> decryptSession = new RestReadSession<S, U>(sessionCookieConfig.getName(), false, decrypt, onHalts.get(Halt.SESSION));
        before.add(decryptSession);

        return this;
    }

    public RestBetweenBuilder<S, U> csrfProtect() {
        DoubleSubmitCSRF doubleSubmitCSRF = securityAppFactory.doubleSubmitCSRF(signKey, rotationSignKeys);
        RestBetween<S,U> checkCSRF = new RestCheckCSRF<>(csrfCookieConfig.getName(), CSRF_HDR_NAME, doubleSubmitCSRF, onHalts.get(Halt.CSRF));
        before.add(checkCSRF);

        return this;
    }

    public RestBetweens<S, U> build() {
        return new RestBetweens<S,U>(before, after);
    }
}
