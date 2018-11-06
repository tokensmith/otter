package org.rootservices.otter.gateway;


import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.HtmlRouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.gateway.entity.target.Target;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.translatable.Translatable;


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
    protected Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> locationTranslators;

    public Gateway(Engine engine, Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> locationTranslators) {
        this.engine = engine;
        this.locationTranslators = locationTranslators;
    }

    public Location add(Method method, Location location) {
        engine.getDispatcher().locations(method).add(location);
        return location;
    }

    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> void add(Target<S, U, P> target) {
        LocationTranslator<S, U, P> locationTranslator = locationTranslator(target.getGroupName());

        Map<Method, Location> locations = locationTranslator.to(target);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(location.getKey(), location.getValue());
        }
    }

    /**
     * Finds the location translator for the groupName.
     *
     * Casting is safe here because the type of the value in locationTranslators
     * are upper bound wild cards for, DefaultSession and DefaultUser. Therefore the
     * values extend, DefaultSession and DefaultUser.
     *
     * https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html
     *
     * @param groupName the name of the group. Used as a lookup key for the translator.
     * @param <S> Session
     * @param <U> User
     * @param <U> Payload
     * @return the locationTranslator for the group
     */
    @SuppressWarnings("unchecked")
    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> LocationTranslator<S, U, P> locationTranslator(String groupName) {
        return (LocationTranslator<S, U, P>) locationTranslators.get(groupName);
    }

    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> void setErrorRoute(StatusCode statusCode, Route<S, U, P> errorRoute) {
        RequestTranslator<S, U, P> requestTranslator = new RequestTranslator<S, U, P>();
        AnswerTranslator<S> answerTranslator = new AnswerTranslator<>();

        RouteRunner errorRouteRunner = new HtmlRouteRun<S, U, P>(errorRoute, requestTranslator, answerTranslator);
        this.engine.getErrorRoutes().put(statusCode, errorRouteRunner);
    }

    public RouteRunner getErrorRoute(StatusCode statusCode) {
        return this.engine.getErrorRoutes().get(statusCode);
    }
}
