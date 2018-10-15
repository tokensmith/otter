package org.rootservices.otter.gateway;


import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.RouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.exception.SessionCtorException;

import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation for integrating a gateway. A gateway translates the
 * http delivery framework to otter and dispatches requests to resources. The
 * http delivery framework objects must not go past this implementation into Otter's
 * internals.
 *
 * Example extension is, ServletGateway.
 */
public class Gateway {
    protected Engine engine;
    protected LocationTranslatorFactory locationTranslatorFactory;
    protected Map<String, LocationTranslator> locationTranslatorCache = new HashMap<>();

    public Gateway(Engine engine, LocationTranslatorFactory locationTranslatorFactory) {
        this.engine = engine;
        this.locationTranslatorFactory = locationTranslatorFactory;
    }

    public Gateway(Engine engine, LocationTranslatorFactory locationTranslatorFactory, Map<String, LocationTranslator> locationTranslatorCache) {
        this.engine = engine;
        this.locationTranslatorFactory = locationTranslatorFactory;
        this.locationTranslatorCache = locationTranslatorCache;
    }

    public Location add(Method method, Location location) {
        engine.getDispatcher().locations(method).add(location);
        return location;
    }

    public <S, U> void add(Target<S, U> target) {
        LocationTranslator<S, U> locationTranslator = locationTranslator(target.getGroupName());

        Map<Method, Location> locations = locationTranslator.to(target);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(location.getKey(), location.getValue());
        }
    }

    public <S, U> void group(Group<S, U> group) throws SessionCtorException {
        locationTranslator(group.getName(), group.getSessionClazz());
    }

    /**
     * This attempts to find an existing `locationTranslator` in the cache.
     * If its not found then a new one is constructed and added to the cache with the key, `group`.
     *
     * This speeds up start up time by using the same betweens for targets within the same group.
     *
     * @param groupName used as a key to lookup a {@code LocationTranslator<S, U>}
     * @param sessionClazz the class of a session
     * @param <S> Session
     * @param <U> User
     * @return an instance of {@code LocationTranslator<S, U>}
     * @throws SessionCtorException if Session does not have a copy constructor
     */
    protected <S, U> LocationTranslator<S, U> locationTranslator(String groupName, Class<S> sessionClazz) throws SessionCtorException {
        LocationTranslator<S, U> locationTranslator = locationTranslator(groupName);

        if (locationTranslator == null && groupName != null) {
            locationTranslator = locationTranslatorFactory.make(sessionClazz);
            locationTranslatorCache.put(groupName, locationTranslator);
        } else if (locationTranslator == null) {
            locationTranslator = locationTranslatorFactory.make(sessionClazz);
        }
        return locationTranslator;
    }

    @SuppressWarnings("unchecked")
    protected <S, U> LocationTranslator<S, U> locationTranslator(String groupName) {
        LocationTranslator<S, U> locationTranslator = (LocationTranslator<S, U>) locationTranslatorCache.get(groupName);
        return locationTranslator;
    }

    public <S, U> void setErrorRoute(StatusCode statusCode, Route<S, U> errorRoute) {
        RequestTranslator<S, U> requestTranslator = new RequestTranslator<>();
        AnswerTranslator<S> answerTranslator = new AnswerTranslator<>();

        RouteRunner errorRouteRunner = new RouteRun<S, U>(errorRoute, requestTranslator, answerTranslator);
        this.engine.getErrorRoutes().put(statusCode, errorRouteRunner);
    }

    public RouteRunner getErrorRoute(StatusCode statusCode) {
        return this.engine.getErrorRoutes().get(statusCode);
    }
}
