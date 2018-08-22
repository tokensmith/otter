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

public class Gateway<T extends Session> {
    protected Engine<T> engine;
    protected Between<T> prepareCSRF;
    protected Between<T> checkCSRF;
    protected EncryptSession<T> encryptSession;
    protected DecryptSession<T> decryptSession;
    protected Route<T> notFoundRoute;

    public Gateway(Engine<T> engine, Between<T> prepareCSRF, Between<T> checkCSRF) {
        this.engine = engine;
        this.prepareCSRF = prepareCSRF;
        this.checkCSRF = checkCSRF;
    }

    public void get(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfProtect(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(prepareCSRF);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfAndSessionProtect(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(prepareCSRF);

        List<Between<T>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getSessionProtect(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between<T>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void post(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .after(new ArrayList<>())
                .before(new ArrayList<>())
                .build();
        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfProtect(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(checkCSRF);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfAndSessionProtect(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(checkCSRF);
        before.add(decryptSession);

        List<Between<T>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfAndSetSession(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(checkCSRF);

        List<Between<T>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postSessionProtect(String path, Resource<T> resource) {
        List<Between<T>> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between<T>> after = new ArrayList<>();
        after.add(encryptSession);

        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void put(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPut().add(route);
    }

    public void patch(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPatch().add(route);
    }

    public void delete(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getDelete().add(route);
    }

    public void connect(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getConnect().add(route);
    }

    public void options(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getOptions().add(route);
    }

    public void trace(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getTrace().add(route);
    }

    public void head(String path, Resource<T> resource) {
        Route<T> route = new RouteBuilder<T>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getHead().add(route);
    }

    public void getRoute(Route<T> route) {
        engine.getDispatcher().getGet().add(route);
    }

    public void postRoute(Route<T> route) {
        engine.getDispatcher().getPost().add(route);
    }

    public void putRoute(Route<T> route) {
        engine.getDispatcher().getPut().add(route);
    }

    public void patchRoute(Route<T> route) {
        engine.getDispatcher().getPatch().add(route);
    }

    public void deleteRoute(Route<T> route) {
        engine.getDispatcher().getDelete().add(route);
    }

    public void connectRoute(Route<T> route) {
        engine.getDispatcher().getConnect().add(route);
    }

    public void optionsRoute(Route<T> route) {
        engine.getDispatcher().getOptions().add(route);
    }

    public void traceRoute(Route<T> route) {
        engine.getDispatcher().getTrace().add(route);
    }

    public void headRoute(Route<T> route) {
        engine.getDispatcher().getHead().add(route);
    }

    // configuration methods below.
    public void setNotFoundRoute(Route<T> notFoundRoute) {
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

    public void setDecryptSession(DecryptSession<T> decryptSession) {
        this.decryptSession = decryptSession;
    }

    public EncryptSession getEncryptSession() {
        return encryptSession;
    }

    public void setEncryptSession(EncryptSession<T> encryptSession) {
        this.encryptSession = encryptSession;
    }
}
