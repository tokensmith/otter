package org.rootservices.otter.gateway;


import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base implementation for integrating a gateway. A gateway translates the
 * http delivery framework to otter and dispatches requests to resources. The
 * http delivery framework objects must not go past this implementation into Otter's
 * internals.
 *
 * Example extension is, ServletGateway.
 *
 * @param <S> Session implementation for application
 * @param <U> User object, intended to be a authenticated user.
 */
public class Gateway<S extends Session, U> {
    protected Engine<S, U> engine;
    protected Between<S, U> prepareCSRF;
    protected Between<S, U> checkCSRF;
    protected EncryptSession<S, U> encryptSession;
    protected DecryptSession<S, U> decryptSession;
    protected Route<S, U> notFoundRoute;

    public Gateway(Engine<S, U> engine, Between<S, U> prepareCSRF, Between<S, U> checkCSRF) {
        this.engine = engine;
        this.prepareCSRF = prepareCSRF;
        this.checkCSRF = checkCSRF;
    }

    public void get(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(prepareCSRF);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfAndSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(prepareCSRF);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void post(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .after(new ArrayList<>())
                .before(new ArrayList<>())
                .build();
        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(checkCSRF);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfAndSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(checkCSRF);
        before.add(decryptSession);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfAndSetSession(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(checkCSRF);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void put(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPut().add(route);
    }

    public void patch(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPatch().add(route);
    }

    public void delete(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getDelete().add(route);
    }

    public void connect(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getConnect().add(route);
    }

    public void options(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getOptions().add(route);
    }

    public void trace(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getTrace().add(route);
    }

    public void head(String path, Resource<S, U> resource) {
        Route<S, U> route = new RouteBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getHead().add(route);
    }

    public void getRoute(Route<S, U> route) {
        engine.getDispatcher().getGet().add(route);
    }

    public void postRoute(Route<S, U> route) {
        engine.getDispatcher().getPost().add(route);
    }

    public void putRoute(Route<S, U> route) {
        engine.getDispatcher().getPut().add(route);
    }

    public void patchRoute(Route<S, U> route) {
        engine.getDispatcher().getPatch().add(route);
    }

    public void deleteRoute(Route<S, U> route) {
        engine.getDispatcher().getDelete().add(route);
    }

    public void connectRoute(Route<S, U> route) {
        engine.getDispatcher().getConnect().add(route);
    }

    public void optionsRoute(Route<S, U> route) {
        engine.getDispatcher().getOptions().add(route);
    }

    public void traceRoute(Route<S, U> route) {
        engine.getDispatcher().getTrace().add(route);
    }

    public void headRoute(Route<S, U> route) {
        engine.getDispatcher().getHead().add(route);
    }

    // configuration methods below.
    public void setNotFoundRoute(Route<S, U> notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }

    public void setCsrfCookieConfig(CookieConfig csrfCookieConfig) {
        ((CheckCSRF) this.checkCSRF).setCookieName(csrfCookieConfig.getName());
        ((PrepareCSRF) this.prepareCSRF).setCookieConfig(csrfCookieConfig);
    }

    public void setCsrfFormFieldName(String fieldName) {
        ((CheckCSRF) this.checkCSRF).setFormFieldName(fieldName);
    }

    public void setSignKey(SymmetricKey signKey) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
    }

    public void setRotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
    }

    public DecryptSession getDecryptSession() {
        return decryptSession;
    }

    public void setDecryptSession(DecryptSession<S, U> decryptSession) {
        this.decryptSession = decryptSession;
    }

    public EncryptSession getEncryptSession() {
        return encryptSession;
    }

    public void setEncryptSession(EncryptSession<S, U> encryptSession) {
        this.encryptSession = encryptSession;
    }
}
