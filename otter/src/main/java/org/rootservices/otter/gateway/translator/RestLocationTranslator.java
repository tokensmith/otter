package org.rootservices.otter.gateway.translator;

import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.RestErrorTarget;
import org.rootservices.otter.gateway.entity.RestTarget;
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

public class RestLocationTranslator<U extends DefaultUser, P extends Translatable> {
    private RestBetweenFlyweight<U, P> restBetweenFlyweight;

    public RestLocationTranslator(RestBetweenFlyweight<U, P> restBetweenFlyweight) {
        this.restBetweenFlyweight = restBetweenFlyweight;
    }

    public Map<Method, Location> to(RestTarget<U, P> from) {
        Map<Method, Location> to = new HashMap<>();

        for(Method method: from.getMethods()) {

            RestBetweens<U, P> betweens = restBetweenFlyweight.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }

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
                    .build();

            to.put(method, location);
        }
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
