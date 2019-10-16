package org.rootservices.otter.gateway.translator;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.factory.BetweenFlyweight;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.translatable.Translatable;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationTranslator<S extends DefaultSession, U extends DefaultUser> {
    private BetweenFlyweight<S, U> betweenFlyweight;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;
    private Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors;

    public LocationTranslator(BetweenFlyweight<S, U> betweenFlyweight, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors, Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors) {
        this.betweenFlyweight = betweenFlyweight;
        this.errorResources = errorResources;
        this.dispatchErrors = dispatchErrors;
        this.defaultDispatchErrors = defaultDispatchErrors;
    }

    public Map<Method, Location> to(Target<S, U> from) {
        Map<Method, Location> to = new HashMap<>();

        Map<StatusCode, ErrorTarget<S, U>> mergedDispatchErrors = mergeDispatchErrors(defaultDispatchErrors, dispatchErrors);

        for(Method method: from.getMethods()) {

            Betweens<S, U> betweens = betweenFlyweight.make(method, from.getLabels());

            List<MimeType> contentTypes = from.getContentTypes().get(method);
            if (contentTypes == null) {
                contentTypes = new ArrayList<>();
            }
            List<MimeType> accepts = from.getAccepts().get(method);
            if (accepts == null) {
                accepts = new ArrayList<>();
            }

            Map<StatusCode, ErrorTarget<S, U>> dispatchErrors = mergeDispatchErrors(mergedDispatchErrors, from.getErrorTargets());


            Location location = new LocationBuilder<S, U>()
                .path(from.getRegex())
                .contentTypes(contentTypes)
                .accepts(accepts)
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
                // these are used in ErrorRouteRunnerFactory via Engine.
                .errorRouteRunners(
                        dispatchErrors
                            .entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> toRoute(e.getValue())
                            ))
                )
                // these are used in RouteRun
                .errorResources(
                        this.mergeErrorResources(errorResources, from.getErrorResources())
                )
                .build();

            to.put(method, location);
        }
        return to;
    }

    protected Map<StatusCode, Resource<S, U>> mergeErrorResources(Map<StatusCode, Resource<S, U>> left, Map<StatusCode, Resource<S, U>> right) {

        Map<StatusCode, Resource<S, U>> to = Stream.of(left, right)
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

    protected Route<S, U> toRoute(ErrorTarget<S, U> from) {
        return new RouteBuilder<S, U>()
                .resource(from.getResource())
                .before(from.getBefore())
                .after(from.getAfter())
                .build();
    }

    /**
     * Merges two maps of error targets (dispatch errors) with the preference to the right when a collision occurs.
     *
     * @param left a map of rest errors
     * @param right a map of rest errors
     * @return a merged map of ErrorTarget.
     */
    protected Map<StatusCode, ErrorTarget<S, U>> mergeDispatchErrors(Map<StatusCode, ErrorTarget<S, U>> left, Map<StatusCode, ErrorTarget<S, U>> right) {
        Map<StatusCode, ErrorTarget<S, U>> to = Stream.of(left, right)
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
