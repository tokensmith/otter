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
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Gateway {
    protected Engine engine;
    protected Between prepareCSRF;
    protected Between checkCSRF;
    protected EncryptSession encryptSession;
    protected DecryptSession decryptSession;
    protected Route notFoundRoute;

    public Gateway(Engine engine, Between prepareCSRF, Between checkCSRF, EncryptSession encryptSession) {
        this.engine = engine;
        this.prepareCSRF = prepareCSRF;
        this.checkCSRF = checkCSRF;
        this.encryptSession = encryptSession;
    }

    public void get(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(prepareCSRF);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfAndSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(prepareCSRF);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void post(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .after(new ArrayList<>())
                .before(new ArrayList<>())
                .build();
        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(checkCSRF);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfAndSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(checkCSRF);
        before.add(decryptSession);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfAndSetSession(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(checkCSRF);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void put(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPut().add(route);
    }

    public void patch(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPatch().add(route);
    }

    public void delete(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getDelete().add(route);
    }

    public void connect(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getConnect().add(route);
    }

    public void options(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getOptions().add(route);
    }

    public void trace(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getTrace().add(route);
    }

    public void head(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getHead().add(route);
    }

    public void getRoute(Route route) {
        engine.getDispatcher().getGet().add(route);
    }

    public void postRoute(Route route) {
        engine.getDispatcher().getPost().add(route);
    }

    public void putRoute(Route route) {
        engine.getDispatcher().getPut().add(route);
    }

    public void patchRoute(Route route) {
        engine.getDispatcher().getPatch().add(route);
    }

    public void deleteRoute(Route route) {
        engine.getDispatcher().getDelete().add(route);
    }

    public void connectRoute(Route route) {
        engine.getDispatcher().getConnect().add(route);
    }

    public void optionsRoute(Route route) {
        engine.getDispatcher().getOptions().add(route);
    }

    public void traceRoute(Route route) {
        engine.getDispatcher().getTrace().add(route);
    }

    public void headRoute(Route route) {
        engine.getDispatcher().getHead().add(route);
    }

    // configuration methods below.
    public void setNotFoundRoute(Route notFoundRoute) {
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

    public void setSessionCookieConfig(CookieConfig sessionCookieConfig) {
        this.encryptSession.setCookieConfig(sessionCookieConfig);
    }

    public void setEncKey(SymmetricKey encKey) {
        this.encryptSession.setPreferredKey(encKey);
    }

    public DecryptSession getDecryptSession() {
        return decryptSession;
    }

    public void setDecryptSession(DecryptSession decryptSession) {
        this.decryptSession = decryptSession;
    }
}
