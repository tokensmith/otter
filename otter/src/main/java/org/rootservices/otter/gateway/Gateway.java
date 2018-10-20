package org.rootservices.otter.gateway;


import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
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
    protected Map<String, LocationTranslator<DefaultSession, DefaultUser>> locationTranslators;

    public Gateway(Engine engine, LocationTranslatorFactory locationTranslatorFactory, Map<String, LocationTranslator<DefaultSession, DefaultUser>> locationTranslators) {
        this.engine = engine;
        this.locationTranslatorFactory = locationTranslatorFactory;
        this.locationTranslators = locationTranslators;
    }

    public Location add(Method method, Location location) {
        engine.getDispatcher().locations(method).add(location);
        return location;
    }

    public <S extends DefaultSession, U extends DefaultUser> void add(Target<S, U> target) {
        LocationTranslator<S, U> locationTranslator = locationTranslators.get(target.getGroupName());

        Map<Method, Location> locations = locationTranslator.to(target);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(location.getKey(), location.getValue());
        }
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
