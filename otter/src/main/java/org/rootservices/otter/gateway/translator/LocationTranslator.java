package org.rootservices.otter.gateway.translator;


import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.target.Target;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.factory.BetweenFactory;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.translatable.Translatable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationTranslator<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private BetweenFactory<S, U, P> betweenFactory;

    public LocationTranslator(BetweenFactory<S, U, P> betweenFactory) {
        this.betweenFactory = betweenFactory;
    }

    public Map<Method, Location> to(Target<S, U, P> from) {
        Map<Method, Location> to = new HashMap<>();

        for(Method method: from.getMethods()) {

            Betweens<S, U, P> betweens = betweenFactory.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }

            Location location = new LocationBuilder<S, U, P>()
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

    public Route<S, U, P> toRoute(ErrorTarget<S, U, P> from) {
        return new RouteBuilder<S, U, P>()
                .resource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }
}
