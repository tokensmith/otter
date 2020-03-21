package net.tokensmith.otter.gateway;

import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.factory.RestBetweenFlyweight;
import net.tokensmith.otter.security.builder.BetweenBuilder;
import net.tokensmith.otter.security.builder.RestBetweenBuilder;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.translatable.Translatable;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Responsible for constructing a RestLocationTranslator.
 * This is not in OtterAppFactory because it follows the same pattern as its sibling, LocationTranslatorFactory.
 */
public class RestLocationTranslatorFactory {
    private Shape shape;

    public RestLocationTranslatorFactory(Shape shape) {
        this.shape = shape;
    }

    public <S extends DefaultSession, U extends DefaultUser, P> RestLocationTranslator<S, U, P> make(Class<S> sessionClazz, Optional<RestBetween<S, U>> authRequired, Optional<RestBetween<S, U>> authOptional, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors, Validate restValidate) {
        return new RestLocationTranslator<S, U, P>(
                restBetweenFlyweight(sessionClazz, authRequired, authOptional),
                restErrors,
                defaultErrors,
                dispatchErrors,
                defaultDispatchErrors,
                restValidate
        );
    }

    /**
     * Construct a flyweight for betweens that will be used when a RestTarget is translated to a Location. Each rest target
     * that is translated will use the same flyweight to add authentication betweens to a Location.
     * Therefore many Locations will use the same betweens instead of creating many identical ones.
     *
     * @param sessionClazz The session class to be used when configuring object reader
     * @param authRequired The between that requires authentication
     * @param authOptional The between that optionally authenticates.
     * @param <S> Session
     * @param <U> User
     * @return RestBetweenFlyweight that will be used in the RestLocationTranslator.
     */
    public <S, U> RestBetweenFlyweight<S, U> restBetweenFlyweight(Class<S> sessionClazz, Optional<RestBetween<S, U>> authRequired, Optional<RestBetween<S, U>> authOptional) {
        TranslatorAppFactory appFactory = new TranslatorAppFactory();

        return new RestBetweenFlyweight<S, U>(
                session(appFactory, sessionClazz),
                sessionOptional(appFactory, sessionClazz),
                csrfProtect(appFactory),
                authRequired,
                authOptional
        );
    }

    protected <S, U> RestBetweens<S, U> session(TranslatorAppFactory appFactory, Class<S> sessionClazz) {
        return new RestBetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .secure(shape.getSecure())
                .encKey(shape.getEncKey())
                .rotationEncKeys(shape.getRotationEncKeys())
                .sessionClazz(sessionClazz)
                .sessionFailStatusCode(shape.getSessionFailStatusCode())
                .session()
                .build();
    }

    protected <S, U> RestBetweens<S, U> sessionOptional(TranslatorAppFactory appFactory, Class<S> sessionClazz) {
        return new RestBetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .secure(shape.getSecure())
                .encKey(shape.getEncKey())
                .rotationEncKeys(shape.getRotationEncKeys())
                .sessionClazz(sessionClazz)
                .sessionFailStatusCode(shape.getSessionFailStatusCode())
                .optionalSession()
                .build();
    }

    protected <S, U> RestBetweens<S, U> csrfProtect(TranslatorAppFactory appFactory) {
        return new RestBetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .secure(shape.getSecure())
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfFailStatusCode(shape.getCsrfFailStatusCode())
                .csrfProtect()
                .build();
    }
}
