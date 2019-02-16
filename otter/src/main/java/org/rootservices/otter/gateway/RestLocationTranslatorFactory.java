package org.rootservices.otter.gateway;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.translator.RestLocationTranslator;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.router.factory.RestBetweenFlyweight;
import org.rootservices.otter.translatable.Translatable;

import java.util.Optional;

/**
 * Responsible for constructing a RestLocationTranslator.
 * This is not in OtterAppFactory because it follows the same pattern as its sibling, LocationTranslatorFactory.
 */
public class RestLocationTranslatorFactory {

    public <U extends DefaultUser, P> RestLocationTranslator<U, P> make(Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional) {
        return new RestLocationTranslator<U, P>(
                restBetweenFlyweight(authRequired, authOptional)
        );
    }

    public <U> RestBetweenFlyweight<U> restBetweenFlyweight(Optional<RestBetween<U>> authRequired, Optional<RestBetween<U>> authOptional) {
        return new RestBetweenFlyweight<>(authRequired, authOptional);
    }
}
