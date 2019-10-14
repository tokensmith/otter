package org.rootservices.otter.config;


import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.controller.error.BadRequestRestResource;
import org.rootservices.otter.controller.error.MediaTypeRestResource;
import org.rootservices.otter.controller.error.NotAcceptableRestResource;
import org.rootservices.otter.controller.error.ServerErrorRestResource;
import org.rootservices.otter.gateway.LocationTranslatorFactory;
import org.rootservices.otter.gateway.RestLocationTranslatorFactory;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.gateway.entity.rest.RestGroup;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.servlet.ServletGateway;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestHeaderTranslator;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.gateway.translator.RestLocationTranslator;
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.security.exception.SessionCtorException;
import org.rootservices.otter.server.container.ServletContainerFactory;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.MimeTypeTranslator;

import java.util.*;


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

    public ServletGateway servletGateway(Shape shape, List<Group<? extends DefaultSession,? extends DefaultUser>> groups, List<RestGroup<? extends DefaultUser>> restGroups) throws SessionCtorException {
        LocationTranslatorFactory locationTranslatorFactory = locationTranslatorFactory(shape);
        RestLocationTranslatorFactory restLocationTranslatorFactory = restLocationTranslatorFactory();

        Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators = locationTranslators(locationTranslatorFactory, groups);
        Map<String, RestLocationTranslator<? extends DefaultUser, ?>> restLocationTranslators = restLocationTranslators(restLocationTranslatorFactory, restGroups);

        Integer writeChunkSize = (shape.getWriteChunkSize() != null) ? shape.getWriteChunkSize() : WRITE_CHUNK_SIZE;

        return new ServletGateway(
                httpServletRequestTranslator(),
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
    public <S extends DefaultSession, U extends DefaultUser> Map<String, LocationTranslator<? extends S, ? extends U>> locationTranslators(LocationTranslatorFactory locationTranslatorFactory, List<Group<? extends S, ? extends U>> groups) throws SessionCtorException {
        Map<String, LocationTranslator<? extends S, ? extends U>> locationTranslators = new HashMap<>();

        for(Group<? extends S, ? extends U> group: groups) {

            Group<S, U> castedGroup = (Group<S,U>) group;
            locationTranslators.put(
                    group.getName(),
                    locationTranslatorFactory.make(
                            castedGroup.getSessionClazz(),
                            castedGroup.getAuthRequired(),
                            castedGroup.getAuthOptional(),
                            castedGroup.getErrorResources(),
                            castedGroup.getDispatchErrors(),
                            new HashMap<>() // 113: default dispatch errors.
                    )
            );
        }

        return locationTranslators;
    }

    public RestLocationTranslatorFactory restLocationTranslatorFactory() {
        return new RestLocationTranslatorFactory();
    }

    @SuppressWarnings("unchecked")
    public <U extends DefaultUser, P> Map<String, RestLocationTranslator<? extends U, ? extends P>> restLocationTranslators(RestLocationTranslatorFactory restLocationTranslatorFactory, List<RestGroup<? extends U>> restGroups) throws SessionCtorException {
        Map<String, RestLocationTranslator<? extends U, ? extends P>> restLocationTranslators = new HashMap<>();

        for(RestGroup<? extends U> restGroup: restGroups) {

            RestGroup<U> castedGroup = (RestGroup<U>) restGroup;
            restLocationTranslators.put(
                    castedGroup.getName(),
                    restLocationTranslatorFactory.make(
                            castedGroup.getAuthRequired(),
                            castedGroup.getAuthOptional(),
                            castedGroup.getRestErrors(),
                            defaultErrors(),
                            castedGroup.getDispatchErrors(),
                            defaultDispatchErrors()
                    )
            );
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
    public <U extends DefaultUser, P extends Translatable> Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> defaultDispatchErrors() {

        Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> defaultDispatchErrors = new HashMap<>();

        // Server Error - a bit unlikely

        // Unsupported Media Type.
        RestResource<U, P> mediaType = (RestResource<U, P>) new MediaTypeRestResource<U>();
        RestErrorTarget<U, P> mediaTypeTarget = new RestErrorTarget<>((Class<P>)ClientError.class, mediaType, new ArrayList<>(), new ArrayList<>());
        defaultDispatchErrors.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, mediaTypeTarget);

        // Not Acceptable
        RestResource<U, P> notAcceptable = (RestResource<U, P>) new NotAcceptableRestResource<U>();
        RestErrorTarget<U, P> notAcceptableTarget = new RestErrorTarget<>((Class<P>)ClientError.class, notAcceptable, new ArrayList<>(), new ArrayList<>());
        defaultDispatchErrors.put(StatusCode.NOT_ACCEPTABLE, notAcceptableTarget);

        return defaultDispatchErrors;
    }

    public HttpServletRequestTranslator httpServletRequestTranslator() {
        return new HttpServletRequestTranslator(
                httpServletRequestCookieTranslator(),
                new HttpServletRequestHeaderTranslator(),
                new QueryStringToMap(),
                new MimeTypeTranslator()
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
