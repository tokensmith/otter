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

    public RestLocationTranslator(RestBetweenFlyweight<U> restBetweenFlyweight, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
        this.restBetweenFlyweight = restBetweenFlyweight;
        this.restErrors = restErrors;
    }

    public Map<Method, Location> to(RestTarget<U, P> from) {
        Map<Method, Location> to = new HashMap<>();

        for(Method method: from.getMethods()) {

            RestBetweens<U> betweens = restBetweenFlyweight.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }

            // merge group reset errors with the location's rest errors.
            Map<StatusCode, RestError<U, ? extends Translatable>> mergedRestErrors = new HashMap<>(restErrors);

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
                    .errorRouteRunners(
                            from.getErrorTargets()
                                    .entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> toRoute(e.getValue())
                                    ))
                    )
                    .restErrorResources(
                        this.mergeRestErrors(restErrors, from.getRestErrors())
                    )
                    .build();

            to.put(method, location);
        }
        return to;
    }

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

    public RestRoute<U, P> toRoute(RestErrorTarget<U, P> from) {
        return new RestRouteBuilder<U, P>()
                .restResource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }
}
