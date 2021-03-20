package net.tokensmith.otter.config;


import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.ClientError;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.ServerError;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.error.rest.BadRequestRestResource;
import net.tokensmith.otter.controller.error.rest.MediaTypeRestResource;
import net.tokensmith.otter.controller.error.rest.NotAcceptableRestResource;
import net.tokensmith.otter.controller.error.rest.ServerErrorRestResource;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.dispatch.json.validator.RestValidate;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.LocationTranslatorFactory;
import net.tokensmith.otter.gateway.RestLocationTranslatorFactory;
import net.tokensmith.otter.gateway.config.RestTranslatorConfig;
import net.tokensmith.otter.gateway.config.TranslatorConfig;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.gateway.servlet.ServletGateway;
import net.tokensmith.otter.gateway.servlet.merger.HttpServletRequestMerger;
import net.tokensmith.otter.gateway.servlet.merger.HttpServletResponseMerger;
import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestHeaderTranslator;
import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.Dispatcher;
import net.tokensmith.otter.router.Engine;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.server.container.ServletContainerFactory;
import net.tokensmith.otter.server.path.CompiledClassPath;
import net.tokensmith.otter.server.path.WebAppPath;
import net.tokensmith.otter.translatable.Translatable;
import net.tokensmith.otter.translator.MimeTypeTranslator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Application Factory to construct objects in project.
 */
public class OtterAppFactory {
    public static Integer WRITE_CHUNK_SIZE = 1024;


    public CompiledClassPath compiledClassPath() {
        return new CompiledClassPath();
    }

    public WebAppPath webAppPath() {
        return new WebAppPath();
    }

    public ServletContainerFactory servletContainerFactory() {
        return new ServletContainerFactory(
                compiledClassPath(),
                webAppPath()
        );
    }

    public ServletGateway servletGateway(Shape shape, List<Group<? extends DefaultSession,? extends DefaultUser>> groups, List<RestGroup<? extends DefaultSession, ? extends DefaultUser>> restGroups) throws SessionCtorException {
        LocationTranslatorFactory locationTranslatorFactory = locationTranslatorFactory(shape);
        RestLocationTranslatorFactory restLocationTranslatorFactory = restLocationTranslatorFactory(shape);

        Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators = locationTranslators(locationTranslatorFactory, groups, shape);
        Map<String, RestLocationTranslator<? extends DefaultSession, ? extends DefaultUser, ?>> restLocationTranslators = restLocationTranslators(restLocationTranslatorFactory, restGroups, shape);

        Integer writeChunkSize = Objects.nonNull(shape.getWriteChunkSize()) ? shape.getWriteChunkSize() : WRITE_CHUNK_SIZE;

        // just in case not using default cookie names for session, csrf.
        Map<String, CookieConfig> cookieConfigs = new HashMap<>();
        for(Map.Entry<String, CookieConfig> item: shape.getCookieConfigs().entrySet()) {
            cookieConfigs.put(item.getValue().getName(), item.getValue());
        }

        return new ServletGateway(
                httpServletRequestTranslator(cookieConfigs),
                httpServletRequestMerger(),
                httpServletResponseMerger(),
                engine(),
                locationTranslators,
                restLocationTranslators,
                writeChunkSize
        );
    }

    public Engine engine() {
        return new Engine(new Dispatcher(), new Dispatcher());
    }

    public LocationTranslatorFactory locationTranslatorFactory(Shape shape) {
        return new LocationTranslatorFactory(shape);
    }

    @SuppressWarnings("unchecked")
    public <S extends DefaultSession, U extends DefaultUser> Map<String, LocationTranslator<? extends S, ? extends U>> locationTranslators(LocationTranslatorFactory locationTranslatorFactory, List<Group<? extends S, ? extends U>> groups, Shape shape) throws SessionCtorException {
        Map<String, LocationTranslator<? extends S, ? extends U>> locationTranslators = new HashMap<>();

        for(Group<? extends S, ? extends U> group: groups) {

            Group<S, U> castedGroup = (Group<S,U>) group;

            Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> defaultOnHalts = defaultOnHalts(shape);
            Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts = Stream.of(defaultOnHalts, castedGroup.getOnHalts())
                .flatMap(map -> map.entrySet().stream())
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v2
                    )
                );

            TranslatorConfig<S, U> config = new TranslatorConfig.Builder<S, U>()
                    .sessionClazz(castedGroup.getSessionClazz())
                    .labelBefore(castedGroup.getLabelBefore())
                    .labelAfter(castedGroup.getLabelAfter())
                    .befores(castedGroup.getBefores())
                    .afters(castedGroup.getAfters())
                    .errorResources(castedGroup.getErrorResources())
                    .dispatchErrors(castedGroup.getDispatchErrors())
                    .defaultDispatchErrors(new HashMap<>())
                    .onHalts(onHalts)
                    .build();

