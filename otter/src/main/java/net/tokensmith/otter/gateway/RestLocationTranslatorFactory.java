package net.tokensmith.otter.gateway;

import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.entity.Label;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public <S extends DefaultSession, U extends DefaultUser, P> RestLocationTranslator<S, U, P> make(Class<S> sessionClazz, Map<Label, List<RestBetween<S, U>>> before, Map<Label, List<RestBetween<S, U>>> after, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors, Validate restValidate) {
        return new RestLocationTranslator<S, U, P>(
                restBetweenFlyweight(sessionClazz, before, after),
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
     * @param before a map of before betweens that will be used in the flyweight.
     * @param after a map of after betweens that will be used in the flyweight.
     * @param <S> Session
     * @param <U> User
     * @return RestBetweenFlyweight that will be used in the RestLocationTranslator.
     */
    public <S, U> RestBetweenFlyweight<S, U> restBetweenFlyweight(Class<S> sessionClazz, Map<Label, List<RestBetween<S, U>>> before, Map<Label, List<RestBetween<S, U>>> after) {
        TranslatorAppFactory appFactory = new TranslatorAppFactory();

        // 188: is this the right spot? add defaults.
        if (Objects.isNull(before.get(Label.CSRF_PREPARE)) || before.get(Label.CSRF_PROTECT).isEmpty()) {
            RestBetweens<S, U> csrfProtect = csrfProtect(appFactory);
            before.put(Label.CSRF_PROTECT, csrfProtect.getBefore());
        }

        // 188: should these only run if needed?
        RestBetweens<S, U> session = session(appFactory, sessionClazz);
        RestBetweens<S, U> sessionOptional = sessionOptional(appFactory, sessionClazz);

        if (Objects.isNull(before.get(Label.SESSION_OPTIONAL)) || before.get(Label.SESSION_OPTIONAL).isEmpty()) {
            before.put(Label.SESSION_OPTIONAL, sessionOptional.getBefore());
        }

        if (Objects.isNull(before.get(Label.SESSION_REQUIRED)) || before.get(Label.SESSION_REQUIRED).isEmpty()) {
            before.put(Label.SESSION_REQUIRED, session.getBefore());
        }

        if (Objects.isNull(after.get(Label.SESSION_OPTIONAL)) || after.get(Label.SESSION_OPTIONAL).isEmpty()) {
            after.put(Label.SESSION_OPTIONAL, sessionOptional.getBefore());
        }

        if (Objects.isNull(after.get(Label.SESSION_REQUIRED)) || after.get(Label.SESSION_REQUIRED).isEmpty()) {
            after.put(Label.SESSION_REQUIRED, session.getAfter());
        }

        return new RestBetweenFlyweight<S, U>(
            before,
            after
        );
    }

    protected <S, U> RestBetweens<S, U> session(TranslatorAppFactory appFactory, Class<S> sessionClazz) {
        return new RestBetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
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
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfFailStatusCode(shape.getCsrfFailStatusCode())
                .csrfProtect()
                .build();
    }
}
