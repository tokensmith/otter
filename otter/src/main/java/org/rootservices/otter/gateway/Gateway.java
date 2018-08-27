package org.rootservices.otter.gateway;


import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.builder.CoordinateBuilder;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Coordinate;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.ArrayList;
import java.util.HashMap;
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
    protected Map<StatusCode, Route<S, U>> errorRoutes = new HashMap<>();

    public Gateway(Engine<S, U> engine, Between<S, U> prepareCSRF, Between<S, U> checkCSRF) {
        this.engine = engine;
        this.prepareCSRF = prepareCSRF;
        this.checkCSRF = checkCSRF;
    }

    public void get(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getGet().add(coordinate);
    }

    public void getCsrfProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(prepareCSRF);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getGet().add(coordinate);
    }

    public void getCsrfAndSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(prepareCSRF);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(coordinate);
    }

    public void getSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(coordinate);
    }

    public void post(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .after(new ArrayList<>())
                .before(new ArrayList<>())
                .build();
        engine.getDispatcher().getPost().add(coordinate);
    }

    public void postCsrfProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(checkCSRF);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getPost().add(coordinate);
    }

    public void postCsrfAndSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(checkCSRF);
        before.add(decryptSession);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(coordinate);
    }

    public void postCsrfAndSetSession(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(checkCSRF);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(coordinate);
    }

    public void postSessionProtect(String path, Resource<S, U> resource) {
        List<Between<S, U>> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between<S, U>> after = new ArrayList<>();
        after.add(encryptSession);

        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(coordinate);
    }

    public void put(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPut().add(coordinate);
    }

    public void patch(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPatch().add(coordinate);
    }

    public void delete(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getDelete().add(coordinate);
    }

    public void connect(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getConnect().add(coordinate);
    }

    public void options(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getOptions().add(coordinate);
    }

    public void trace(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getTrace().add(coordinate);
    }

    public void head(String path, Resource<S, U> resource) {
        Coordinate<S, U> coordinate = new CoordinateBuilder<S, U>()
                .path(path)
                .contentTypes(new ArrayList<>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getHead().add(coordinate);
    }

    public void getCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getGet().add(coordinate);
    }

    public void postCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getPost().add(coordinate);
    }

    public void putCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getPut().add(coordinate);
    }

    public void patchCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getPatch().add(coordinate);
    }

    public void deleteCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getDelete().add(coordinate);
    }

    public void connectCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getConnect().add(coordinate);
    }

    public void optionsCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getOptions().add(coordinate);
    }

    public void traceCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getTrace().add(coordinate);
    }

    public void headCoordinate(Coordinate<S, U> coordinate) {
        engine.getDispatcher().getHead().add(coordinate);
    }

    // configuration methods below.
    public void setErrorRoutes(Map<StatusCode, Route<S, U>> errorRoutes) {
        this.errorRoutes = errorRoutes;
    }

    public void setNotFoundRoute(Route<S, U> notFoundRoute) {
        this.errorRoutes.put(StatusCode.NOT_FOUND, notFoundRoute);
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
