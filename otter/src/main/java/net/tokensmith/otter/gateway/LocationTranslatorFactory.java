package net.tokensmith.otter.gateway;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.gateway.config.TranslatorConfig;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.router.factory.BetweenFlyweight;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.security.builder.BetweenBuilder;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;


/**
 * Responsible for constructing a LocationTranslator.
 * This is not in OtterAppFactory due to it's complexity
 */
public class LocationTranslatorFactory {
    private Shape shape;

    public LocationTranslatorFactory(Shape shape) {
        this.shape = shape;
    }

    public <S extends DefaultSession, U extends DefaultUser> LocationTranslator<S, U> make(TranslatorConfig<S, U> config) throws SessionCtorException {
        return new LocationTranslator<S, U>(
                betweenFlyweight(
                    config.getSessionClazz(),
                    config.getLabelBefore(),
                    config.getLabelAfter(),
                    config.getBefores(),
                    config.getAfters(),
                    config.getOnHalts()
                ),
                config.getErrorResources(),
                config.getDispatchErrors(),
                config.getDefaultDispatchErrors()
        );
    }

    /**
     * Construct a flyweight for betweens that will be used when a Target is translated to a Location. Each target that
     * is translated will use the same flyweight to add csrf, session, and authentication betweens to a Location.
     * Therefore many Locations will use the same betweens instead of creating many identical ones.
     *
     * @param sessionClazz The Class of the session
     * @param labelBefore a map of before betweens that will be used in the flyweight.
     * @param labelAfter a map of after betweens that will be used in the flyweight.
     * @param befores before betweens that will be used in the flyweight.
     * @param afters after betweens that will be used in the flyweight.
     * @param onHalts a map of halt handlers
     * @param <S> Session
     * @param <U> User
     * @return BetweenFlyweight that will be used the LocationTranslator.
     * @throws SessionCtorException if S does not have a copy constructor.
     */
    public <S, U> BetweenFlyweight<S, U> betweenFlyweight(Class<S> sessionClazz, Map<Label, List<Between<S,U>>> labelBefore, Map<Label, List<Between<S,U>>> labelAfter, List<Between<S, U>> befores, List<Between<S, U>> afters, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) throws SessionCtorException {
        TranslatorAppFactory appFactory = new TranslatorAppFactory();

        // 188: is this the right spot? add defaults.
        if (Objects.isNull(labelBefore.get(Label.CSRF_PREPARE)) || labelBefore.get(Label.CSRF_PREPARE).isEmpty()) {
            Betweens<S, U> csrfPrepare = csrfPrepare(appFactory);
            labelBefore.put(Label.CSRF_PREPARE, csrfPrepare.getBefore());
        }

        if (Objects.isNull(labelBefore.get(Label.CSRF_PROTECT)) || labelBefore.get(Label.CSRF_PROTECT).isEmpty()) {
            Betweens<S, U> csrfProtect = csrfProtect(appFactory, onHalts);
            labelBefore.put(Label.CSRF_PROTECT, csrfProtect.getBefore());
        }

        // 188: should these only run if needed?
        Betweens<S, U> session = session(appFactory, sessionClazz, onHalts);
        Betweens<S, U> sessionOptional = sessionOptional(appFactory, sessionClazz, onHalts);

        if (Objects.isNull(labelBefore.get(Label.SESSION_OPTIONAL)) || labelBefore.get(Label.SESSION_OPTIONAL).isEmpty()) {
            labelBefore.put(Label.SESSION_OPTIONAL, sessionOptional.getBefore());
        }

        if (Objects.isNull(labelBefore.get(Label.SESSION_REQUIRED)) || labelBefore.get(Label.SESSION_REQUIRED).isEmpty()) {
            labelBefore.put(Label.SESSION_REQUIRED, session.getBefore());
        }

        if (Objects.isNull(labelAfter.get(Label.SESSION_OPTIONAL)) || labelAfter.get(Label.SESSION_OPTIONAL).isEmpty()) {
            labelAfter.put(Label.SESSION_OPTIONAL, sessionOptional.getAfter());
        }

        if (Objects.isNull(labelAfter.get(Label.SESSION_REQUIRED)) || labelAfter.get(Label.SESSION_REQUIRED).isEmpty()) {
            labelAfter.put(Label.SESSION_REQUIRED, session.getAfter());
        }

        return new BetweenFlyweight<S, U>(
            labelBefore,
            labelAfter,
            befores,
            afters
        );
    }

    protected <S, U> Betweens<S, U> csrfPrepare(TranslatorAppFactory appFactory) {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfCookieConfig(shape.getCsrfCookie())
                .csrfPrepare()
                .build();

    }

    protected <S, U> Betweens<S, U> csrfProtect(TranslatorAppFactory appFactory, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfCookieConfig(shape.getCsrfCookie())
                .onHalts(onHalts)
                .csrfProtect()
                .build();

    }

    protected <S, U> Betweens<S, U> session(TranslatorAppFactory appFactory, Class<S> sessionClazz, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) throws SessionCtorException {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .encKey(shape.getEncKey())
                .rotationEncKey(shape.getRotationEncKeys())
                .sessionClass(sessionClazz)
                .sessionCookieConfig(shape.getSessionCookie())
                .onHalts(onHalts)
                .session()
                .build();
    }


    protected <S, U> Betweens<S, U> sessionOptional(TranslatorAppFactory appFactory, Class<S> sessionClazz, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) throws SessionCtorException {
        return new BetweenBuilder<S, U>()
                .routerAppFactory(appFactory)
                .encKey(shape.getEncKey())
                .rotationEncKey(shape.getRotationEncKeys())
                .sessionClass(sessionClazz)
                .sessionCookieConfig(shape.getSessionCookie())
                .onHalts(onHalts)
                .optionalSession()
                .build();
    }
}
