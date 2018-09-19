package org.rootservices.otter.gateway.translator;


import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.factory.BetweenFactory;
import org.rootservices.otter.security.builder.entity.Betweens;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationTranslator<S, U> {
    private BetweenFactory<S, U> betweenFactory;

    public LocationTranslator(BetweenFactory<S, U> betweenFactory) {
        this.betweenFactory = betweenFactory;
    }

    public Map<Method, Location<S, U>> to(Target<S, U> from) {
        Map<Method, Location<S, U>> to = new HashMap<>();

        for(Method method: from.getMethods()) {

            Betweens<S, U> betweens = betweenFactory.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }

            Location<S, U> location = new LocationBuilder<S, U>()
                .path(from.getRegex())
                .contentTypes(contentTypes)
                .resource(from.getResource())
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
                .errorRoutes(
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

    public Route<S, U> toRoute(ErrorTarget<S, U> from) {
        return new RouteBuilder<S, U>()
                .resource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }
}
