package org.rootservices.otter.gateway.translator;


import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.gateway.entity.rest.RestTarget;
import org.rootservices.otter.router.builder.RestLocationBuilder;
import org.rootservices.otter.router.builder.RestRouteBuilder;
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
    private RestBetweenFlyweight<U> restBetweenFlyweight;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    // defaults if not provided, 400, 415
    private Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors;

    // 113: left off here need to add default bad request and unsupported media type resource.
    public RestLocationTranslator(RestBetweenFlyweight<U> restBetweenFlyweight, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors) {
        this.restBetweenFlyweight = restBetweenFlyweight;
        this.restErrors = restErrors;
        this.defaultErrors = defaultErrors;
    }

    public Map<Method, Location> to(RestTarget<U, P> from) {
        Map<Method, Location> to = new HashMap<>();

        for(Method method: from.getMethods()) {

            RestBetweens<U> betweens = restBetweenFlyweight.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }

            Map<StatusCode, RestError<U, ? extends Translatable>> mergedRestErrors = mergeRestErrors(restErrors, from.getRestErrors());
            mergedRestErrors = mergeRestErrors(defaultErrors, mergedRestErrors);

            Location location = new RestLocationBuilder<U, P>()
                    .path(from.getRegex())
                    .contentTypes(contentTypes)
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
                    // these are used in ErrorRouteRunnerFactory via Engine.
                    .errorRouteRunners(
                            from.getErrorTargets()
                                    .entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> toRoute(e.getValue())
                                    ))
                    )
                    // these are used in JsonRouteRun
                    .restErrorResources(mergedRestErrors)
                    .build();




            to.put(method, location);
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

    protected RestRoute<U, P> toRoute(RestErrorTarget<U, P> from) {
        return new RestRouteBuilder<U, P>()
                .restResource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }
}
