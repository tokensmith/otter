package net.tokensmith.otter.gateway;

import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.factory.RestBetweenFlyweight;
import net.tokensmith.otter.translatable.Translatable;

import java.util.Map;
import java.util.Optional;

/**
 * Responsible for constructing a RestLocationTranslator.
 * This is not in OtterAppFactory because it follows the same pattern as its sibling, LocationTranslatorFactory.
 */
public class RestLocationTranslatorFactory {

    public <U extends DefaultUser, P> RestLocationTranslator<U, P> make(Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> defaultDispatchErrors) {
        return new RestLocationTranslator<U, P>(
                restBetweenFlyweight(authRequired, authOptional),
                restErrors,
                defaultErrors,
                dispatchErrors,
                defaultDispatchErrors
        );
    }

    /**
     * Construct a flyweight for betweens that will be used when a RestTarget is translated to a Location. Each rest target
     * that is translated will use the same flyweight to add authentication betweens to a Location.
     * Therefore many Locations will use the same betweens instead of creating many identical ones.
     *
     * @param authRequired The between that requires authentication
     * @param authOptional The between that optionally authenticates.
     * @param <U> User
     * @return RestBetweenFlyweight that will be used in the RestLocationTranslator.
     */
    public <U> RestBetweenFlyweight<U> restBetweenFlyweight(Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional) {
        return new RestBetweenFlyweight<>(authRequired, authOptional);
    }
}
