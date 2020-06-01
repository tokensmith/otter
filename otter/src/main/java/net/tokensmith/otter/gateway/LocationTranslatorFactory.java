package net.tokensmith.otter.gateway;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.factory.BetweenFlyweight;
import net.tokensmith.otter.security.builder.BetweenBuilder;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Responsible for constructing a LocationTranslator.
 * This is not in OtterAppFactory due to it's complexity
 */
public class LocationTranslatorFactory {
    private Shape shape;

    public LocationTranslatorFactory(Shape shape) {
        this.shape = shape;
    }

    public <S extends DefaultSession, U extends DefaultUser> LocationTranslator<S, U> make(Class<S> sessionClazz, Map<Label, List<Between<S,U>>> before, Map<Label, List<Between<S,U>>> after, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors, Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors) throws SessionCtorException {
        return new LocationTranslator<S, U>(
                betweenFlyweight(sessionClazz, before, after),
                errorResources,
                dispatchErrors,
                defaultDispatchErrors
        );
    }

    /**
     * Construct a flyweight for betweens that will be used when a Target is translated to a Location. Each target that
     * is translated will use the same flyweight to add csrf, session, and authentication betweens to a Location.
     * Therefore many Locations will use the same betweens instead of creating many identical ones.
     *
     * @param sessionClazz The Class of the session
     * @param before a map of before betweens that will be used in the flyweight.
     * @param after a map of after betweens that will be used in the flyweight.
     * @param <S> Session
     * @param <U> User
     * @return BetweenFlyweight that will be used the LocationTranslator.
     * @throws SessionCtorException if S does not have a copy constructor.
     */
    public <S, U> BetweenFlyweight<S, U> betweenFlyweight(Class<S> sessionClazz, Map<Label, List<Between<S,U>>> before, Map<Label, List<Between<S,U>>> after) throws SessionCtorException {
        TranslatorAppFactory appFactory = new TranslatorAppFactory();

        // 188: is this the right spot? add defaults.
        if (before.get(Label.CSRF_PREPARE).isEmpty()) {
            Betweens<S, U> csrfPrepare = csrfPrepare(appFactory);
            before.put(Label.CSRF_PREPARE, csrfPrepare.getBefore());
        }

        if (before.get(Label.CSRF_PROTECT).isEmpty()) {
            Betweens<S, U> csrfProtect = csrfProtect(appFactory);
            before.put(Label.CSRF_PROTECT, csrfProtect.getBefore());
        }

        // 188: should these only run if needed?
        Betweens<S, U> session = session(appFactory, sessionClazz);
        Betweens<S, U> sessionOptional = sessionOptional(appFactory, sessionClazz);

        if (before.get(Label.SESSION_OPTIONAL).isEmpty()) {
            before.put(Label.CSRF_PROTECT, sessionOptional.getBefore());
        }

        if (before.get(Label.SESSION_REQUIRED).isEmpty()) {
            before.put(Label.SESSION_REQUIRED, session.getBefore());
        }

        if (after.get(Label.SESSION_OPTIONAL).isEmpty()) {
            after.put(Label.SESSION_REQUIRED, sessionOptional.getBefore());
        }

        if (after.get(Label.SESSION_REQUIRED).isEmpty()) {
            after.put(Label.SESSION_REQUIRED, session.getAfter());
        }

        return new BetweenFlyweight<S, U>(
            before,
            after
        );
    }

    protected <S, U> Betweens<S, U> csrfPrepare(TranslatorAppFactory appFactory) {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfFailStatusCode(shape.getCsrfFailStatusCode())
                .csrfFailTemplate(shape.getCsrfFailTemplate())
                .csrfCookieConfig(shape.getCsrfCookie())
                .csrfPrepare()
                .build();

    }

    protected <S, U> Betweens<S, U> csrfProtect(TranslatorAppFactory appFactory) {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfFailStatusCode(shape.getCsrfFailStatusCode())
                .csrfFailTemplate(shape.getCsrfFailTemplate())
                .csrfCookieConfig(shape.getCsrfCookie())
                .csrfProtect()
                .build();

    }

    protected <S, U> Betweens<S, U> session(TranslatorAppFactory appFactory, Class<S> sessionClazz) throws SessionCtorException {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .encKey(shape.getEncKey())
                .rotationEncKey(shape.getRotationEncKeys())
                .sessionClass(sessionClazz)
                .sessionFailStatusCode(shape.getSessionFailStatusCode())
                .sessionFailTemplate(shape.getSessionFailTemplate())
                .sessionCookieConfig(shape.getSessionCookie())
                .session()
                .build();
    }


    protected <S, U> Betweens<S, U> sessionOptional(TranslatorAppFactory appFactory, Class<S> sessionClazz) throws SessionCtorException {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .encKey(shape.getEncKey())
                .rotationEncKey(shape.getRotationEncKeys())
                .sessionClass(sessionClazz)
                .sessionFailStatusCode(shape.getSessionFailStatusCode())
                .sessionFailTemplate(shape.getSessionFailTemplate())
                .sessionCookieConfig(shape.getSessionCookie())
                .optionalSession()
                .build();
    }
}
