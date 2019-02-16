package org.rootservices.otter.gateway;


import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.dispatch.RouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.RestTarget;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.gateway.translator.RestLocationTranslator;
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
    protected Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators;
    protected Map<String, RestLocationTranslator<? extends DefaultUser, ?>> restLocationTranslators;

    public Gateway(Engine engine, Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators, Map<String, RestLocationTranslator<? extends DefaultUser, ?>> restLocationTranslators) {
        this.engine = engine;
        this.locationTranslators = locationTranslators;
        this.restLocationTranslators = restLocationTranslators;
    }

    public Location add(Method method, Location location) {
        engine.getDispatcher().locations(method).add(location);
        return location;
    }

    public <S extends DefaultSession, U extends DefaultUser> void add(Target<S, U> target) {
        LocationTranslator<S, U> locationTranslator = locationTranslator(target.getGroupName());

        Map<Method, Location> locations = locationTranslator.to(target);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(location.getKey(), location.getValue());
        }
    }

    public <U extends DefaultUser, P> void add(RestTarget<U, P> restTarget) {
        RestLocationTranslator<U, P> restLocationTranslator = restLocationTranslator(restTarget.getGroupName());

        Map<Method, Location> locations = restLocationTranslator.to(restTarget);
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
     * @return the locationTranslator for the group
     */
    @SuppressWarnings("unchecked")
    public <S extends DefaultSession, U extends DefaultUser> LocationTranslator<S, U> locationTranslator(String groupName) {
        return (LocationTranslator<S, U>) locationTranslators.get(groupName);
    }

    /**
     * Finds the rest location translator for the groupName.
     *
     * Casting is safe here because the type of the value in locationTranslators
     * are upper bound wild cards for, DefaultUser and Translatable. Therefore the
     * values extend, DefaultUser and Translatable.
     *
     * https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html
     *
     * @param groupName the name of the group. Used as a lookup key for the translator.
     * @param <U> User
     * @param <P> Payload
     * @return the restLocationTranslator for the group
     */
    @SuppressWarnings("unchecked")
    public <U extends DefaultUser, P> RestLocationTranslator<U, P> restLocationTranslator(String groupName) {
        return (RestLocationTranslator<U, P>) restLocationTranslators.get(groupName);
    }

    public <S extends DefaultSession, U extends DefaultUser> void setErrorRoute(StatusCode statusCode, Route<S, U> errorRoute) {
        RequestTranslator<S, U> requestTranslator = new RequestTranslator<>();
        AnswerTranslator<S> answerTranslator = new AnswerTranslator<>();

        RouteRunner errorRouteRunner = new RouteRun<S, U>(errorRoute, requestTranslator, answerTranslator);
        this.engine.getErrorRoutes().put(statusCode, errorRouteRunner);
    }

    public RouteRunner getErrorRoute(StatusCode statusCode) {
        return this.engine.getErrorRoutes().get(statusCode);
    }
}
