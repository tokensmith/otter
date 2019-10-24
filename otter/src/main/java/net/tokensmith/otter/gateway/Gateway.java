package net.tokensmith.otter.gateway;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.gateway.entity.Target;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.Dispatcher;
import net.tokensmith.otter.router.Engine;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.Method;


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

    protected Location add(Dispatcher dispatcher, Method method, Location location) {
        dispatcher.locations(method).add(location);
        return location;
    }

    public <S extends DefaultSession, U extends DefaultUser> void add(Target<S, U> target) {
        LocationTranslator<S, U> locationTranslator = locationTranslator(target.getGroupName());

        Map<Method, Location> locations = locationTranslator.to(target);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(engine.getDispatcher(), location.getKey(), location.getValue());
        }
    }

    public <U extends DefaultUser, P> void add(RestTarget<U, P> restTarget) {
        RestLocationTranslator<U, P> restLocationTranslator = restLocationTranslator(restTarget.getGroupName());

        Map<Method, Location> locations = restLocationTranslator.to(restTarget);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(engine.getDispatcher(), location.getKey(), location.getValue());
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

    public <S extends DefaultSession, U extends DefaultUser> void notFound(Target<S, U> notFound) {
        LocationTranslator<S, U> locationTranslator = locationTranslator(notFound.getGroupName());

        Map<Method, Location> locations = locationTranslator.to(notFound);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(engine.getNotFoundDispatcher(), location.getKey(), location.getValue());
        }
    }

    public <U extends DefaultUser, P> void notFound(RestTarget<U, P> notFound) {
        RestLocationTranslator<U, P> restLocationTranslator = restLocationTranslator(notFound.getGroupName());

        Map<Method, Location> locations = restLocationTranslator.toNotFound(notFound);
        for(Map.Entry<Method, Location> location: locations.entrySet()) {
            add(engine.getNotFoundDispatcher(), location.getKey(), location.getValue());
        }
    }
}
