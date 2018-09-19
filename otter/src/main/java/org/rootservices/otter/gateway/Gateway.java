package org.rootservices.otter.gateway;


import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
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
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class Gateway<S, U> {
    protected Engine<S, U> engine;
    protected LocationTranslator<S, U> locationTranslator;

    public Gateway(Engine<S, U> engine, LocationTranslator<S, U> locationTranslator) {
        this.engine = engine;
        this.locationTranslator = locationTranslator;
    }

    public Location<S, U> add(Method method, Location<S, U> location) {
        engine.getDispatcher().locations(method).add(location);
        return location;
    }

    public void add(Target<S, U> target) {
        Map<Method, Location<S, U>> locations = locationTranslator.to(target);
        for(Map.Entry<Method, Location<S, U>> location: locations.entrySet()) {
            add(location.getKey(), location.getValue());
        }
    }

    public void setErrorRoute(StatusCode statusCode, Route<S, U> errorRoute) {
        this.engine.getErrorRoutes().put(statusCode, errorRoute);
    }

    public Route<S, U> getErrorRoute(StatusCode statusCode) {
        return this.engine.getErrorRoutes().get(statusCode);
    }
}