            locationTranslators.put(group.getName(), locationTranslatorFactory.make(config));
        }

        return locationTranslators;
    }

    public <S extends DefaultSession> Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> defaultOnHalts(Shape shape) {
        Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> defaults = new HashMap<>();

        defaults.put(Halt.CSRF, (Response<S> response, HaltException ex) -> {
            response.setTemplate(Optional.empty());
            response.setStatusCode(StatusCode.FORBIDDEN);
            response.getCookies().remove(shape.getCsrfCookie().getName());
            return response;
        });

        defaults.put(Halt.SESSION, (Response<S> response, HaltException ex) -> {
            response.setTemplate(Optional.empty());
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.getCookies().remove(shape.getSessionCookie().getName());
            return response;
        });

        return defaults;
    }

    public Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> defaultRestOnHalts(Shape shape) {
        Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> defaults = new HashMap<>();

        defaults.put(Halt.CSRF, (RestBtwnResponse response, HaltException ex) -> {
            response.setStatusCode(StatusCode.FORBIDDEN);
            response.getCookies().remove(shape.getCsrfCookie().getName());
            return response;
        });

        defaults.put(Halt.SESSION, (RestBtwnResponse response, HaltException ex) -> {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.getCookies().remove(shape.getSessionCookie().getName());
            return response;
        });

        return defaults;
    }

    public RestLocationTranslatorFactory restLocationTranslatorFactory(Shape shape) {
        return new RestLocationTranslatorFactory(shape);
    }

    public Validate restValidate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return new RestValidate(validator);
    }

    @SuppressWarnings("unchecked")
    public <U extends DefaultUser, S extends DefaultSession, P> Map<String, RestLocationTranslator<? extends S, ? extends U, ? extends P>> restLocationTranslators(RestLocationTranslatorFactory restLocationTranslatorFactory, List<RestGroup<? extends S, ? extends U>> restGroups, Shape shape) throws SessionCtorException {
        Map<String, RestLocationTranslator<? extends S, ? extends U, ? extends P>> restLocationTranslators = new HashMap<>();

        // bean validator
        Validate restValidate = restValidate();

        for(RestGroup<? extends S, ? extends U> restGroup: restGroups) {

            RestGroup<S, U> castedGroup = (RestGroup<S, U>) restGroup;

            var onHalts = Stream.of(defaultRestOnHalts(shape), castedGroup.getOnHalts())
                .flatMap(map -> map.entrySet().stream())
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v2
                    )
                );

            RestTranslatorConfig<S, U> config = new RestTranslatorConfig.Builder<S, U>()
                    .sessionClazz(castedGroup.getSessionClazz())
                    .labelBefore(castedGroup.getLabelBefore())
                    .labelAfter(castedGroup.getLabelAfter())
                    .befores(castedGroup.getBefores())
                    .afters(castedGroup.getAfters())
                    .restErrors(castedGroup.getRestErrors())
                    .defaultErrors(defaultErrors())
                    .dispatchErrors(castedGroup.getDispatchErrors())
                    .defaultDispatchErrors(defaultDispatchErrors())
                    .validate(restValidate)
                    .onHalts(onHalts)
                    .build();

            restLocationTranslators.put(castedGroup.getName(), restLocationTranslatorFactory.make(config));
        }

        return restLocationTranslators;
    }

    @SuppressWarnings("unchecked")
    public <U extends DefaultUser, P extends Translatable> Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors() {

        Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors = new HashMap<>();

        RestError<U, P> badRequest = new RestError<U, P>((Class<P>)ClientError.class, (RestResource<U, P>) new BadRequestRestResource<U>());
        defaultErrors.put(StatusCode.BAD_REQUEST, badRequest);

        RestError<U, P> serverError = new RestError<U, P>((Class<P>)ServerError.class, (RestResource<U, P>) new ServerErrorRestResource<U>());
        defaultErrors.put(StatusCode.SERVER_ERROR, serverError);

        return defaultErrors;
    }

    @SuppressWarnings("unchecked")
    public < S extends DefaultSession, U extends DefaultUser, P extends Translatable> Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors() {

        Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors = new HashMap<>();

        // Server Error - a bit unlikely

        // Unsupported Media Type.
        RestResource<U, P> mediaType = (RestResource<U, P>) new MediaTypeRestResource<U>();
        RestErrorTarget<S, U, P> mediaTypeTarget = new RestErrorTarget<>((Class<P>)ClientError.class, mediaType, new ArrayList<>(), new ArrayList<>());
        defaultDispatchErrors.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, mediaTypeTarget);

        // Not Acceptable
        RestResource<U, P> notAcceptable = (RestResource<U, P>) new NotAcceptableRestResource<U>();
        RestErrorTarget<S, U, P> notAcceptableTarget = new RestErrorTarget<>((Class<P>)ClientError.class, notAcceptable, new ArrayList<>(), new ArrayList<>());
        defaultDispatchErrors.put(StatusCode.NOT_ACCEPTABLE, notAcceptableTarget);

        return defaultDispatchErrors;
    }

    public HttpServletRequestTranslator httpServletRequestTranslator(Map<String, CookieConfig> cookieConfigs) {
        return new HttpServletRequestTranslator(
                httpServletRequestCookieTranslator(),
                new HttpServletRequestHeaderTranslator(),
                new QueryStringToMap(),
                new MimeTypeTranslator(),
                cookieConfigs
        );
    }

    public HttpServletRequestMerger httpServletRequestMerger() {
        return new HttpServletRequestMerger();
    }

    public HttpServletResponseMerger httpServletResponseMerger() {
        return new HttpServletResponseMerger(httpServletRequestCookieTranslator());
    }

    public HttpServletRequestCookieTranslator httpServletRequestCookieTranslator() {
        return new HttpServletRequestCookieTranslator();
    }

    public Base64.Decoder urlDecoder() {
        return Base64.getUrlDecoder();
    }
}
