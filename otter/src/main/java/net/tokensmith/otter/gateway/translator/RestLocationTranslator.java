package net.tokensmith.otter.gateway.translator;



import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.dispatch.translator.RestErrorHandler;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.router.builder.RestLocationBuilder;
import net.tokensmith.otter.dispatch.config.DispatchAppFactory;
import net.tokensmith.otter.router.entity.Location;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.RestRoute;
import net.tokensmith.otter.router.factory.RestBetweenFlyweight;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.translatable.Translatable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestLocationTranslator<S extends DefaultSession, U extends DefaultUser, P> {
    private static DispatchAppFactory dispatchAppFactory = new DispatchAppFactory();

    private RestBetweenFlyweight<S, U> restBetweenFlyweight;

    // default payload validator.
    private Validate restValidate;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;

    // defaults if not provided, 400
    private Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors;

    // dispatch errors if not provided 415
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors;

    // dispatch error defaults if not provided 415
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors;

    public RestLocationTranslator(RestBetweenFlyweight<S, U> restBetweenFlyweight, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors, Validate restValidate) {
        this.restBetweenFlyweight = restBetweenFlyweight;
        this.restErrors = restErrors;
        this.defaultErrors = defaultErrors;
        this.dispatchErrors = dispatchErrors;
        this.defaultDispatchErrors = defaultDispatchErrors;
        this.restValidate = restValidate;
    }

    public Map<Method, Location> to(RestTarget<S, U, P> from) {
        Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> mergedDispatchErrors = mergeDispatchErrors(defaultDispatchErrors, dispatchErrors);
        Map<StatusCode, RestErrorHandler<U>> errorHandlers = toErrorHandlers(from.getRestErrors());

        Map<Method, Location> to;
        to = to(from, false, errorHandlers, mergedDispatchErrors);
        return to;
    }

    public Map<Method, Location> toNotFound(RestTarget<S, U, P> from) {
        Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> mergedDispatchErrors = mergeDispatchErrors(defaultDispatchErrors, dispatchErrors);
        Map<StatusCode, RestErrorHandler<U>> errorHandlers = toErrorHandlers(from.getRestErrors());

        Map<Method, Location> to;
        to = to(from, true, errorHandlers, mergedDispatchErrors);
        return to;
    }

    protected Map<Method, Location> to(RestTarget<S, U, P> from, Boolean isDispatchError, Map<StatusCode, RestErrorHandler<U>> errorHandlers, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> mergedDispatchErrors) {
        Map<Method, Location> to = new HashMap<>();

        for(Method method: from.getMethods()) {
            // 179
            RestLocationBuilder<S, U, P> locationBuilder = makeLocationBuilder(
                method, from, errorHandlers, mergedDispatchErrors
            );
            locationBuilder.isDispatchError(isDispatchError);
            to.put(method, locationBuilder.build());
        }
        return to;
    }

    protected RestLocationBuilder<S, U, P> makeLocationBuilder(Method method, RestTarget<S, U, P> from, Map<StatusCode, RestErrorHandler<U>> errorHandlers, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> mergedDispatchErrors) {
        RestBetweens<S, U> betweens = restBetweenFlyweight.make(method, from.getLabels());

        List<MimeType> contentTypes = from.getContentTypes().get(method);
        if (contentTypes == null) {
            contentTypes = new ArrayList<>();
        }

        List<MimeType> accepts = from.getAccepts().get(method);
        if (accepts == null) {
            accepts = new ArrayList<>();
        }

        // use default payload valiate?
        Validate validate = restValidate;
        if (from.getValidate() != null) {
            validate = from.getValidate();
        }

        RestLocationBuilder<S, U, P> locationBuilder = new RestLocationBuilder<S, U, P>()
                .path(from.getRegex())
                .contentTypes(contentTypes)
                .accepts(accepts)
                .restResource(from.getRestResource())
                .payload(from.getPayload())
                .validate(validate)
                .before(
                        Stream.of(betweens.getBefore(), from.getBefore())
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                )
                .after(
                        Stream.of(betweens.getAfter(), from.getAfter())
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                )
                // these are used in JsonRouteRun
                .restErrorHandlers(errorHandlers);

        // merge dispatch errors with overrides on from.
        Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors = mergeDispatchErrors(mergedDispatchErrors, from.getErrorTargets());

        // add the error routes to be used in engine.
        for(Map.Entry<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> entry: dispatchErrors.entrySet()) {
            RestRoute<S, U, ? extends Translatable> restRoute = dispatchAppFactory.makeRestRoute(entry.getValue());

            RouteRunner restRouteRunner = dispatchAppFactory.makeJsonDispatchErrorRouteRun(
                restRoute,
                entry.getValue().getPayload(),
                validate
            );
            locationBuilder = locationBuilder.errorRouteRunner(
                    entry.getKey(),
                    restRouteRunner
            );
        }
        return locationBuilder;
    }

    /**
     * Merges two maps of rest errors with the preference to the right when a collision occurs.
     *
     * @param left a map of rest errors
     * @param right a map of rest errors
     * @return a merged map of rest errors.
     */
    protected Map<StatusCode, RestError<U, ? extends Translatable>> mergeRestErrors(Map<StatusCode, RestError<U, ? extends Translatable>> left, Map<StatusCode, RestError<U, ? extends Translatable>> right) {

        Map<StatusCode, RestError<U, ? extends Translatable>> to = Stream.of(left, right)
                .flatMap(map -> map.entrySet().stream())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (v1, v2) -> v2
                        )
                );

        return to;
    }

    protected Map<StatusCode, RestErrorHandler<U>> toErrorHandlers(Map<StatusCode, RestError<U, ? extends Translatable>> from) {
        Map<StatusCode, RestErrorHandler<U>> to = new HashMap<>();

        Map<StatusCode, RestError<U, ? extends Translatable>> mergedRestErrors = mergeRestErrors(defaultErrors, restErrors);
        Map<StatusCode, RestError<U, ? extends Translatable>> fromMerged = mergeRestErrors(mergedRestErrors, from);

        for(Map.Entry<StatusCode, RestError<U, ? extends Translatable>> entry: fromMerged.entrySet()) {
            to.put(entry.getKey(), dispatchAppFactory.restErrorHandler(entry.getValue()));
        }
        return to;
    }


    /**
     * Merges two maps of rest error targets (dispatch errors) with the preference to the right when a collision occurs.
     *
     * @param left a map of rest errors
     * @param right a map of rest errors
     * @return a merged map of RestErrorTarget.
     */
    protected Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> mergeDispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> left, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> right) {
        Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> to = Stream.of(left, right)
                .flatMap(map -> map.entrySet().stream())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (v1, v2) -> v2
                        )
                );
        return to;
    }
}
