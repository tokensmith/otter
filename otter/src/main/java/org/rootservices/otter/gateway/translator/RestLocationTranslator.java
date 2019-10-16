package org.rootservices.otter.gateway.translator;



import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.RestErrorHandler;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.gateway.entity.rest.RestTarget;
import org.rootservices.otter.router.builder.RestLocationBuilder;
import org.rootservices.otter.dispatch.config.DispatchAppFactory;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.RestRoute;
import org.rootservices.otter.router.factory.RestBetweenFlyweight;
import org.rootservices.otter.security.builder.entity.RestBetweens;
import org.rootservices.otter.translatable.Translatable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestLocationTranslator<U extends DefaultUser, P> {
    private static DispatchAppFactory dispatchAppFactory = new DispatchAppFactory();

    private RestBetweenFlyweight<U> restBetweenFlyweight;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;

    // defaults if not provided, 400
    private Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors;

    // dispatch errors if not provided 415
    private Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors;

    // dispatch error defaults if not provided 415
    private Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> defaultDispatchErrors;

    public RestLocationTranslator(RestBetweenFlyweight<U> restBetweenFlyweight, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> defaultDispatchErrors) {
        this.restBetweenFlyweight = restBetweenFlyweight;
        this.restErrors = restErrors;
        this.defaultErrors = defaultErrors;
        this.dispatchErrors = dispatchErrors;
        this.defaultDispatchErrors = defaultDispatchErrors;
    }

    public Map<Method, Location> to(RestTarget<U, P> from) {
        Map<Method, Location> to = new HashMap<>();

        Map<StatusCode, RestError<U, ? extends Translatable>> mergedRestErrors = mergeRestErrors(defaultErrors, restErrors);
        Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> mergedDispatchErrors = mergeDispatchErrors(defaultDispatchErrors, dispatchErrors);

        for(Method method: from.getMethods()) {

            RestBetweens<U> betweens = restBetweenFlyweight.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }

            List<MimeType> accepts = from.getAccepts().get(method);
            if (accepts == null) {
                accepts = new ArrayList<>();
            }

            mergedRestErrors = mergeRestErrors(mergedRestErrors, from.getRestErrors());
            Map<StatusCode, RestErrorHandler<U>> errorHandlers = toErrorHandlers(mergedRestErrors);

            RestLocationBuilder<U, P> locationBuilder = new RestLocationBuilder<U, P>()
                    .path(from.getRegex())
                    .contentTypes(contentTypes)
                    .accepts(accepts)
                    .restResource(from.getRestResource())
                    .payload(from.getPayload())
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

            // merge dispatch errors
            Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> dispatchErrors = mergeDispatchErrors(mergedDispatchErrors, from.getErrorTargets());

            // add the error routes to be used in engine.
            for(Map.Entry<StatusCode, RestErrorTarget<U, ? extends Translatable>> entry: dispatchErrors.entrySet()) {
                RestRoute<U, ? extends Translatable> restRoute = dispatchAppFactory.makeRestRoute(entry.getValue());

                RouteRunner restRouteRunner = dispatchAppFactory.makeJsonDispatchErrorRouteRun(restRoute, entry.getValue().getPayload());
                locationBuilder = locationBuilder.errorRouteRunner(
                        entry.getKey(),
                        restRouteRunner
                );
            }

            to.put(method, locationBuilder.build());
        }
        return to;
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
        for(Map.Entry<StatusCode, RestError<U, ? extends Translatable>> entry: from.entrySet()) {
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
    protected Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> mergeDispatchErrors(Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> left, Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> right) {
        Map<StatusCode, RestErrorTarget<U, ? extends Translatable>> to = Stream.of(left, right)
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
